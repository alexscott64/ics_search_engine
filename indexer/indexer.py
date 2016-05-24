# Authors: Alex Scott and Sebastian Val
import os
import json
import re
from stopwords import Stopwords
from collections import defaultdict
from termdoc import TermDoc
from termidf import TermIDF
from idurl import IdUrl
from idtitle import IdTitle
from titletf import TitleTF
from titleidf import TitleIDF
from bs4 import BeautifulSoup
import MySQLdb

user = "cs121"
password = "cs121pass"

def get_cursor():
    try:
        db = MySQLdb.connect("localhost", user, password, "indexer")
        db.autocommit(True)
        db.set_character_set('utf8') # account for title characters
        return db.cursor()
    except:
        print "Failed connecting to DB!"
        raise
    
##################
# OUTBOUND LINKS #
##################
def process_all_outbound(docid, html, cursor):
    links = get_links(html)
    for link in links:
        store_link(docid, link, cursor)    
    
def store_link(docid, link, cursor):
    cursor.execute("INSERT INTO all_outgoing_urls (docid,link) VALUES(%s, %s)", (docid, link))

def process_parent_to_child(cursor):
    cursor.execute("INSERT IGNORE INTO out_link (p_docid, out_docid) SELECT all_outgoing_urls.docid as p_docid, id_url.docid as out_docid "
                   "FROM all_outgoing_urls LEFT JOIN id_url ON all_outgoing_urls.link = id_url.url HAVING out_docid IS NOT NULL ")
                   
            

###########
# TERM TF #
###########

def process_all_termdocs(intext, docid, cursor):
    freqdict = tokenize_text(intext)
    for term, freq in freqdict.iteritems():
        newtermdoc = TermDoc(term, docid, freq)
        store_termdoc(newtermdoc, cursor)

def store_termdoc(termdoc, cursor):
    cursor.execute("INSERT INTO tf (term, docid, freq, tf) VALUES(%s, %s, %s, %s)", (termdoc.term, termdoc.docid, termdoc.freq, termdoc.tf))

def update_termdocs(cursor):
    cursor.execute("SELECT term,docid,tf FROM tf")
    term_records = cursor.fetchall()
    for term_tuple in term_records:
        cursor.execute("SELECT idf FROM idf WHERE term = %s", (term_tuple[0],))
        (idf,) = cursor.fetchone()
        tfidf = term_tuple[2] * idf
        cursor.execute("UPDATE tf SET tfidf = %s WHERE term = %s AND docid = %s", (tfidf, term_tuple[0],term_tuple[1]))

############
# TERM IDF #
############

def store_termidf(termidf, cursor):
    cursor.execute("INSERT INTO idf (term, freq, idf) VALUES(%s, %s, %s)", (termidf.term, termidf.freq, termidf.idf))
        
def process_all_idfs(cursor):
    freqdict = defaultdict(int)
    cursor.execute("SELECT term,docid FROM tf")
    records = cursor.fetchall()
    corp_size = len(records)
    for rec in records:
        freqdict[rec[0]] +=1
    for term in freqdict:
        newtermidf = TermIDF(term, freqdict[term], corp_size)        
        store_termidf(newtermidf, cursor)

############
# TITLE TF #
############

def process_all_titletfs(title, docid, cursor):
    freqdict = tokenize_text(title)
    for term, freq in freqdict.iteritems():
        newtitletf = TitleTF(term, docid, freq)
        store_titletf(newtitletf, cursor)

def store_titletf(titletf, cursor):
    cursor.execute("INSERT INTO title_tf (term, docid, freq, tf) VALUES (%s, %s, %s, %s)", (titletf.term, titletf.docid, titletf.freq, titletf.tf))

def update_titletfs(cursor):
    cursor.execute("SELECT term, docid, tf FROM title_tf")
    term_records = cursor.fetchall()
    for term_tuple in term_records:
        cursor.execute("SELECT idf FROM title_idf WHERE term = %s", (term_tuple[0],))
        (idf,) = cursor.fetchone()
        tfidf = term_tuple[2] * idf
        cursor.execute("UPDATE title_tf SET tfidf = %s WHERE term = %s AND docid = %s", (tfidf, term_tuple[0], term_tuple[1]))

#############
# TITLE IDF #
#############

def process_all_titleidfs(cursor):
    freqdict = defaultdict(int)
    cursor.execute("SELECT term, docid FROM title_tf")
    records = cursor.fetchall()
    corp_size = len(records)
    for rec in records:
        freqdict[rec[0]] += 1
    for term in freqdict:
        newtitleidf = TitleIDF(term, freqdict[term], corp_size)
        store_titleidf(newtitleidf, cursor)

def store_titleidf(titleidf, cursor):
    cursor.execute("INSERT INTO title_idf (term, freq, idf) VALUES (%s, %s, %s)", (titleidf.term, titleidf.freq, titleidf.idf))

############################################
# MISC. PROCESSING (docid/url,docid/title) #
############################################

def process_id_url_title(docid, url, title, cursor):
    new_idurl = IdUrl(docid, url) 
    store_idurl(new_idurl, cursor) # store the url/docid into id_url table
    new_idtitle = IdTitle(docid, title)
    store_idtitle(new_idtitle, cursor) # store the docid/title into id_title

def store_idurl(idurl, cursor):
    cursor.execute("INSERT INTO id_url (docid, url) VALUES (%s, %s)", (idurl.docid, idurl.url))

def store_idtitle(idtitle, cursor):
    # First, convert database charset to utf8 to store
    # different kinds of symbols.
    cursor.execute('SET NAMES utf8;')
    cursor.execute('SET CHARACTER SET utf8;')
    cursor.execute('SET character_set_connection=utf8;')
    # Then insert the docid and title into the database
    cursor.execute("INSERT INTO id_title (docid, title) VALUES (%s, %s)", (idtitle.docid, idtitle.title))

#########################
# TOKENIZE / INDEX FILE #
#########################

# adapted from stack overflow
re_matcher = re.compile("^https?://.*ics.uci.edu")
def get_links(html):
    links = []
    soup = BeautifulSoup(html, "html.parser")
    for link in soup.findAll('a', attrs={'href': re_matcher}):
        links.append(link.get('href'))
    return links

def hasdigit(token):
    return any(c.isdigit() for c in token)

stopw = Stopwords()
def check_token(token):
    return not stopw.is_stop(token) and not hasdigit(token) and len(token) > 1 and len(token) < 20

def add_token(token):
    pass

nonalphanum = re.compile("[^0-9a-z']")
def tokenize_text(intext):
    freqdict = defaultdict(int)
    for token in nonalphanum.split(intext):
        if check_token(token):
            freqdict[token] += 1
    return freqdict

def process_file(infile, cursor):
    try:
        injson = json.load(file(infile))
        docid = int(injson["id"])
        url = injson["_id"]
        title = injson["title"]
        intext = injson["text"].lower()
        # html = injson["html"].lower()
        # print "DocId: %s URL: %s text: %s" % (docid, url, intext)
    except:
        print "Error during parsing of file: " + infile
        return
    process_id_url_title(docid, url, title, cursor)
    process_all_termdocs(intext, docid, cursor)
    process_all_titletfs(title, docid, cursor)
    # Leaving pagerank out
    # process_all_outbound(docid, html, cursor)
    
# Processes all term idfs and title idfs
def second_pass(cursor):
    process_all_idfs(cursor)
    process_all_titleidfs(cursor)
    # Leaving pagerank out
    # process_parent_to_child(cursor)
    
    
# Updates tfidf scores for terms and titles 
def third_pass(cursor):
    update_termdocs(cursor)
    update_titletfs(cursor) 

def index(indir):
    cursor = get_cursor()       
    for infile in os.listdir(indir):
        abs_file = os.path.abspath(os.path.join(indir, infile))
        print "Processing Filename: " + abs_file
        process_file(abs_file, cursor)    
    print "Starting IDF and Title IDF processing!"
    second_pass(cursor)    
    print "Updating tfidf scores for terms and titles!"
    third_pass(cursor)        

if __name__ == "__main__":
    indir = 'C:\Users\username\Downloads\sample'
    index(indir)
