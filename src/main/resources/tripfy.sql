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
  `u_warncnt` bigint AUTO_INCREMENT DEFAULT 0,
  `isDelete` int DEFAULT 0
);

CREATE TABLE `u_img` (
  `userid` varcahr(300),
  `systemname` varchar(2000),
  `orgname` varchar(2000)
);

CREATE TABLE `manager` (
  `managerid` varchar(300) PRIMARY KEY,
  `managerpw` varcahr(300)
);

CREATE TABLE `guide` (
  `guidenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `g_likecnt` bigint AUTO_INCREMENT DEFAULT 0,
  `g_warncnt` bigint AUTO_INCREMENT DEFAULT 0
);

CREATE TABLE `region` (
  `regionname` varchar(300),
  `contrycode` varchar(300),
  PRIMARY KEY (`regionname`, `contrycode`)
);

CREATE TABLE `board` (
  `boardnum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `title` varchar(300),
  `content` text,
  `regdate` datetime DEFAULT 'now()',
  `updateck` int DEFAULT 0,
  `regionname` varchar(300),
  `contrycode` varchar(300),
  `likecnt` bigint,
  `viewcnt` bigint
);

CREATE TABLE `boardadd` (
  `boarnum` bigint,
  `place_name` varchar(300),
  `road_address` varchar(300),
  `address` varchar(300),
  `duedate` date,
  `enddate` date
);

CREATE TABLE `b_reply` (
  `replynum` bigint PRIMARY KEY AUTO_INCREMENT,
  `boardnum` bigint,
  `userid` varchar(300),
  `updateck` int DEFAULT 0,
  `em_systemname` varchar(300),
  `contents` varchar(2000),
  `regdate` datetime DEFAULT 'now()'
);

CREATE TABLE `b_file` (
  `boardnum` bigint,
  `systemname` varchar(300) PRIMARY KEY,
  `orgname` varchar(300)
);

CREATE TABLE `packages` (
  `pacakgenum` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `guidenum` bigint,
  `p_title` varchar(300),
  `p_content` text,
  `maxcnt` int,
  `a_price` int,
  `c_price` int,
  `p_startdate` DATETIME,
  `p_enddate` VARCHAR(300),
  `p_tourdays` int,
  `viewcnt` bigint,
  `deadline` date NOT NULL,
  `isDelete` int DEFAULT 0
);

CREATE TABLE `timeline` (
  `timelinenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `day` int,
  `tit` text,
  `cont` text
);

CREATE TABLE `packages_file` (
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
  `resernum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `a_cnt` int,
  `c_cnt` int,
  `userid` varchar(300),
  `keycode` varchar(300),
  `price` varchar(300),
  `p_method` varchar(300),
  `is_delete` int DEFAULT 0
);

CREATE TABLE `not_user` (
  `packagenum` bigint,
  `name` varchar(300),
  `phone` varchar(300),
  `keycode` varchar(300) PRIMARY KEY
);

CREATE TABLE `p_report` (
  `userid` varchar(300),
  `reporturl` varchar(300),
  `reportcontents` text,
  `state` varchar(300) DEFAULT '대기중'
);

CREATE TABLE `chat` (
  `chat_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid_1` varchar(300),
  `userid_2` varchar(300),
  `packagenum` bigint
);

CREATE TABLE `chat_detail` (
  `messagenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `chat_id` bigint,
  `senderid` varchar(300),
  `message` text,
  `regdate` datetime DEFAULT 'now()'
);
