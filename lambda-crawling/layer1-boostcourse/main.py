import hashlib
import logging
import re
import time
from tempfile import mkdtemp
from typing import List, Dict, Any

from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.chrome.service import Service

from mariadb_connection import process_crawled_prices


# CRAWLING SETTINGS
DOMAIN = "boostcourse"
SEARCH_URL = "https://www.boostcourse.org/opencourse?page={page}"
BASE_URL = "https://www.boostcourse.org"
CARD_SELECTOR = "div.course_area ul.course_list li"
URL_SELECTOR = "a"

body = None

##########################INITIALIZE FUNCTIONS#####################

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

    return driver

########################EXTRACT FUNCTIONS##################
def extract_price(text: str) -> int:
    """
    Extract numeric price from text.
    1) If numeric format found, convert to int.
    2) Otherwise, raise ValueError (treated as invalid).
    """
    text = text.strip()
    match = re.search(r"[\d,]+", text)
    if match:
        return int(match.group().replace(",", ""))

    raise ValueError(f"Invalid price text: '{text}'")

def remove_query_params(url: str) -> str:
    """
    Remove query parameters from a URL.
    """
    return url.split('?')[0]

####################CRAWLING FUNCTIONS#######################
def parse_lecture_card(card: BeautifulSoup) -> Dict[str, Any] or None:
    """
    Parse a single lecture card to extract detail URL, original/current price, etc.
    If parsing fails (e.g. weird price), return None so DB won't be updated.
    Goorm specific: If current_price is 0 but original_price exists, use original_price as current_price
    """
    link_tag = card.select_one(URL_SELECTOR)
    if not link_tag:
        print("[WARNING] Skipping lecture: no link tag (<a>) found")
        return None

    detail_url = BASE_URL + remove_query_params(link_tag.get("href", ""))
    if not detail_url:
        print("[WARNING] Skipping lecture: empty detail URL")
        return None

    detail_url = BASE_URL + remove_query_params(link_tag.get("href", "")) + "/home"

    hash_value = hashlib.sha256(detail_url.encode("utf-8")).hexdigest()

    current_price = 0
    original_price = 0

    print(f"[SUCCESS] Card parsed. url={detail_url}, original_price={original_price}, current_price={current_price}")
    return {
        "hash": hash_value,
        "originalPrice": original_price,
        "currentPrice": current_price,
        "detailUrl": detail_url,
    }

def crawl_lectures() -> List[Dict[str, Any]]:
    """
    Crawl lectures from Goorm.
    Returns a list of lecture dictionaries.
    """
    results = []
    page = 1

    try:
        while True:
            if page == 2: break
            url = SEARCH_URL.format(page=page)
            print(f"[INFO] Crawling page {page}: {url}")

            driver.get(url)
            time.sleep(3)

            soup = BeautifulSoup(driver.page_source, "html.parser")
            cards = soup.select(CARD_SELECTOR)

            if not cards:
                if page == 1:
                    print("[ERROR] No lectures found on page=1. Possible selector change. Stopping crawl")
                else:
                    print(f"[INFO] No more lectures found at page={page}. Stopping crawl")
                break

            print(f"[INFO] Found {len(cards)} cards on page {page}")

            failed_count = 0
            for idx, card in enumerate(cards, start=1):
                lecture_info = parse_lecture_card(card)
                if lecture_info:
                    results.append(lecture_info)
                else:
                    print(f"[WARNING] Failed to parse card #{idx} on page={page}")
                    failed_count += 1

            if failed_count == len(cards):
                print(f"[ERROR] All cards failed to parse on page={page}. Stopping crawl")
                break

            page += 1

    except Exception as e:
        print(f"[ERROR] Unexpected error during crawling: {str(e)}")
    finally:
        driver.quit()

    print(f"[SUMMARY] Crawling finished. Total lectures parsed: {len(results)}")
    return results


########################################################
def lambda_handler(event, context):
    global cursor, driver

    driver = initialise_driver()

    print("[INFO] Lambda handler started")
    crawled_lectures = crawl_lectures()

    if not crawled_lectures:
        print("[WARNING] No lectures crawled, skipping database processing")
        return {
            "statusCode": 200,
            "body": f"{DOMAIN} crawling completed, no lectures found"
        }

    process_crawled_prices(crawled_lectures, DOMAIN)

    print(f"[SUMMARY] Lambda handler completed. Total={len(crawled_lectures)} lectures processed")
    return {
        "statusCode": 200,
        "body": f"{DOMAIN} crawling completed, total={len(crawled_lectures)}"
    }