<?php
/* Authors: Alex Scott and Sebastian Val */

class Result
{
	public $url;
	public $tfidf;
	public $docid;
	public $title;
}

function search_header() {
?>
<html itemscope="" itemtype="http://schema.org/WebPage" lang="en">
<head>
<title>UCI Search</title>
<style type="text/css">

sentinel {
	
}
</style>
</head>
<body>
<FORM ACTION="search.php" METHOD="POST">  
    <div class="float_center">
        <ul class="child">
          <li><input id="q" maxlength="2048" name="q" autocomplete="off"
		title="Search" type="text" value="<?php echo (isset($_REQUEST["q"])) ? $_REQUEST["q"] : " "; ?>" aria-label="Search"><input type="Submit" value="Search" name="Search"></li>
        </ul>
        <div class="clear"></div>
    </div>
<?php 	
}

function print_results($results) {
	foreach ($results as $result) {
		?>
		<div >
		    <h3><a href="<?php echo $result->url; ?>"><?php echo $result->title; ?></a></h3>
		    <div>
		        <div>
		            <div style="white-space:nowrap">
		            	<cite><?php echo $result->url; ?><b><?php echo $result->title; ?></b></cite>
		            </div>
		            <span class="st"><!-- Snippet? -->&nbsp;</span>
		        </div>
		    </div>
		</div>
	<?php 
	}	
}

function get_title($docid, $db) {
	$sql = "SELECT title FROM id_title WHERE docid = ? LIMIT 1";
	$statement = $db->prepare($sql);
	$statement->bind_param('i', $docid);
	$statement->execute();
	$statement->bind_result($title);
	$statement->store_result();
	while($statement->fetch()){
		return $title;
	}
	return "error";

}

function get_url($docid, $db) {
	$sql = "SELECT url FROM id_url WHERE docid = ? LIMIT 1";
	$statement = $db->prepare($sql);
	$statement->bind_param('i', $docid);
	$statement->execute();
	$statement->bind_result($url);
	$statement->store_result();
	while($statement->fetch()){
		return $url;
	}
	return "error";
	
}

function get_search_results($q, $db) {	
	$results = array();	
	$query_params = array();
	$conditions = array();
	$title_multiple = 2;
	
	$words = preg_split('/\s+/', $q);
	foreach ($words as $word) {
		array_push($conditions, "term = ?");
		array_push($query_params, $word);
	}
	foreach ($words as $word) {
		array_push($query_params, $word);
	}
	$cond_sql = join(" OR ", $conditions);
	$sql = "SELECT docid,SUM(q.tot_tfidf) as sum_tot_tfidf FROM (" .
			"SELECT tf.docid,SUM(tf.tfidf) AS tot_tfidf FROM tf WHERE $cond_sql GROUP by tf.docid UNION " .
			"SELECT title_tf.docid,$title_multiple*SUM(title_tf.tfidf) FROM title_tf WHERE $cond_sql GROUP by title_tf.docid) " .
			"as q GROUP BY q.docid ORDER BY sum_tot_tfidf DESC LIMIT 10 ";	
	$statement = $db->prepare($sql);
	$statement->bind_param(str_repeat('s', sizeof($query_params)), ...$query_params);
	$statement->execute();
	$statement->bind_result($docid, $tfidf);
	$statement->store_result();
	while($statement->fetch()){
		$new_result = new Result;
		$new_result->tfidf = $tfidf;
		$new_result->docid = $docid;
		$new_result->url = get_url($docid, $db);
		$new_result->title = get_title($docid, $db);
		/* echo "Got DOCID: $docid and URL: " . $new_result->url . "<br />"; */
		array_push($results, $new_result);
	}
	return $results;
}

$db = new mysqli('localhost', 'cs121', 'cs121pass', 'indexer');

function do_search_results($q, $db) {
	$results = get_search_results($q, $db);
	print_results($results);
}

if (isset($_REQUEST["Search"]) && $_REQUEST["Search"] == "Search") {
	if (isset($_REQUEST["q"])) {
		search_header();
		do_search_results($_REQUEST["q"], $db);
	}
} 
elseif (isset($_REQUEST["Lucky"]) && $_REQUEST["Lucky"] == "I'm Feeling Lucky" ) {
	//
}
else {
	search_header();
}
?>
