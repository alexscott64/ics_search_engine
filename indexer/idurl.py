import math

class IdUrl:
    docid = None
    url = None

    def __init__(self, docid, url):
        self.docid = docid
        self.url = url

    def __str__(self):
        return "Doc ID: %s URL: %s" % (self.docid, self.url)
