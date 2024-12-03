import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin, urlparse
import re
import time

visited_urls = set()

def crawl(url, depth):
    if depth == 0 or url in visited_urls:
        return
    visited_urls.add(url)
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200 and 'text/html' in response.headers['Content-Type']:
            soup = BeautifulSoup(response.content, 'html.parser')
            title = soup.title.string if soup.title else ''
            text = soup.get_text()
            store_document(url, title, text)
            for link in soup.find_all('a', href=True):
                absolute_url = urljoin(url, link['href'])
                if is_valid_url(absolute_url):
                    crawl(absolute_url, depth - 1)
            time.sleep(1)  # Politeness delay
    except Exception as e:
        print(f"Failed to crawl {url}: {e}")

def is_valid_url(url):
    parsed = urlparse(url)
    return parsed.scheme in ('http', 'https')

def store_document(url, title, content):
    conn = get_db_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO documents (url, title, content) VALUES (%s, %s, %s)", (url, title, content))
        conn.commit()
        document_id = cursor.lastrowid
        index_content(document_id, content)
    except mysql.connector.errors.IntegrityError:
        pass  # URL already exists
    finally:
        cursor.close()
        conn.close()

def index_content(document_id, content):
    words = tokenize(content)
    word_freq = {}
    for word in words:
        word_freq[word] = word_freq.get(word, 0) + 1
    conn = get_db_connection()
    cursor = conn.cursor()
    for word, freq in word_freq.items():
        cursor.execute("INSERT INTO words (word) VALUES (%s) ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id)", (word,))
        word_id = cursor.lastrowid
        cursor.execute("INSERT INTO inverted_index (word_id, document_id, frequency) VALUES (%s, %s, %s)", (word_id, document_id, freq))
    conn.commit()
    cursor.close()
    conn.close()

def tokenize(text):
    text = text.lower()
    tokens = re.findall(r'\b\w+\b', text)
    stop_words = set(['and', 'or', 'the', 'is', 'in', 'at', 'of', 'a', 'an'])
    return [word for word in tokens if word not in stop_words]

# Start crawling
crawl('https://www.example.com', depth=2)
