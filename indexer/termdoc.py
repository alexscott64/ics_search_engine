# Authors: Alex Scott and Sebastian Val

import math

class TermDoc:
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
        return "Term: %s DocID: %s Freq: %d TF: %f" % (self.term, self.docid, self.freq, self.tf)
        
    def calc_tf(self):
        return 1 + math.log10(self.freq)