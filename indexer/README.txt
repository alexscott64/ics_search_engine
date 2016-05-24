For our TF-IDF we make use of the following definitions (partially taken from wikipedia) :
Index field tf.freq for one term within one document is the number of times that term appears in the document
Index field tf.tf is "Weighted Term Frequency" for one term within one document) is 1+log(tf.freq)

Index field idf.freq for one term within corpus is the number of documents in the corpus which contain the term
Index field idf.idf "Inverse Document Frequency" for one term within one corpus is log(1 + corpus_size / idf.freq)

Index field tf.tfidf is tf.tf(term,doc) * idf.idf(term,corpus)