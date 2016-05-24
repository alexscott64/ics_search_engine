# Map a DocID -> the doc id's <title></title> tag content
# under the id_title SQL table

class IdTitle:
    docid = None
    title = None

    def __init__(self, docid, title):
        self.docid = docid
        self.title = title

    def __str__(self):
        return "Doc ID: %s Title: %s" % (self.docid, self.title)
