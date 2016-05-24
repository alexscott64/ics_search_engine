For the Crawler, we eventaully ignoring urls containing:
	1. "calendar", "fano.ics", "ganglia", "duttgrup" (traps)
	2. fano.ics.uci.edu 
		> ~10k synthetic pages with minimal relevant textual content and lots 
		of cross linking
	3. wics.ics.uci.edu (down) 
	4. "datasets" 
		> large datasets comprising ~3Gigs


In answering the common words question, we used the MySQL FullText list of stopwords (544 entries)
We did not further filter this list though some common entries are the results of html text parsing issues in crawler4j.

We added argument parsing such that the Crawler class's main method will crawl the ICS domain with no arguments.
In addition, the main function can be fed a seedURL to invoke the Crawler.crawl() method.
The Crawler.crawl() method can also be tested programatically as specified.

Our JSONFile class requires Google's Gson Java Library in the classpath.