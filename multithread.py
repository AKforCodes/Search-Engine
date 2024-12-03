from threading import Thread

def crawl_threaded(urls, depth):
    threads = []
    for url in urls:
        thread = Thread(target=crawl, args=(url, depth))
        thread.start()
        threads.append(thread)
    for thread in threads:
        thread.join()
