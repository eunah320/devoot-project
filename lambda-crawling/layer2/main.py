import json
import logging
from tempfile import mkdtemp

from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.chrome.service import Service

import sys
import os
from openai import OpenAI
import pymysql
import asyncio
import hashlib
from bs4 import BeautifulSoup
import re
import boto3

logger = logging.getLogger()
logger.setLevel(logging.INFO)

source_name = None
target_url = None
db_user = os.getenv("db_user")
db_password = os.getenv("db_password")
db_host = os.getenv("db_host")
db_name = os.getenv("db_name")
sqs = boto3.client('sqs')
s3 = boto3.client('s3')
queue_url = os.environ['SQS_QUEUE_URL']

cursor = None # db connection
conn = None
driver = None
body = None
receipt_handle = None

# dynamic variables
name_sel = "head > title"
lecturer_sel = ""
image_sel = ""
image_attr = ""
price_sel = ""
curriculum_sel = ""
name_start_cut = ""
name_end_cut = ""

client = OpenAI(
    api_key=os.getenv("api_key")
)


##########################INITIALIZE FUNCTIONS#####################
def initialize_db_connection():
    global cursor, conn
    try:
        conn = pymysql.connect(
            host=db_host,
            user=db_user,
            password=db_password,
            database=db_name,
            port=3306,
            cursorclass=pymysql.cursors.DictCursor
        )
        cursor = conn.cursor()
    except pymysql.MySQLError as e:
        print(f"Error Connecting to MySQL/MariaDB: {e}")
        sys.exit(1)

def initialise_driver():
    chrome_options = ChromeOptions()
    chrome_options.add_argument("--headless=new")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--disable-dev-tools")
    chrome_options.add_argument("--no-zygote")
    chrome_options.add_argument("--single-process")
    chrome_options.add_argument(f"--user-data-dir={mkdtemp()}")
    chrome_options.add_argument(f"--data-path={mkdtemp()}")
    chrome_options.add_argument(f"--disk-cache-dir={mkdtemp()}")
    chrome_options.add_argument("--remote-debugging-pipe")
    chrome_options.add_argument("--verbose")
    chrome_options.add_argument("--log-path=/tmp")
    chrome_options.add_argument("--start-maximized")
    chrome_options.binary_location = "/opt/chrome/chrome-linux64/chrome"

    service = Service(
        executable_path="/opt/chrome-driver/chromedriver-linux64/chromedriver",
        service_log_path="/tmp/chromedriver.log"
    )

    driver = webdriver.Chrome(
        service=service,
        options=chrome_options
    )

    # driver.set_window_size(1800, 1000)

    return driver

def initialize_dynamic_variables():
    global name_sel, lecturer_sel, image_sel, image_attr, price_sel, name_start_cut, name_end_cut, curriculum_sel
    if source_name == "inflearn":
        name_start_cut = 0
        name_end_cut = 6
        lecturer_sel = "#__next > div.css-hwqsu1.mantine-1fr50if > div.css-c31z9o.mantine-1jggmkl > div > section.css-1h0915r.mantine-1avyp1d > div > div > aside > div > div.css-1ncs04q.mantine-4zwhfa > div > div:nth-child(1) > div > a"
        image_attr = "og:image"
        image_sel = f"head > meta[property=\"{image_attr}\"]"
        price_sel = "#__next > div.css-hwqsu1.mantine-1fr50if > div.css-c31z9o.mantine-1jggmkl > div > section.css-1h0915r.mantine-1avyp1d > div > div > aside > div > div.mantine-Stack-root.css-y48vtv.mantine-14kdbyl"
        curriculum_sel = "#__next > div.css-hwqsu1.mantine-1fr50if > div.css-c31z9o.mantine-1jggmkl > div > section.css-1h0915r.mantine-1avyp1d > div > div > section > div > div:nth-child(6) > div.mantine-1avyp1d"
    elif source_name == "goorm":
        name_start_cut = 0
        name_end_cut = 8
        lecturer_sel = "div._1Xj1gq > div.KuR9pp.d-flex.align-items-center.justify-content-center > div > div"
        image_attr = "og:image"
        image_sel = f"head > meta[property=\"{image_attr}\"]"
        price_sel = "#lectureSummaryCover > div > div._3eZvqh > div:nth-child(1) > div"
        curriculum_sel = "#tabContents_2 > div._1Mxg04._3dohOQ.card"
    elif source_name == "boostcourse":
        name_sel = "head > meta[property=\"og:title\"]"
        name_start_cut = 0
        name_end_cut = 8
        lecturer_sel = "#container > div.lnb > div.inc_sub.default > div > div.inner > div.profile > span.name"
        image_attr = "og:image"
        image_sel = f"head > meta[property=\"{image_attr}\"]"
        price_sel = ""
        curriculum_sel = "#snb > nav > ul.NE\\=a\\:lmn"

def receive_message_from_SQS():
    global source_name, target_url, receipt_handle
    try:
        # Receive a message from the SQS queue
        response = sqs.receive_message(
            QueueUrl=queue_url,
            AttributeNames=['All'],
            MaxNumberOfMessages=1,
            WaitTimeSeconds=0
        )

        if 'Messages' in response:
            # Extract the message body from the response
            message = response['Messages'][0]
            receipt_handle = message['ReceiptHandle']

            # Parse the JSON message
            message_body = json.loads(message['Body'])

            # Extract the 'domain' and 'target_url' from the JSON data
            source_name = message_body.get('data', {}).get('domain', None)
            target_url = message_body.get('data', {}).get('target_url', None)

            print(f"Domain: {source_name}")
            print(f"Target URL: {target_url}")

            # Delete the message from the queue to prevent it from being processed again
            sqs.delete_message(
                QueueUrl=queue_url,
                ReceiptHandle=receipt_handle
            )
        else:
            print("No messages available in the queue.")
    except Exception as e:
        print(f"Error receiving message: {str(e)}")


def delete_message_from_SQS():
    global queue_url, receipt_handle
    sqs.delete_message(
        QueueUrl=queue_url,
        ReceiptHandle=receipt_handle
    )

#########################EXTRACT FUNCTIONS##################
# 큰따옴표, 작은따옴표 DB 삽입을 위한 문자열 변환
def replaceQuotes(string):
    return string.replace("\\", "\\\\").replace("\"", "\\\"")


def replace_single_quotes(text):
    # (?<!\\) : 역슬래시(\)가 없는 작은따옴표만 찾음 (Lookbehind 사용)
    result = re.sub(r"(?<!\\)'", r"\\'", text)
    return result


def get_name(soup):
    try:
        if source_name == "boostcourse":
            return replace_single_quotes(replaceQuotes(str(soup.select_one(name_sel)['content'])))
        data = str(soup.select_one(name_sel).get_text())
        return replace_single_quotes(replaceQuotes(data[name_start_cut:-name_end_cut].strip()))
    except Exception as e:
        report_error("get name failure.", str(e))


def get_lecturer(soup):
    try:
        return str(soup.select_one(lecturer_sel).get_text())
    except Exception as e:
        report_error("get lecturer failure.", str(e))


def get_image(soup):
    try:
        return str(soup.select_one(image_sel)['content'])
    except Exception as e:
        report_error("get image_url failure.", str(e))


# 가격 정보는 openai .활용
def get_price_curriculum_category_tags(soup, name):
    data = ""
    try:
        if price_sel == "":
            price_txt = "가격정보없음. "
        else:
            price_txt = "가격정보: " + str(soup.select_one(price_sel).get_text()) + "\n"
        if curriculum_sel == "":
            curriculum_txt = ""
        else:
            curriculum_txt = "교육과정정보: " + str(soup.select_one(curriculum_sel).get_text())
        data = price_txt + "\n" + curriculum_txt + "\n"
    except Exception as e:
        report_error("crawling price, curriculum text error.", str(e))
    metadata = f"강의명 : {name}" + """
        내가 제공해주는 텍스트를 분석해서 다음과 같이 강의 정보를 정리해 줘.
        정보 이외에 어떠한 부가 설명이나 백틱 기호도 추가할 필요 없어
        1. 첫 번째 줄에는 가격 정보를 표기한다.
         - <원가>:<할인된 가격>의 형태
         - 원가와 할인된 가격은 그대로 쓰여 있으니 30% 할인과 같은 문구가 있더라도 직접 곱할 필요가 없음.
         - 가격 정보가 1개밖에 없다면 할인 중이 아니므로 가격과 할인된 가격을 동일하게 작성
         - 콤마(,)나 원화(\\), 공백은 제거
         - 가격 정보가 없거나 무료인 경우 0으로 채운다.
        예시)
        23700:20000
        3000:3000
        16000:0
        0:0

        2. 두 번째 줄에는 교육 과정을 json 형태로 줄 바꿈 없이 표기한다. 단, 다음 규칙을 엄격하게 지켜 수정한 값을 표기한다.
         - json의 key,value 내용 안에 있는 큰 따옴표의( " ) 경우 바로 앞에 역슬래시 1개를 추가한다.
         - 1씩 증가하는 key에는 value로 객체가 들어간다. 각 객체에는 majorTitle(대과목 이름), subLectures(소과목)이라는 key를 가진다.
         - 대과목의 러닝 타임이 존재하더라도 따로 작성하지 않는다.
         - subLectures 키의 value는 소과목 정보 객체들이 담긴 배열이다. 각각 title(소과목 이름)과 time(소과목 러닝타임)을 가진다. time 정보를 찾을 수 없다면 공백 ""으로 작성한다.
        예시)
        {"1":{"majorTitle":"섹션 1. 파이썬 시작하기","subLectures":[{"title":"입문자를 위한 파이썬 공부 비법","time":"20:03"},{"title":"프로그래밍 이론으로 맛보기 1","time":"23:20"}, ... ]}, "2": ...}

        3. 세 번째 줄에는 현재 강의명과 가장 관련이 높은 카테고리를 다음 중 1개를 골라 작성한다.
        ['백엔드','프론트엔드','모바일 앱 개발','프로그래밍 언어','알고리즘/자료구조','데이터베이스','데브옵스/인프라','게임 개발','데이터 사이언스', '인공지능','보안/네트워크','하드웨어','디자인/아트','기타']
        예시)
        프로그래밍 언어 

        4. 네 번째 줄에는 현재 강의명과 가장 관련이 높은 키워드를 3개 선택해야 한다.
         - 첫 번째 키워드는 다음 중 1개를 선택해야 한다.
         ['HTML','JavaScript','Java','Ruby','TypeScript','Swift','Kotlin','Python','C','C++','C#','Go','Scals','Dart',
         'MySQL','Oracle','컴퓨터 비전','자연어 처리','시스템/운영체제','클라우드','블록체인','컴퓨터 구조','Markdown','데이터 분석','데이터 엔지니어링',
         '딥러닝/머신러닝','임베디드/IoT','반도체','로봇공학','UX/UI']
         - 2, 3번째 키워드는 위에 없는 키워드를 임의로 선택해야 한다. 웬만하면 기술 스택 위주로 선택한다.
         - 각 키워드는 콤마로 구분한다.
         예시)
         MySQL,MyBatis,데이터베이스
    """
    response = chat_with_gpt(metadata, data) + "\n"
    print(response)
    try:
        results = response.strip().split("\n")
        price = results[0].split(":")
        curriculum = results[1]
        category = results[2]
        tags = results[3]
        return price, replace_single_quotes(curriculum), category, tags
    except Exception as e:
        report_error("api response parsing error.", str(e))


def get_hash():
    try:
        return hashlib.sha256(target_url.encode()).hexdigest()
    except Exception as e:
        report_error("hashing error.", str(e))

####################CRAWLING FUNCTIONS#######################
def chat_with_gpt(metaprompt, prompt):
    try:
        # ChatGPT API 호출
        res = client.chat.completions.create(
            model="gpt-4o-mini",  # 모델명 지정
            messages=[
                {"role": "system", "content": metaprompt},
                {"role": "user", "content": prompt}
            ]
        )
        # 모델의 응답 추출
        reply = res.choices[0].message.content
        return reply
    except Exception as e:
        report_error("chat_with_gpt failure.", str(e))

def report_error(msg, detail):
    try:
        msg = f"[Lambda-l2]: {msg}\n target_url: {target_url}\ndetail: {detail}"
        sql = f"INSERT INTO `errorlog` (`content`) VALUE(`{msg}`)"
        print(f"[ERROR] {msg}")
        print(f"detail: {detail}")
        cursor.execute(sql)
        conn.commit()
    except Exception as e:
        print("[ERROR] ErrorLog write failure.")
        print(str(e))
    driver.quit()
    cursor.close()
    conn.close()
    sys.exit(1)


def insert_lecture(data):
    sql = '''
        INSERT INTO `lecture` 
        (`category`, `name`, `lecturer`, `imageUrl`, `originalPrice`, `currentPrice`, 
         `curriculum`, `sourceUrl`, `sourceName`, `tags`, `hash`) 
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    '''
    print(f"SQL: {sql}")

    try:
        with conn.cursor() as cursor:
            cursor.executemany(sql, data)  # Insert multiple rows
            conn.commit()
        print("[SUCCESS] Lecture inserted.")
    except pymysql.MySQLError as e:
        conn.rollback()  # Rollback in case of error
        report_error("Lecture insertion error", str(e))


async def parser():
    global body

    driver.get(target_url)
    await asyncio.sleep(1)
    soup = BeautifulSoup(driver.page_source, 'html.parser')
    name = get_name(soup)
    image_url = get_image(soup)
    lecturer = get_lecturer(soup)
    price, curriculum, category, tags = get_price_curriculum_category_tags(soup, name)
    originprice = price[0]
    currentprice = price[1]
    hash_value = get_hash()

    lecture_data = {
        "category": category,
        "name": name,
        "lecturer": lecturer,
        "image_url": image_url,
        "original_price": originprice,
        "current_price": currentprice,
        "curriculum": curriculum,
        "target_url": target_url,
        "source_name": source_name,
        "tags": tags,
        "hash": hash_value
    }

    # Pretty-print the JSON
    print(json.dumps(lecture_data, indent=4, ensure_ascii=False))
    body = json.dumps(lecture_data, indent=4, ensure_ascii=False)

    insert_lecture([[category, name, lecturer, image_url, originprice, currentprice, curriculum, target_url,
                     source_name, tags, hash_value]])


async def main():
    await parser()
    try:
        driver.quit()
        cursor.close()
        conn.close()
    except Exception:
        print("No connection to close.(Not an critical error)")


###############################################
def lambda_handler(event, context):
    global cursor, driver

    receive_message_from_SQS()
    initialize_dynamic_variables()
    initialize_db_connection()
    driver = initialise_driver()

    asyncio.run(main())

    delete_message_from_SQS()

    # Build the response with the search result titles
    response = {
        "statusCode": 200,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": body
    }

    return response
