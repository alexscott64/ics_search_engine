DROP TABLE IF EXISTS `stopwords`;
CREATE TABLE `stopwords` (
  `stopword` varchar(100) NOT NULL,
  PRIMARY KEY (`stopword`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `subdomains`;
CREATE TABLE `subdomains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subdomain` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `word_counts`;
CREATE TABLE `word_counts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL,
  `num_words` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `words`;
CREATE TABLE `words` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `words_top5000`;
CREATE TABLE `words_top5000` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(100) DEFAULT NULL,
  `word_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
