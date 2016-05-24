<html itemscope="" itemtype="http://schema.org/WebPage" lang="en">
<!-- # Authors: Alex Scott and Sebastian Val -->
<head>
<style type="text/css">
.float_center {
	float: right;
	position: relative;
	left: -50%;
	text-align: left;
}

.float_center>.child {
	position: relative;
	left: 50%;
}

body {
	height: 100%;
	width: 100%;
	font-family: arial, sans-serif;
}

.clear {
	clear: both;
}

#title {
	text-align: center;
	color: #ffffff;
	margin: 40px 0 0 0;
}

#container {
	width: 500px;
	margin: 20px auto 0 auto;
	background-color: rgba(255, 255, 255, 0.5);
	border: 2px solid #000000;
	padding: 20px;
	border-radius: 10px;
}

ul {
	list-style-type: none;
	margin: 0;
	padding: 10px 0;
}

ul li {
	float: left;
	list-style-type: none;
	margin: 0 6px;
}

p {
	margin: 0;
}

input[type="submit"] {
	border: 1px solid #f2f2f2;
	background-image: -webkit-linear-gradient(top,#f8f8f8,#f1f1f1);
	background-color: #f8f8f8;
	box-shadow: 0 1px 1px rgba(0,0,0,0.1);
	border-radius: 2px;
	color: #757575;
	font-size: 13px;
	font-weight: bold;
}

sentinel {
	
}
</style>
</head>
<body>
<h1 id="title">UCI Search</h1>
<section id="container">
	<FORM ACTION="search.php" METHOD="POST">  
    <div class="float_center">
        <ul class="child">
          <li><input id="q" maxlength="2048" name="q" autocomplete="off"
		title="Search" type="text" value="" aria-label="Search"></li>
        </ul>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>

    <div class="float_center">
        <ul class="child">
          <li><input type="Submit" value="Search" name="Search"></li>
          <!--  <li><input type="Submit" value="I'm Feeling Lucky" name="Lucky"></a></li>  -->          
        </ul>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
	</FORM>
</section>
</body>
</html>