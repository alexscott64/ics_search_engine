/* Authors: Alex Scott and Sebastian Val */

package ir.assignments.three;

import java.util.Collection;

public class Crawler {
    /**
     * This method is for testing purposes only. It does not need to be used
     * to answer any of the questions in the assignment. However, it must
     * function as specified so that your crawler can be verified programatically.
     * 
     * This methods performs a crawl starting at the specified seed URL. Returns a
     * collection containing all URLs visited during the crawl.
     */
    public static Collection<String> crawl(String seedURL) {
        return CS121WebCrawler.doCrawl(seedURL, 1);
    }
    
    public static void main(String [] args) {
        Collection<String> urlCollection;
        if(args.length == 0) { // CRAWL ICS
            urlCollection = CS121WebCrawler.doCrawl("http://www.ics.uci.edu", 0);			        
        } else { // CRAWL ARGUMENT
            urlCollection = CS121WebCrawler.doCrawl(args[0], 1);			
        }
        for (String url : urlCollection) {
            System.out.println(url);
        }
    }
}
