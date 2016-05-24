/* Authors: Alex Scott and Sebastian Val */

package ir.assignments.three;

import java.io.File;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;

import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* import ir.assignments.three.JSONFile; */




public class CS121WebCrawler extends WebCrawler {

    /**
     *  CRAWL_FLAG is a variable that lets us know whether or not to use
     *  a specific options when crawling domains:
     *
     *  1. CRAWL_FLAG = -1;
     *      - Set by default, do not do or change anything. It should be updated later
     *  2. CRAWL_FLAG = 0;
     *      - If a grader or someone running our code wants to crawl the ics domain, and no other domains
     *      then the CRAWL_FLAG = 0, and our default options (from patterns and domain/scheme) are set
     *  3. CRAWL_FLAG = 1;
     *      - If command line arguments (which are seeds) are provided, we will check and use those arguments
     *      as domain seeds.
     */
    private static int CRAWL_FLAG = -1;

    private static final Logger logger = LoggerFactory.getLogger(CS121WebCrawler.class);    

    private static final Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
        "|rm|smil|wmv|swf|wma|zip|rar|gz|ps|mso|xml|pptx?|xlsx?|docx?|rtf|odc" + 
        "|r|png|pdf|log|odp|m|jar|java|exe|eps|db|data?|fig|bz2|bg|bib|rdf|raw|rss|sh|cpp|c|cc|csv|txt|mat))$");

    private static String DOMAIN;
    private static String SCHEME = "http:";
    
    private static Collection<String> urlCollection = null;
    /* private static JSONFile json = new JSONFile(); */
    
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        if(CRAWL_FLAG == 1) { // DO NOT CRAWL ICS
            String href = url.getURL().toLowerCase();
            return !FILTERS.matcher(href).matches() && href.startsWith(SCHEME) && href.contains(DOMAIN);
        } else { // CRAWL ICS
            String href = url.getURL().toLowerCase();
            if (href.contains("calender") || href.contains("wics.ics.uci.edu") || href.contains("fano.ics.uci.edu")) {
                return false;
            }
            if (href.contains("datasets") || href.contains("duttgroup") || href.contains("ganglia") || href.contains("mailman")) {
                return false;
            }
            return !FILTERS.matcher(href).matches() && href.startsWith(SCHEME) && href.contains(DOMAIN);
        }
    }
    
    @Override
    public void visit(Page page) {
      int docid = page.getWebURL().getDocid();
      String url = page.getWebURL().getURL();
      int parentDocid = page.getWebURL().getParentDocid();
      /* TODO: Write urls to disk to preserve state instead of adding to collection directly. */
      urlCollection.add(url);

      logger.debug("Docid: {}", docid);
      logger.info("URL: {}", url);
      logger.debug("Docid of parent page: {}", parentDocid);

      if (page.getParseData() instanceof HtmlParseData) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String text = htmlParseData.getText();
        String html = htmlParseData.getHtml();
        Set<WebURL> links = htmlParseData.getOutgoingUrls();

        logger.debug("Text length: {}", text.length());
        logger.debug("Html length: {}", html.length());
        logger.debug("Number of outgoing links: {}", links.size());
        /* Logger.debug("JSON Repr: {}", CS121WebCrawler.json.pageToFile(page)); */
        
      }
      

      logger.debug("=============");
    }

    /**
     * getDomain();
     * @param seedURL string containing the url we want to crawl
     * @return DOMAIN string which has http, https, and www filtered out
     */
    public static String getDomain(String seedURL) {
        return seedURL.replaceFirst("^(http://|http://www\\.|https://|https://www\\.|www\\.)","");
    }

    public static Collection<String> doCrawl(String seedURL, int flag) {
        CRAWL_FLAG = flag; // determine if we are going to crawl the ics domain
        urlCollection = new ArrayList<String>();
        DOMAIN = getDomain(seedURL);
        System.out.println(DOMAIN);
        if(flag == 0) {
            CRAWL_FLAG = 0;
        } else {
            CRAWL_FLAG = 1;
        }
        String storageFolder = "./crawl_storage";
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setPolitenessDelay(1200);
        if(CRAWL_FLAG == 0) { // set resumable crawling on ics domain
            crawlConfig.setResumableCrawling(true);
        } else {
            crawlConfig.setResumableCrawling(false);
        }
        crawlConfig.setIncludeBinaryContentInCrawling(false);
        crawlConfig.setProcessBinaryContentInCrawling(false);
        crawlConfig.setMaxDepthOfCrawling(10);        
        crawlConfig.setSocketTimeout(1000);
        crawlConfig.setOnlineTldListUpdate(false);
        
        CrawlController crawlController = null;
        
        try {
            File f = new File(storageFolder);
            f.mkdirs();
        } catch(Exception e) {
            e.printStackTrace();
        }
        crawlConfig.setCrawlStorageFolder(storageFolder);
        crawlConfig.setUserAgentString("UCI Inf141-CS121 crawler 21288450 68427591");
        System.out.println(crawlConfig.toString());
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        try {
                crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println(crawlController.toString());
        /* TODO: set Domain and Scheme */
        crawlController.addSeed(seedURL);
        crawlController.start(CS121WebCrawler.class, 1);
        /* TODO: Loads all urls from disk into collection */
        return urlCollection;
    }
    
    

}
