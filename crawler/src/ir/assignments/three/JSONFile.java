/* Authors: Alex Scott and Sebastian Val */
package ir.assignments.three;

import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import com.google.gson.Gson;

import java.util.Set;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.Collections;

/**
 * Creates a JSON file given a Page from WebCrawler
 */
public class JSONFile {

    private static final String directory = "data/";       // Change this to change what folder
                                                            // you want to save the output under

    private static final String fileExtension = ".json";    // Change this to change
                                                            // what type of file we save

    private static final Gson gson = new Gson();

    private String fileName;

    private int docid;
    private int textLength;     // amount of text length
    private int parentDocid;
    private String url;
    private String text;        // document text
    private String subDomain;
    private Set<WebURL> links;
    
    public JSONFile() {
        // Get docid and then create the file name
        this.docid = -1;
        this.fileName = "";

        // Get relevant information for creating the JSON object
        this.subDomain = "";
        this.url = "";
        this.parentDocid = -1;
        this.text = "";
        this.textLength = 0;
        this.links = Collections.emptySet();  
    }


    /**
     * Converts a Page to a JSON structure and writes to disk
     * @return String representation of Page
     */
    public String pageToFile(Page page) {
        // Get docid and then create the file name
        this.docid = page.getWebURL().getDocid();
        this.fileName = Integer.toString(this.docid);

        // Get relevant information for creating the JSON object
        this.subDomain = page.getWebURL().getSubDomain();
        this.url = page.getWebURL().getURL();
        this.parentDocid = page.getWebURL().getParentDocid();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            this.text = htmlParseData.getText();
            this.textLength = this.text.length();
            this.links = htmlParseData.getOutgoingUrls();
        } else {
            this.text = "";
            this.textLength = 0;
            this.links = Collections.emptySet();
        }
        return outputToFile(this);
    }

    /**
     * Outputs our data to a .json file
     * @return String representation of Page
     */
    public String outputToFile(JSONFile j) {
        try {
            File f = new File(directory + fileName + fileExtension);
            f.getParentFile().mkdirs();
            f.createNewFile();
            String str = gson.toJson(j);
            FileOutputStream out;
            out = new FileOutputStream(f);
            OutputStreamWriter outWriter = new OutputStreamWriter(out);
            outWriter.append(str);
            outWriter.close();
            out.close();
            return str;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /* Getters for private variables*/

    // The name of the file we write to
    public String getFileName() {
        return this.fileName;
    }

    public int getDocId() {
        return this.docid;
    }

    public int getTextLength() {
        return this.textLength;
    }

    public int getParentDocid() {
        return this.parentDocid;
    }

    public String getUrl() {
        return this.url;
    }

    public String getText() {
        return this.text;
    }

    public String getSubDomain() {
        return this.subDomain;
    }

    public Set<WebURL> getLinks() {
        return this.links;
    }


}
