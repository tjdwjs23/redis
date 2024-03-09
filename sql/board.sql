CREATE TABLE `BOARD` (
     `ID` bigint(20) NOT NULL AUTO_INCREMENT,
     `TITLE` varchar(200) DEFAULT NULL,
     `CONTENT` text DEFAULT NULL,
     `WRITE_ID` varchar(100) DEFAULT NULL,
     `CREATED_DATE` datetime NOT NULL DEFAULT current_timestamp() COMMENT '생성일',
     `UPDATED_DATE` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일',
     PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;