DROP DATABASE IF EXISTS `asset-management`;

CREATE DATABASE IF NOT EXISTS `asset-management`;
USE `asset-management`;

--
-- Table structure for table `category`
--

CREATE TABLE `category`
(
    `id`                int NOT NULL,
    `created_by`        varchar(255) DEFAULT NULL,
    `created_date`      datetime     DEFAULT NULL,
    `last_updated_by`   varchar(255) DEFAULT NULL,
    `last_updated_date` datetime     DEFAULT NULL,
    `category_name`     varchar(255) DEFAULT NULL,
    `prefix`            varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_category_name` (`category_name`),
    UNIQUE KEY `unique_category_prefix` (`prefix`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `category`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

CREATE TABLE `user`
(
    `staff_code`        varchar(255) NOT NULL,
    `created_by`        varchar(255) DEFAULT NULL,
    `created_date`      datetime     DEFAULT NULL,
    `last_updated_by`   varchar(255) DEFAULT NULL,
    `last_updated_date` datetime     DEFAULT NULL,
    `dob`               date         DEFAULT NULL,
    `is_first_login`    bit(1)       DEFAULT NULL,
    `fname`             varchar(255) DEFAULT NULL,
    `gender`            varchar(255) DEFAULT NULL,
    `joined_date`       date     NOT NULL,
    `lname`             varchar(255) DEFAULT NULL,
    `location`          varchar(255) DEFAULT NULL,
    `password`          varchar(255) NOT NULL,
    `status`            varchar(255) DEFAULT NULL,
    `type`              varchar(255) DEFAULT NULL,
    `username`          varchar(255) NOT NULL,
    PRIMARY KEY (`staff_code`),
    UNIQUE KEY `unique_user_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `user`
    ENABLE KEYS */;
UNLOCK TABLES;

CREATE TABLE `asset`
(
    `code`              varchar(255) NOT NULL,
    `created_by`        varchar(255) DEFAULT NULL,
    `created_date`      datetime     DEFAULT NULL,
    `last_updated_by`   varchar(255) DEFAULT NULL,
    `last_updated_date` datetime     DEFAULT NULL,
    `name`              varchar(255) DEFAULT NULL,
    `install_date`      datetime     DEFAULT NULL,
    `is_deleted`        bit(1)       DEFAULT NULL,
    `location`          varchar(255) DEFAULT NULL,
    `specification`     varchar(255) DEFAULT NULL,
    `state`             varchar(255)     DEFAULT NULL,
    `category_id`       int          DEFAULT NULL,
    PRIMARY KEY (`code`),
    KEY `fk_asset_category` (`category_id`),
    CONSTRAINT `fk_asset_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asset`
--

LOCK TABLES `asset` WRITE;
/*!40000 ALTER TABLE `asset`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `asset`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment`
--

CREATE TABLE `assignment`
(
    `id`                int          NOT NULL AUTO_INCREMENT,
    `created_by`        varchar(255)  DEFAULT NULL,
    `created_date`      datetime      DEFAULT NULL,
    `last_updated_by`   varchar(255)  DEFAULT NULL,
    `last_updated_date` datetime      DEFAULT NULL,
    `assigned_date`     datetime     NOT NULL,
    `note`              varchar(4000) DEFAULT NULL,
    `state`             varchar(255)  DEFAULT NULL,
    `asset_code`        varchar(255) NOT NULL,
    `assign_by`         varchar(255)  DEFAULT NULL,
    `assign_to`         varchar(255)  DEFAULT NULL,
    `is_deleted`        bit(1)       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_assign_asset_code` (`asset_code`),
    KEY `fk_assign_by` (`assign_by`),
    KEY `fk_assign_to` (`assign_to`),
    CONSTRAINT `fk_assign_to` FOREIGN KEY (`assign_to`) REFERENCES `user` (`staff_code`),
    CONSTRAINT `fk_assign_by` FOREIGN KEY (`assign_by`) REFERENCES `user` (`staff_code`),
    CONSTRAINT `fk_assign_asset_code` FOREIGN KEY (`asset_code`) REFERENCES `asset` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `assignment`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_request`
--

CREATE TABLE `return_request`
(
    `request_id`        bigint       NOT NULL AUTO_INCREMENT,
    `created_by`        varchar(255) DEFAULT NULL,
    `created_date`      datetime     DEFAULT NULL,
    `last_updated_by`   varchar(255) DEFAULT NULL,
    `last_updated_date` datetime     DEFAULT NULL,
    `return_date`       datetime     DEFAULT NULL,
    `state`             varchar(255) DEFAULT NULL,
    `accept_by`         varchar(255) DEFAULT NULL,
    `assignment_id`     int          NOT NULL,
    `request_by`        varchar(255) NOT NULL,
    PRIMARY KEY (`request_id`),
    KEY `fk_assign_return_request` (`assignment_id`),
    CONSTRAINT `fk_assign_return_request` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_request`
--

LOCK TABLES `return_request` WRITE;
/*!40000 ALTER TABLE `return_request`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `return_request`
    ENABLE KEYS */;
UNLOCK TABLES;
