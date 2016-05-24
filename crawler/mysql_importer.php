<?php
/* Authors: Alex Scott and Sebastian Val */
$i = 0;
$mysqli = new mysqli("localhost", "icsuser", "ics12345", "words");
if ($mysqli->connect_errno) {
    echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
}
$directory = "data.020316/";
$files = scandir($directory);
foreach($files as $file) {
$num_words = 0;
// if ($i > 20) break;
if ($file[0] == '.') continue;

$fcontents = file_get_contents($directory . $file);

$doc = json_decode($fcontents, true);
$doc["text"] = preg_replace("/[^A-Za-z ]/", '', $doc["text"]);
$query = "INSERT INTO words (word) VALUES";
foreach (explode(" ", $doc["text"]) as $word) {
    if (strlen($word) == 0 || strlen($word) > 99) continue;
    $query .= "('" . $mysqli->real_escape_string(strtolower($word)) . "'),";
    $num_words++;
}
$query = rtrim($query, ",");
if (!$mysqli->query($query)) {
    echo "Insert failed: (" . $mysqli->errno . ") " . $mysqli->error;
}
$query2 = "INSERT INTO word_counts (url, num_words) VALUES(";
$query2 .= "'" . $mysqli->real_escape_string($doc["url"]) . "',";
$query2 .= (string) $num_words . ")"; 
if (!$mysqli->query($query2)) {
    echo "Insert failed: (" . $mysqli->errno . ") " . $mysqli->error;
}
$query3 = "INSERT INTO subdomains (subdomain) VALUES(";
$query3 .= "'" . $mysqli->real_escape_string($doc["subDomain"]) . "')";
if (!$mysqli->query($query3)) {
    echo "Insert failed: (" . $mysqli->errno . ") " . $mysqli->error;
}

$i++;
}
?>
