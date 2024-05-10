create database tripfy;

use tripfy;

CREATE TABLE `user` (
  `userid` varchar(300) PRIMARY KEY,
  `userpw` varchar(300),
  `phone` varchar(300),
  `email` varchar(300),
  `gender` varchar(300),
  `birth` varchar(300),
  `addr` varchar(300),
  `addrdetail` varchar(300),
  `addretc` varchar(300),
  `regdate` date DEFAULT 'now()',
  `user_warncnt` bigint AUTO_INCREMENT DEFAULT 0,
  `isDelete` int DEFAULT 0
);

CREATE TABLE `user_img` (
  `userid` varchar(300),
  `sysname` varchar(2000),
  `orgname` varchar(2000)
);

CREATE TABLE `manager` (
  `managerid` varchar(300) PRIMARY KEY,
  `managerpw` varchar(300)
);

CREATE TABLE `guide` (
  `guidenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `guide_likecnt` bigint AUTO_INCREMENT DEFAULT 0,
  `guide_warncnt` bigint AUTO_INCREMENT DEFAULT 0
);

create table `guide_user`(
	`guidenum` bigint,
    `userid` varchar(300)
);

create table `review` (
	`guidenum` bigint,
    `userid` varchar(300),
    `contents` text,
    `packagenum` bigint
);

CREATE TABLE `region` (
  `regionname` varchar(300),
  `countrycode` varchar(300),
  PRIMARY KEY (`regionname`, `countrycode`)
);

CREATE TABLE `board` (
  `boardnum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `title` varchar(300),
  `content` text,
  `regdate` datetime DEFAULT 'now()',
  `updatecheck` int DEFAULT 0,
  `regionname` varchar(300),
  `countrycode` varchar(300),
  `likecnt` bigint,
  `viewcnt` bigint
);

CREATE TABLE `boardaddr` (
  `boarnum` bigint,
  `placename` varchar(300),
  `roadaddress` varchar(300),
  `address` varchar(300),
  `duedate` date,
  `enddate` date
);

CREATE TABLE `board_reply` (
  `replynum` bigint PRIMARY KEY AUTO_INCREMENT,
  `boardnum` bigint,
  `userid` varchar(300),
  `updatecheck` int DEFAULT 0,
  `em_sysname` varchar(300),
  `contents` varchar(2000),
  `regdate` datetime DEFAULT 'now()'
);

CREATE TABLE `board_file` (
  `boardnum` bigint,
  `sysname` varchar(300) PRIMARY KEY,
  `orgname` varchar(300)
);

CREATE TABLE `package` (
  `pacakgenum` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `guidenum` bigint,
  `package_title` varchar(300),
  `package_content` text,
  `maxcnt` int,
  `adult_price` int,
  `child_price` int,
  `startdate` DATETIME,
  `enddate` VARCHAR(300),
  `tourdays` int,
  `viewcnt` bigint,
  `deadline` date NOT NULL,
  `isdelete` int DEFAULT 0
);

CREATE TABLE `timeline` (
  `timelinenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `day` int,
  `title` text,
  `contents` text
);

CREATE TABLE `package_file` (
  `packagenum` bigint,
  `pf_sysname` VARCHAR(300) PRIMARY KEY,
  `pf_orgname` VARCHAR(300)
);

CREATE TABLE `timeline_file` (
  `timelinenum` bigint,
  `tf_sysname` VARCHAR(300) PRIMARY KEY,
  `tf_orgname` VARCHAR(300)
);

CREATE TABLE `reservation` (
  `reservationnum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `adult_cnt` int,
  `child_cnt` int,
  `userid` varchar(300),
  `keycode` varchar(300),
  `price` varchar(300),
  `pay_method` varchar(300),
  `isdelete` int DEFAULT 0
);

CREATE TABLE `not_user` (
  `packagenum` bigint,
  `name` varchar(300),
  `phone` varchar(300),
  `keycode` varchar(300) PRIMARY KEY
);

CREATE TABLE `report` (
  `userid` varchar(300),
  `reporturl` varchar(300),
  `reportcontents` text,
  `state` varchar(300) DEFAULT '대기중'
);

CREATE TABLE `chat` (
  `chatid` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid_a` varchar(300),
  `userid_b` varchar(300),
  `packagenum` bigint
);

CREATE TABLE `chat_detail` (
  `messagenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `chatid` bigint,
  `senderid` varchar(300),
  `message` text,
  `regdate` datetime DEFAULT 'now()'
);
