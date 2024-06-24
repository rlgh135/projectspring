CREATE DATABASE tripfy;
drop database tripfy;
USE tripfy;

CREATE TABLE `user` (
  `userid` varchar(300) PRIMARY KEY,
  `userpw` varchar(300),
  `phone` varchar(300),
  `email` varchar(300),
  `gender` varchar(300),
  `birth` varchar(300),
  `addr` varchar(300),
  `placeid` varchar(300),
  `regdate` date DEFAULT (current_date),
  `user_warncnt` bigint DEFAULT 0,
  `isDelete` int DEFAULT 0,
  `introduce` text
);

CREATE TABLE `user_img` (
  `userid` varchar(300),
  `sysname` varchar(2000),
  `orgname` varchar(2000),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `guide` (
  `guidenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `guide_likecnt` bigint DEFAULT 0,
  `guide_warncnt` bigint DEFAULT 0,
  `introduce` text,
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `guide_user`(
  `guidenum` bigint,
  `userid` varchar(300),
  FOREIGN KEY (`guidenum`) REFERENCES `guide`(`guidenum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `review` (
  `guidenum` bigint,
  `userid` varchar(300),
  `contents` text,
  `packagenum` bigint,
  `em_sysname` varchar(300),
  FOREIGN KEY (`guidenum`) REFERENCES `guide`(`guidenum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`),
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`)
);

CREATE TABLE `region` (
  `regionname` varchar(300),
  `countrycode` varchar(300),
  PRIMARY KEY (`regionname`, `countrycode`)
);

INSERT INTO region VALUES ('kr','서울');
INSERT INTO region VALUES ('kr','제주도');
INSERT INTO region VALUES ('kr','경기도');
INSERT INTO region VALUES ('kr','강원도');
INSERT INTO region VALUES ('kr','충청도');
INSERT INTO region VALUES ('kr','경상도');
INSERT INTO region VALUES ('kr','전라도');
INSERT INTO region VALUES ('kr','인천광역시');

CREATE TABLE `board` (
  `boardnum` bigint PRIMARY KEY AUTO_INCREMENT,
  `userid` varchar(300),
  `title` varchar(300),
  `content` text,
  `regdate` datetime DEFAULT now(),
  `updatecheck` int DEFAULT 0,
  `regionname` varchar(300),
  `countrycode` varchar(300),
  `likecnt` bigint DEFAULT 0,
  `viewcnt` bigint DEFAULT 0,
  `replycnt` bigint DEFAULT 0,
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`),
  FOREIGN KEY (`regionname`, `countrycode`) REFERENCES `region`(`regionname`, `countrycode`)
);

CREATE TABLE `board_like` (
  `boardnum` bigint,
  `userid` varchar(300),
  FOREIGN KEY (`boardnum`) REFERENCES `board`(`boardnum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `boardaddr` (
  `boardnum` bigint,
  `placename` varchar(300),
  `startdate` date,
  `enddate` date,
  FOREIGN KEY (`boardnum`) REFERENCES `board`(`boardnum`)
);

CREATE TABLE `board_reply` (
  `replynum` bigint PRIMARY KEY AUTO_INCREMENT,
  `boardnum` bigint,
  `userid` varchar(300),
  `updatecheck` int DEFAULT 0,
  `em_sysname` varchar(300),
  `contents` varchar(2000),
  `regdate` datetime DEFAULT now(),
  FOREIGN KEY (`boardnum`) REFERENCES `board`(`boardnum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `board_file` (
  `boardnum` bigint,
  `sysname` varchar(300),
  `orgname` varchar(300),
  FOREIGN KEY (`boardnum`) REFERENCES `board`(`boardnum`)
);

CREATE TABLE `package` (
  `packagenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `guidenum` bigint,
  `package_title` varchar(300),
  `package_content` text,
  `maxcnt` int,
  `adult_price` int,
  `child_price` int,
  `startdate` varchar(300),
  `enddate` varchar(300),
  `tourdays` int,
  `viewcnt` bigint DEFAULT 0,
  `deadline` date NOT NULL,
  `isdelete` int DEFAULT 0,
  `regionname` varchar(300),
  `countrycode` varchar(300),
  `Visibility` varchar(3) DEFAULT 'x',
  FOREIGN KEY (`guidenum`) REFERENCES `guide`(`guidenum`),
  FOREIGN KEY (`regionname`, `countrycode`) REFERENCES `region`(`regionname`, `countrycode`)
);

CREATE TABLE `package_like` (
  `packagenum` bigint,
  `userid` varchar(300),
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `timeline` (
  `timelinenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `day` int,
  `detail_num` int,
  `title` text,
  `contents` text,
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`)
);

CREATE TABLE `package_file` (
  `packagenum` bigint,
  `pf_sysname` varchar(300),
  `pf_orgname` varchar(300),
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`)
);

CREATE TABLE `timeline_file` (
  `timelinenum` bigint,
  `tf_sysname` varchar(300),
  `tf_orgname` varchar(300),
  FOREIGN KEY (`timelinenum`) REFERENCES `timeline`(`timelinenum`)
);

CREATE TABLE `reservation` (
  `reservationnum` bigint PRIMARY KEY AUTO_INCREMENT,
  `packagenum` bigint,
  `adult_cnt` int,
  `child_cnt` int,
  `userid` varchar(300),
  `phone` varchar(300),
  `email` varchar(300),
  `keycode` varchar(300),
  `price` varchar(300),
  `pay_method` varchar(300),
  `isdelete` int DEFAULT 0,
  `name` varchar(300),
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `not_user` (
  `packagenum` bigint,
  `name` varchar(300),
  `phone` varchar(300),
  `keycode` varchar(300) PRIMARY KEY,
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`)
);

CREATE TABLE `report` (
  `userid` varchar(300),
  `reporturl` varchar(300),
  `reportcontents` text,
  `state` varchar(300) DEFAULT '대기중',
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

# 채팅 테이블

CREATE TABLE `chat_room` (
  `chat_room_idx` bigint PRIMARY KEY AUTO_INCREMENT,
  `chat_room_type` int NOT NULL COMMENT "채팅방 타입 - 0 일반/일반, 1 가이드/일반(문의), 2 가이드/일반(다대다)",
  `chat_room_title` varchar(300) COMMENT "null 가능, 패키지 관련이면 무조건 null",
  `packagenum` bigint COMMENT "null이면 일반 <-> 일반, null이 아니면 판매자 <-> 일반 <- 정도로 볼 수 있음, 240617부로 chat_room_type 추가, 역할이 넘어감",
  `chat_room_is_terminated` boolean NOT NULL COMMENT "종료되었는지 여부",
  `regdate` datetime NOT NULL DEFAULT (CURRENT_TIMESTAMP()) COMMENT "채팅방 개설 시간",
  FOREIGN KEY (`packagenum`) REFERENCES `package`(`packagenum`)
);

CREATE TABLE `chat_user` (
  `chat_room_idx` bigint,
  `userid` varchar(300),
  `chat_user_is_creator` boolean NOT NULL COMMENT "채팅방 생성자, 일반 <-> 일반의 경우 중요하지 않음, 판매자 <-> 유저의 경우 판매자가 true",
  `chat_user_is_quit` boolean NOT NULL COMMENT "해당 사용자가 나갔는지 여부",
  `chat_detail_idx` bigint COMMENT "각 레코드의 userid가 조회한 제일 최근의 메시지, 순환 참조를 위해 null기입을 가능하게 함, 그리고 자기 자신의 메시지도 기입 가능하게 해야 함",
  PRIMARY KEY(`chat_room_idx`, `userid`),
  FOREIGN KEY (`chat_room_idx`) REFERENCES `chat_room`(`chat_room_idx`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `chat_detail` (
  `chat_detail_idx` bigint PRIMARY KEY AUTO_INCREMENT,
  `chat_room_idx` bigint NOT NULL,
  `userid` varchar(300) NOT NULL COMMENT "메시지 작성자",
  `chat_detail_content` varchar(300) NOT NULL,
  `regdate` datetime NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  FOREIGN KEY (`chat_room_idx`) REFERENCES `chat_room`(`chat_room_idx`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

CREATE TABLE `chat_user_regdate` (
  `cur_idx` bigint PRIMARY KEY AUTO_INCREMENT,
  `chat_room_idx` bigint NOT NULL,
  `userid` varchar(300) NOT NULL,
  `cur_action` char(20) NOT NULL COMMENT "진입/이탈 기록, 최초진입(채팅방 생성시 진입) INIT_ENTER, 일반진입(생성 이후 진입) NORM_ENTER, 이탈 LEAVE",
  `cur_regdate` datetime NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  FOREIGN KEY (`chat_room_idx`) REFERENCES `chat_room`(`chat_room_idx`),
  FOREIGN KEY (`userid`) REFERENCES `user`(`userid`)
);

# 매니저와 쪽지

CREATE TABLE `manager`(
  `manage_key` varchar(1000)
);

INSERT INTO manager VALUES("X7a5G3kZ9T");

CREATE TABLE `task_message`(
  `messagenum` bigint PRIMARY KEY AUTO_INCREMENT,
  `task_type` varchar(300), /*1보드 2패키지 3유저*/
  `detail_num` varchar(300),
  `sendid` varchar(300),
  `contents` text,
  `answer` text,
  `task_status` varchar(300) DEFAULT "1", /*1은 진행중 2는 처리완료*/
  `regdate` datetime DEFAULT now()
);
