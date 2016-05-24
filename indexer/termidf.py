# Authors: Alex Scott and Sebastian Val

import math

class TermIDF:
    term = None
    freq = None
    idf = None   
    
    def __init__(self, term, freq, corp_size):
        self.term = term
        self.freq = freq
        self.idf = self.calc_idf(corp_size)
        
    def __str__(self):
        return "Term: %s Freq: %d IDF: %f" % (self.term, self.freq, self.idf)
        
    def calc_idf(self, corp_size):
        return math.log10(1.0 + (float(corp_size) / float(self.freq)))