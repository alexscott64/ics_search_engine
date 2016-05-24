# Authors: Alex Scott and Sebastian Val
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

#########################
# INITIALIZE TO DEFAULT #
#########################

def clear_pagerank_table(cursor):
    cursor.execute("DELETE FROM pagerank")

def default_pagerank(cursor):
    cursor.execute("SELECT count(*) FROM id_url")
    (docid_count,) = cursor.fetchone()
    default_contrib = 1.0 / int(docid_count)
    cursor.execute("SELECT docid FROM id_url")
    docid_records = cursor.fetchall()
    for doc_tuple in docid_records:
        cursor.execute("INSERT INTO pagerank VALUES(%s, %s)", (doc_tuple[0],default_contrib))

############
# PAGERANK #
############

def pagerank():
    cursor = get_cursor()
    clear_pagerank_table(cursor)
    default_pagerank(cursor)       

if __name__ == "__main__":
    pagerank()
