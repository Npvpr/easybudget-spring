-- MySQL dump 10.13  Distrib 8.0.39, for Linux (x86_64)
--
-- Host: localhost    Database: easybudgetdb
-- ------------------------------------------------------
-- Server version	8.0.39-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `balance` decimal(38,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'cash',100.00),(2,'wise',0.00),(3,'monzo',100.00),(5,'palata',400.00),(6,'New Testing',900.00);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `balance`
--

DROP TABLE IF EXISTS `balance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `balance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance`
--

LOCK TABLES `balance` WRITE;
/*!40000 ALTER TABLE `balance` DISABLE KEYS */;
/*!40000 ALTER TABLE `balance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (18,'fun'),(27,'Salary'),(28,'Bill'),(29,'Food'),(30,'FunTesting'),(31,'Test'),(32,'FunTesting');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entry`
--

DROP TABLE IF EXISTS `entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entry` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cost` decimal(38,2) DEFAULT NULL,
  `date_time` datetime(6) DEFAULT NULL,
  `description` varchar(1000) NOT NULL,
  `type` enum('INCOME','OUTCOME') DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf7mt5po7olj7eiwqlt6t3qc7j` (`category_id`),
  KEY `FK4co40llmt3yampxxsbfvp9itq` (`account_id`),
  CONSTRAINT `FK4co40llmt3yampxxsbfvp9itq` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKf7mt5po7olj7eiwqlt6t3qc7j` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry`
--

LOCK TABLES `entry` WRITE;
/*!40000 ALTER TABLE `entry` DISABLE KEYS */;
INSERT INTO `entry` VALUES (45,111212.00,'2024-09-10 22:47:00.000000','123123113','INCOME',27,1),(47,123.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',29,1),(48,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',29,5),(49,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',29,5),(50,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',31,5),(51,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',31,5),(56,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,5),(57,10.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(58,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(59,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(60,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(61,100.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(62,100.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(63,100.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(64,100.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(65,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(66,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(67,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(68,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(69,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(70,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(71,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(72,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(73,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(74,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,6),(75,200.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(76,200.00,'2024-09-10 16:01:00.000000','123123','OUTCOME',30,6),(77,100.00,'2024-09-10 16:01:00.000000','123123','INCOME',30,5),(78,100.00,'2024-10-10 16:01:00.000000','123123','INCOME',30,5),(79,100.00,'2023-10-10 16:01:00.000000','123123','INCOME',30,5),(80,100.00,'2024-09-01 22:47:00.000000','123123113','INCOME',27,1);
/*!40000 ALTER TABLE `entry` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-08  3:01:01
