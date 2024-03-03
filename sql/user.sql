CREATE TABLE `USER` (
        `ID` bigint(20) NOT NULL AUTO_INCREMENT,
        `USER_NAME` varchar(200) DEFAULT NULL,
        `PASSWORD` varchar(200) DEFAULT NULL,
        PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;