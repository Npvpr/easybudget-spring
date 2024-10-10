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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (2,'Wise',567.13),(7,'Cash',0.00),(8,'Monzo',0.00);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (27,'Salary'),(28,'Rent and Bills'),(29,'Food'),(33,'Family Support'),(34,'Transfer'),(35,'Transportation'),(36,'Clothes'),(37,'Electronics'),(38,'Hygiene products'),(39,'Kitchen Items'),(40,'University'),(41,'Government'),(42,'Sim and Internet');
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
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entry`
--

LOCK TABLES `entry` WRITE;
/*!40000 ALTER TABLE `entry` DISABLE KEYS */;
INSERT INTO `entry` VALUES (81,100.00,'2024-08-25 17:54:00.000000','Ma Su','INCOME',34,2),(82,2.10,'2024-08-25 17:57:00.000000','Tesco','OUTCOME',29,2),(84,4.00,'2024-08-30 18:15:00.000000','Poundland','OUTCOME',38,2),(85,6.10,'2024-08-31 18:16:00.000000','Ma Su House','OUTCOME',35,2),(86,2.44,'2024-09-01 18:17:00.000000','AliExpress','OUTCOME',38,2),(87,1.40,'2024-09-02 18:18:00.000000','Tesco','OUTCOME',29,2),(88,5000.00,'2024-09-04 18:19:00.000000','Uncle Oo','INCOME',33,2),(89,4500.00,'2024-09-04 18:20:00.000000','University of Westminster (2nd payment to fill 50% of total fees)','OUTCOME',40,2),(90,11.10,'2024-09-04 18:21:00.000000','Temu: Handheld Vacuum Cleaner','OUTCOME',37,2),(91,3.99,'2024-09-04 18:22:00.000000','Temu, 5 pairs of underwear','OUTCOME',36,2),(92,15.73,'2024-09-05 18:22:00.000000','Zaman Brothers: eggs, chicken, beans','OUTCOME',29,2),(93,1.00,'2024-09-07 18:24:00.000000','Poundland: Battery for portable bidet','OUTCOME',37,2),(94,35.00,'2024-09-07 18:25:00.000000','Primark: Football shirt, long pant, short pant, shoes','OUTCOME',36,2),(95,4.20,'2024-09-07 18:27:00.000000','Ma Su House','OUTCOME',35,2),(96,3.50,'2024-09-11 18:31:00.000000','University of Westminster','OUTCOME',35,2),(97,1.05,'2024-09-12 18:32:00.000000','Tesco','OUTCOME',29,2),(98,5.40,'2024-09-13 18:33:00.000000','Ma Su House','OUTCOME',35,2),(99,34.00,'2024-09-14 18:34:00.000000','Driver License','OUTCOME',41,2),(100,12.00,'2024-09-14 18:35:00.000000','Order New Oyster Card Online','OUTCOME',35,2),(101,16.99,'2024-09-14 18:36:00.000000','Saintbury: Rice, ...','OUTCOME',29,2),(102,7.20,'2024-09-14 18:37:00.000000','Ma Su House','OUTCOME',35,2),(103,0.90,'2024-09-16 18:37:00.000000','Tesco','OUTCOME',29,2),(104,5.40,'2024-09-16 18:37:00.000000','University of Westminster','OUTCOME',35,2),(105,5.50,'2024-09-17 18:39:00.000000','University of Westminster','OUTCOME',35,2),(106,1200.00,'2024-09-18 18:39:00.000000','Uncle Oo','INCOME',33,2),(107,10.00,'2024-09-20 18:41:00.000000','Lebara Monthly Package','OUTCOME',42,2),(108,5.40,'2024-09-19 18:41:00.000000','University of Westminster','OUTCOME',35,2),(109,4.20,'2024-09-20 18:42:00.000000','Ma Su House','OUTCOME',35,2),(110,4.20,'2024-09-21 18:42:00.000000','Ma Su House','OUTCOME',35,2),(111,4.49,'2024-09-23 18:43:00.000000','Zaman Brothers','OUTCOME',29,2),(112,10.00,'2024-09-23 18:43:00.000000','Shuma Account Check','OUTCOME',28,2),(113,5.40,'2024-09-23 18:44:00.000000','University of Westminster','OUTCOME',35,2),(114,6.70,'2024-09-25 18:44:00.000000','University of Westminster, Look houses to rent','OUTCOME',35,2),(115,790.00,'2024-09-25 18:45:00.000000','Shuma remaining rent for September','OUTCOME',28,2),(116,12.30,'2024-09-25 20:35:00.000000','Look around to rent houses','OUTCOME',35,2),(117,5.50,'2024-09-26 20:36:00.000000','University of Westminster','OUTCOME',35,2),(118,17.00,'2024-09-28 21:08:00.000000','Buy New Oyster Card at the station','OUTCOME',35,2),(119,30.00,'2024-09-28 21:08:00.000000','Buy Railcard One Third Off','OUTCOME',35,2),(120,8.50,'2024-09-28 21:09:00.000000','Travel With Nyan Lin Htet','OUTCOME',35,2),(121,1000.00,'2024-10-01 21:10:00.000000','Uncle Oo','INCOME',33,2),(122,1080.00,'2024-10-01 21:11:00.000000','Send to Ko Htet Myat for one month deposit and one month rent (540+540)','OUTCOME',28,2),(123,50.00,'2024-10-02 21:12:00.000000','Added into Oyster Card','OUTCOME',35,2),(124,3.70,'2024-10-07 21:13:00.000000','Tesco: Cauliflower, Chicken','OUTCOME',29,2),(125,5.48,'2024-10-08 21:14:00.000000','Zaman Brothers: Chicken Balls, Cooking Oil','OUTCOME',29,2);
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

-- Dump completed on 2024-10-10 22:22:53
