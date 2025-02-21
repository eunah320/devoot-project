import json
import os
import sys
from datetime import datetime
from typing import Dict

import boto3
import pymysql
import uuid

def get_connection():
    """
    Get a MySQL connection using pymysql.
    1) Try importing config.py (local testing).
    2) If not found, use environment variables (Lambda usage).
    """
    try:
        try:
            from config import DB_CONFIG
            conn = pymysql.connect(**DB_CONFIG)
            print("[SUCCESS] Database connection established via local config.")
        except ImportError:
            conn = pymysql.connect(
                host=os.getenv("db_host"),
                user=os.getenv("db_user"),
                password=os.getenv("db_password"),
                database=os.getenv("db_name"),
                port=int(os.environ.get('DB_PORT', 3306)),
                charset='utf8mb4',
                cursorclass=pymysql.cursors.DictCursor
            )
            print("[SUCCESS] Database connection established via environment variables.")
        return conn
    except pymysql.MySQLError as e:
        print(f"[ERROR] Database connection failed: {str(e)}")
        sys.exit(1)

class LectureDB:
    """
    Class for interacting with the lecture table in the database.
    """

    def __init__(self):
        self.get_connection = get_connection

    def fetch_lecture_prices(self) -> Dict[str, Dict[str, int]]:
        conn = self.get_connection()
        cursor = conn.cursor()

        try:
            print("[INFO] Fetching existing lecture prices from DB")
            cursor.execute("""
                SELECT hash, originalPrice, currentPrice
                FROM lecture
                WHERE hash IS NOT NULL
            """)

            results = {}
            for row in cursor.fetchall():
                hash_value, orig_price, curr_price = row
                if hash_value:
                    results[hash_value] = {
                        "originalPrice": orig_price or 0,
                        "currentPrice": curr_price or 0
                    }

            print(f"[SUCCESS] Fetched {len(results)} lecture records.")
            return results

        except pymysql.MySQLError as e:
            print(f"[ERROR] Failed to fetch lecture prices: {str(e)}")
            return {}
        finally:
            cursor.close()
            conn.close()
            print("[INFO] DB connection closed after fetch.")

    def update_lecture_price(self, hash_value: str, new_original_price: int, new_current_price: int) -> bool:
        """
        Update prices in DB if there's a difference from existing.
        Returns True if update was successful (row actually changed), else False.
        """
        conn = None
        cursor = None

        try:
            conn = self.get_connection()
            cursor = conn.cursor()

            cursor.execute("""
                UPDATE lecturetest
                SET originalPrice = %s,
                    currentPrice = %s,
                    updatedAt = NOW()
                WHERE hash = %s
                  AND (originalPrice != %s OR currentPrice != %s)
            """, (new_original_price, new_current_price, hash_value,
                  new_original_price, new_current_price))

            # Check how many rows were actually updated
            rows_updated = cursor.rowcount

            if rows_updated > 0:
                conn.commit()
                print(f"[SUCCESS] DB update successful for hash={hash_value}")
                return True
            else:
                print(f"[INFO] No DB update needed (price unchanged). hash={hash_value}")
                return False

        except pymysql.MySQLError as e:
            print(f"[ERROR] DB update failed for hash={hash_value}: {str(e)}")
            if conn:
                conn.rollback()
            return False
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
                print("[INFO] DB connection closed after update.")


def detect_price_changes(db_orig: int, db_curr: int, site_orig: int, site_curr: int) -> list:
    """
    Compare DB prices vs. crawled site prices to detect changes.
    Return a list describing the change events:
    - "Original price changed"
    - "Discount started"
    - "Discount ended"
    - "Discount price changed"
    """
    changes = []

    if db_orig != site_orig:
        changes.append("Original price changed")

    db_discounted = (db_orig != db_curr)
    site_discounted = (site_orig != site_curr)

    if not db_discounted and site_discounted:
        changes.append("Discount started")
    elif db_discounted and not site_discounted:
        changes.append("Discount ended")
    elif db_discounted and site_discounted and (db_curr != site_curr):
        changes.append("Discount price changed")

    return changes


def send_new_lecture_to_sqs(domain: str, detail_url: str):
    """
    Simulate sending a new lecture message to SQS.
    Actual SQS code is commented out.
    """
    try:
        sqs = boto3.client('sqs')
        queue_url = os.environ['SQS_QUEUE_URL']

        message = {
            'timestamp': datetime.utcnow().isoformat(),
            'data': {
                'domain': domain,
                'target_url': detail_url
            }
        }

        response = sqs.send_message(
            QueueUrl=queue_url,
            MessageBody=json.dumps(message),
            MessageGroupId='default',  # Required for FIFO queues
            MessageDeduplicationId=str(uuid.uuid4())
        )
        print(f"Message sent. MessageId: {response['MessageId']}")
        return response
    except Exception as e:
        print(f"[ERROR] Failed to send message to SQS: {str(e)}")
        raise

def process_crawled_prices(crawled_lectures: list, domain: str):
    """
    Compare crawled lectures with DB, update DB if needed, or treat as new.
    Print summary of how many were updated, new, or unchanged.
    """
    db = LectureDB()
    existing_prices = db.fetch_lecture_prices()

    total_updated = 0
    total_new = 0
    total_unchanged = 0

    for lecture in crawled_lectures:
        hash_value = lecture["hash"]
        site_original_price = lecture["originalPrice"]
        site_current_price = lecture["currentPrice"]
        detail_url = lecture["detailUrl"]

        # Case 1: Already exists in DB
        if hash_value in existing_prices:
            db_orig = existing_prices[hash_value]["originalPrice"]
            db_curr = existing_prices[hash_value]["currentPrice"]

            changes = detect_price_changes(db_orig, db_curr, site_original_price, site_current_price)

            if changes:
                updated = db.update_lecture_price(hash_value, site_original_price, site_current_price)
                if updated:
                    print(f"[UPDATE] Price changed. url={detail_url}")
                    print(f"         Changes: {changes}")
                    print(f"         Old => original={db_orig}, current={db_curr}")
                    print(f"         New => original={site_original_price}, current={site_current_price}")
                    total_updated += 1
                else:
                    # Update was attempted but no rows changed => possibly concurrency or same price
                    print(f"[ERROR] DB update attempt failed. url={detail_url}")
            else:
                # No price changes
                print(f"[INFO] No price change detected. url={detail_url}")
                total_unchanged += 1

        # Case 2: New Lecture
        else:
            print(f"[INFO] New Lecture found. url={lecture['detailUrl']}")
            send_new_lecture_to_sqs(domain, lecture['detailUrl'])
            total_new += 1

    print("[SUMMARY] Price processing completed.")
    print(f"          Updated={total_updated}, New={total_new}, Unchanged={total_unchanged}")
