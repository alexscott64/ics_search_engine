# Sebastian Val, Alex Scott
# Creates a class which counts a frequency, tf score, and tfidf score
# given a term from a title. This will get stored in a title_tf table in SQL
import math

class TitleTF:
    term = None
    docid = None
    freq = None
    tf = None

    def __init__(self, term, docid, freq):
        self.term = term
        self.docid = docid
        self.freq = freq
        self.tf = self.calc_tf()

    def __str__(self):
        "Title term: %s DocID: %s Freq: %d TF: %f" % (self.term, self.docid, self.freq, self.tf)

    def calc_tf(self):
        return 1 + math.log10(self.freq)
