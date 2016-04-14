-- MySQL dump 10.15  Distrib 10.0.23-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: colims
-- ------------------------------------------------------
-- Server version	10.0.23-MariaDB-0ubuntu0.15.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `analytical_run`
--

DROP TABLE IF EXISTS `analytical_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analytical_run` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `storage_location` varchar(255) DEFAULT NULL,
  `l_instrument_id` bigint(20) DEFAULT NULL,
  `l_sample_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiteej35b4sjhfx12kd02jm6iw` (`l_instrument_id`),
  KEY `FKe9t2cb0e8d0xob2qfcoly21t5` (`l_sample_id`),
  CONSTRAINT `FKe9t2cb0e8d0xob2qfcoly21t5` FOREIGN KEY (`l_sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FKiteej35b4sjhfx12kd02jm6iw` FOREIGN KEY (`l_instrument_id`) REFERENCES `instrument` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `colims_user`
--

DROP TABLE IF EXISTS `colims_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `colims_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `name` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `l_institution_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7qy96sq9o6jh5517or8yh758` (`name`),
  KEY `FKmb059tuvfut3ocw5q05o35njn` (`l_institution_id`),
  CONSTRAINT `FKmb059tuvfut3ocw5q05o35njn` FOREIGN KEY (`l_institution_id`) REFERENCES `institution` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `experiment`
--

DROP TABLE IF EXISTS `experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `number` bigint(20) DEFAULT NULL,
  `storage_location` varchar(255) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `l_project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK48dysdsbha1vdr40udltld10v` (`l_project_id`),
  CONSTRAINT `FK48dysdsbha1vdr40udltld10v` FOREIGN KEY (`l_project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `experiment_binary_file`
--

DROP TABLE IF EXISTS `experiment_binary_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiment_binary_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `content` longblob NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `l_experiment_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaqv506oy06s0geln513eam413` (`l_experiment_id`),
  CONSTRAINT `FKaqv506oy06s0geln513eam413` FOREIGN KEY (`l_experiment_id`) REFERENCES `experiment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fasta_db`
--

DROP TABLE IF EXISTS `fasta_db`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fasta_db` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(200) NOT NULL,
  `file_path` varchar(250) NOT NULL,
  `md5_checksum` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(255) DEFAULT NULL,
  `taxonomy_accession` varchar(255) DEFAULT NULL,
  `version` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_has_role`
--

DROP TABLE IF EXISTS `group_has_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_has_role` (
  `l_group_id` bigint(20) NOT NULL,
  `l_role_id` bigint(20) NOT NULL,
  KEY `FKd4iuk0my83rtlhhulrcrtf76x` (`l_role_id`),
  KEY `FKd4jnxgpstfi5gntmi4m61ordn` (`l_group_id`),
  CONSTRAINT `FKd4iuk0my83rtlhhulrcrtf76x` FOREIGN KEY (`l_role_id`) REFERENCES `group_role` (`id`),
  CONSTRAINT `FKd4jnxgpstfi5gntmi4m61ordn` FOREIGN KEY (`l_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_role`
--

DROP TABLE IF EXISTS `group_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7kvrlnisllgg2md5614ywh82g` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identification_file`
--

DROP TABLE IF EXISTS `identification_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identification_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_type` varchar(255) DEFAULT NULL,
  `content` longblob,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `l_search_and_val_settings_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpgcxh2303lg1m1if047hm4opi` (`l_search_and_val_settings_id`),
  CONSTRAINT `FKpgcxh2303lg1m1if047hm4opi` FOREIGN KEY (`l_search_and_val_settings_id`) REFERENCES `search_and_validation_settings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `institution`
--

DROP TABLE IF EXISTS `institution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `abbreviation` varchar(10) NOT NULL,
  `city` varchar(30) NOT NULL,
  `country` varchar(30) NOT NULL,
  `name` varchar(30) NOT NULL,
  `number` int(11) NOT NULL,
  `postal_code` int(11) DEFAULT NULL,
  `street` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instrument`
--

DROP TABLE IF EXISTS `instrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instrument` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `l_detector_cv_id` bigint(20) NOT NULL,
  `l_source_cv_id` bigint(20) NOT NULL,
  `l_type_cv_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_11wfouotl7vb11u6ebomnbsrr` (`name`),
  KEY `FK1wxnrvqujdygv9ll147322weo` (`l_detector_cv_id`),
  KEY `FKoildeovchu26pb5ewxjaak4hv` (`l_source_cv_id`),
  KEY `FKmihpkan5xf8nt1256tovnqlno` (`l_type_cv_id`),
  CONSTRAINT `FK1wxnrvqujdygv9ll147322weo` FOREIGN KEY (`l_detector_cv_id`) REFERENCES `instrument_cv_param` (`id`),
  CONSTRAINT `FKmihpkan5xf8nt1256tovnqlno` FOREIGN KEY (`l_type_cv_id`) REFERENCES `instrument_cv_param` (`id`),
  CONSTRAINT `FKoildeovchu26pb5ewxjaak4hv` FOREIGN KEY (`l_source_cv_id`) REFERENCES `instrument_cv_param` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instrument_cv_param`
--

DROP TABLE IF EXISTS `instrument_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instrument_cv_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `cv_property` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instrument_has_analyzer`
--

DROP TABLE IF EXISTS `instrument_has_analyzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instrument_has_analyzer` (
  `l_instrument_id` bigint(20) NOT NULL,
  `l_instrument_cv_param_id` bigint(20) NOT NULL,
  KEY `FKe59386f5p3dnckvgwqtlq0eay` (`l_instrument_cv_param_id`),
  KEY `FKf70hvf3t0rthby8x7in9gpjc6` (`l_instrument_id`),
  CONSTRAINT `FKe59386f5p3dnckvgwqtlq0eay` FOREIGN KEY (`l_instrument_cv_param_id`) REFERENCES `instrument_cv_param` (`id`),
  CONSTRAINT `FKf70hvf3t0rthby8x7in9gpjc6` FOREIGN KEY (`l_instrument_id`) REFERENCES `instrument` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material`
--

DROP TABLE IF EXISTS `material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `l_cell_type_cv_id` bigint(20) DEFAULT NULL,
  `l_compartment_cv_id` bigint(20) DEFAULT NULL,
  `l_species_cv_id` bigint(20) NOT NULL,
  `l_tissue_cv_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbn76m95hfcc93i84r7f2jd0fx` (`l_cell_type_cv_id`),
  KEY `FKsu9ti70rc3fi9aqdsm9jttdji` (`l_compartment_cv_id`),
  KEY `FKly75y5tu933dmvgdeqvffqgvu` (`l_species_cv_id`),
  KEY `FKc4mhhee3lkceh33p7661vsvc2` (`l_tissue_cv_id`),
  CONSTRAINT `FKbn76m95hfcc93i84r7f2jd0fx` FOREIGN KEY (`l_cell_type_cv_id`) REFERENCES `material_cv_param` (`id`),
  CONSTRAINT `FKc4mhhee3lkceh33p7661vsvc2` FOREIGN KEY (`l_tissue_cv_id`) REFERENCES `material_cv_param` (`id`),
  CONSTRAINT `FKly75y5tu933dmvgdeqvffqgvu` FOREIGN KEY (`l_species_cv_id`) REFERENCES `material_cv_param` (`id`),
  CONSTRAINT `FKsu9ti70rc3fi9aqdsm9jttdji` FOREIGN KEY (`l_compartment_cv_id`) REFERENCES `material_cv_param` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `material_cv_param`
--

DROP TABLE IF EXISTS `material_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `material_cv_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `cv_property` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `modification`
--

DROP TABLE IF EXISTS `modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `modification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) DEFAULT NULL,
  `average_mass_shift` double DEFAULT NULL,
  `monoisotopic_mass_shift` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `utilities_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peptide`
--

DROP TABLE IF EXISTS `peptide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peptide` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `charge` int(11) DEFAULT NULL,
  `psm_post_error_prob` double DEFAULT NULL,
  `psm_prob` double DEFAULT NULL,
  `peptide_sequence` varchar(255) NOT NULL,
  `theoretical_mass` double DEFAULT NULL,
  `l_identification_file_id` bigint(20) DEFAULT NULL,
  `l_spectrum_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmbfqe4esd8l1or4wr22mp6rvd` (`l_identification_file_id`),
  KEY `FKmn3ekc0ajyk0f1b99ys1cv3kt` (`l_spectrum_id`),
  CONSTRAINT `FKmbfqe4esd8l1or4wr22mp6rvd` FOREIGN KEY (`l_identification_file_id`) REFERENCES `identification_file` (`id`),
  CONSTRAINT `FKmn3ekc0ajyk0f1b99ys1cv3kt` FOREIGN KEY (`l_spectrum_id`) REFERENCES `spectrum` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peptide_has_modification`
--

DROP TABLE IF EXISTS `peptide_has_modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peptide_has_modification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `delta_score` double DEFAULT NULL,
  `location` int(11) DEFAULT NULL,
  `modification_type` int(11) DEFAULT NULL,
  `prob_score` double DEFAULT NULL,
  `l_modification_id` bigint(20) DEFAULT NULL,
  `l_peptide_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjpyca1ms38npf9ydijom3uu8g` (`l_modification_id`),
  KEY `FK9nwv51l1qn930vkydlnqjmqjk` (`l_peptide_id`),
  CONSTRAINT `FK9nwv51l1qn930vkydlnqjmqjk` FOREIGN KEY (`l_peptide_id`) REFERENCES `peptide` (`id`),
  CONSTRAINT `FKjpyca1ms38npf9ydijom3uu8g` FOREIGN KEY (`l_modification_id`) REFERENCES `modification` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1690 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peptide_has_protein_group`
--

DROP TABLE IF EXISTS `peptide_has_protein_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peptide_has_protein_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `peptide_post_error_prob` double DEFAULT NULL,
  `peptide_prob` double DEFAULT NULL,
  `l_peptide_id` bigint(20) DEFAULT NULL,
  `l_protein_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn1jstvoawyb1elxm61oy2jaql` (`l_peptide_id`),
  KEY `FK18pu9uw3usnltmdj6bx1f8yso` (`l_protein_group_id`),
  CONSTRAINT `FK18pu9uw3usnltmdj6bx1f8yso` FOREIGN KEY (`l_protein_group_id`) REFERENCES `protein_group` (`id`),
  CONSTRAINT `FKn1jstvoawyb1elxm61oy2jaql` FOREIGN KEY (`l_peptide_id`) REFERENCES `peptide` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2ojme20jpga3r4r79tdso17gi` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `label` varchar(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `l_owner_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_etb9i6krbg45bl5o1kt0cc4q8` (`title`),
  KEY `FKsa6nsrbeschu8w2g96r5oxpv8` (`l_owner_user_id`),
  CONSTRAINT `FKsa6nsrbeschu8w2g96r5oxpv8` FOREIGN KEY (`l_owner_user_id`) REFERENCES `colims_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_has_user`
--

DROP TABLE IF EXISTS `project_has_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_has_user` (
  `l_project_id` bigint(20) NOT NULL,
  `l_user_id` bigint(20) NOT NULL,
  KEY `FKm5mj3iioe68fjwwy23c5o41xw` (`l_user_id`),
  KEY `FKq3ifbm2kqtbgemvp9tyx2a6l0` (`l_project_id`),
  CONSTRAINT `FKm5mj3iioe68fjwwy23c5o41xw` FOREIGN KEY (`l_user_id`) REFERENCES `colims_user` (`id`),
  CONSTRAINT `FKq3ifbm2kqtbgemvp9tyx2a6l0` FOREIGN KEY (`l_project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protein`
--

DROP TABLE IF EXISTS `protein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protein` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `protein_sequence` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1340 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protein_accession`
--

DROP TABLE IF EXISTS `protein_accession`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protein_accession` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) NOT NULL,
  `l_protein_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq08c89rewk8ag0hmyydalymv1` (`l_protein_id`),
  CONSTRAINT `FKq08c89rewk8ag0hmyydalymv1` FOREIGN KEY (`l_protein_id`) REFERENCES `protein` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1342 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protein_group`
--

DROP TABLE IF EXISTS `protein_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protein_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `protein_post_error_prob` double DEFAULT NULL,
  `protein_prob` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1196 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protein_group_has_protein`
--

DROP TABLE IF EXISTS `protein_group_has_protein`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protein_group_has_protein` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `main_group_protein` bit(1) NOT NULL,
  `protein_accession` varchar(255) DEFAULT NULL,
  `l_protein_id` bigint(20) DEFAULT NULL,
  `l_protein_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKyk152njh2ab03m0071buyv09` (`l_protein_id`),
  KEY `FKkvk2epn94kvxekgpfertigmbq` (`l_protein_group_id`),
  CONSTRAINT `FKkvk2epn94kvxekgpfertigmbq` FOREIGN KEY (`l_protein_group_id`) REFERENCES `protein_group` (`id`),
  CONSTRAINT `FKyk152njh2ab03m0071buyv09` FOREIGN KEY (`l_protein_id`) REFERENCES `protein` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1423 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protocol`
--

DROP TABLE IF EXISTS `protocol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `l_cell_based_cv_id` bigint(20) DEFAULT NULL,
  `l_enzyme_cv_id` bigint(20) DEFAULT NULL,
  `l_reduction_cv_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lidqee66itlhns030fyykvptc` (`name`),
  KEY `FKg5vc0ccy9ejrdb5hpyepyphdi` (`l_cell_based_cv_id`),
  KEY `FK1tcjjud12cme93drk0t6fg7l9` (`l_enzyme_cv_id`),
  KEY `FK3euus878cvnitmx6pejx4f7px` (`l_reduction_cv_id`),
  CONSTRAINT `FK1tcjjud12cme93drk0t6fg7l9` FOREIGN KEY (`l_enzyme_cv_id`) REFERENCES `protocol_cv_param` (`id`),
  CONSTRAINT `FK3euus878cvnitmx6pejx4f7px` FOREIGN KEY (`l_reduction_cv_id`) REFERENCES `protocol_cv_param` (`id`),
  CONSTRAINT `FKg5vc0ccy9ejrdb5hpyepyphdi` FOREIGN KEY (`l_cell_based_cv_id`) REFERENCES `protocol_cv_param` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protocol_cv_param`
--

DROP TABLE IF EXISTS `protocol_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol_cv_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `cv_property` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protocol_has_chemical_labeling`
--

DROP TABLE IF EXISTS `protocol_has_chemical_labeling`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol_has_chemical_labeling` (
  `l_protocol_id` bigint(20) NOT NULL,
  `l_chemical_labeling_cv_param_id` bigint(20) NOT NULL,
  KEY `FK3mqe9s11bj22eh0ayovjyrvd8` (`l_chemical_labeling_cv_param_id`),
  KEY `FKt3khhbo0mifwdh8amcwy2j73d` (`l_protocol_id`),
  CONSTRAINT `FK3mqe9s11bj22eh0ayovjyrvd8` FOREIGN KEY (`l_chemical_labeling_cv_param_id`) REFERENCES `protocol_cv_param` (`id`),
  CONSTRAINT `FKt3khhbo0mifwdh8amcwy2j73d` FOREIGN KEY (`l_protocol_id`) REFERENCES `protocol` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protocol_has_other_cv_param`
--

DROP TABLE IF EXISTS `protocol_has_other_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol_has_other_cv_param` (
  `l_protocol_id` bigint(20) NOT NULL,
  `l_other_protocol_cv_param_id` bigint(20) NOT NULL,
  KEY `FKpsrx2auqgdyvrslnvwept04bu` (`l_other_protocol_cv_param_id`),
  KEY `FKhtd7224thnb0iokbcu2i43lad` (`l_protocol_id`),
  CONSTRAINT `FKhtd7224thnb0iokbcu2i43lad` FOREIGN KEY (`l_protocol_id`) REFERENCES `protocol` (`id`),
  CONSTRAINT `FKpsrx2auqgdyvrslnvwept04bu` FOREIGN KEY (`l_other_protocol_cv_param_id`) REFERENCES `protocol_cv_param` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quant_param_settings_has_reagent`
--

DROP TABLE IF EXISTS `quant_param_settings_has_reagent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quant_param_settings_has_reagent` (
  `l_quant_parameters_id` bigint(20) NOT NULL,
  `l_quant_cv_param_id` bigint(20) NOT NULL,
  KEY `FKt3vv5dtaint5epinybomqcps0` (`l_quant_cv_param_id`),
  KEY `FKbwbwyd1jb471yc79wj0o4iqse` (`l_quant_parameters_id`),
  CONSTRAINT `FKbwbwyd1jb471yc79wj0o4iqse` FOREIGN KEY (`l_quant_parameters_id`) REFERENCES `quantification_parameters` (`id`),
  CONSTRAINT `FKt3vv5dtaint5epinybomqcps0` FOREIGN KEY (`l_quant_cv_param_id`) REFERENCES `quantification_cv_param` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification`
--

DROP TABLE IF EXISTS `quantification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `intensity` double NOT NULL,
  `weight` int(11) NOT NULL,
  `l_quantification_file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhp6q598tukiw3dybr5s5uye5d` (`l_quantification_file_id`),
  CONSTRAINT `FKhp6q598tukiw3dybr5s5uye5d` FOREIGN KEY (`l_quantification_file_id`) REFERENCES `quantification_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_cv_param`
--

DROP TABLE IF EXISTS `quantification_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_cv_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `cv_property` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_engine`
--

DROP TABLE IF EXISTS `quantification_engine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_engine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_file`
--

DROP TABLE IF EXISTS `quantification_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_type` varchar(255) DEFAULT NULL,
  `content` longblob,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `l_quant_settings_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4d8x74xeymr5d28knlt6d8m25` (`l_quant_settings_id`),
  CONSTRAINT `FK4d8x74xeymr5d28knlt6d8m25` FOREIGN KEY (`l_quant_settings_id`) REFERENCES `quantification_settings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_group`
--

DROP TABLE IF EXISTS `quantification_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `l_peptide_id` bigint(20) DEFAULT NULL,
  `l_quantification_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8di4lpv85fcqengxef1f8lagc` (`l_peptide_id`),
  KEY `FKql7d4db324m42qkjw2tsrub4p` (`l_quantification_id`),
  CONSTRAINT `FK8di4lpv85fcqengxef1f8lagc` FOREIGN KEY (`l_peptide_id`) REFERENCES `peptide` (`id`),
  CONSTRAINT `FKql7d4db324m42qkjw2tsrub4p` FOREIGN KEY (`l_quantification_id`) REFERENCES `quantification` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_parameters`
--

DROP TABLE IF EXISTS `quantification_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error` double DEFAULT NULL,
  `include_modifications` bit(1) DEFAULT NULL,
  `label_count` int(11) DEFAULT NULL,
  `minimum_ratio_count` int(11) DEFAULT NULL,
  `l_method_cv_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6da6k4vf3dqnsrtphoi4vppl8` (`l_method_cv_id`),
  CONSTRAINT `FK6da6k4vf3dqnsrtphoi4vppl8` FOREIGN KEY (`l_method_cv_id`) REFERENCES `quantification_cv_param` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantification_settings`
--

DROP TABLE IF EXISTS `quantification_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantification_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `l_analytical_run_id` bigint(20) DEFAULT NULL,
  `l_quant_engine_id` bigint(20) DEFAULT NULL,
  `l_quant_param_settings_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK47llb2g9m544eafyltelgxgh3` (`l_analytical_run_id`),
  KEY `FKesojbmir3semmlud3auxfb5ds` (`l_quant_engine_id`),
  KEY `FKmeif1l8necpvq8fj7xwpyxt8w` (`l_quant_param_settings_id`),
  CONSTRAINT `FK47llb2g9m544eafyltelgxgh3` FOREIGN KEY (`l_analytical_run_id`) REFERENCES `analytical_run` (`id`),
  CONSTRAINT `FKesojbmir3semmlud3auxfb5ds` FOREIGN KEY (`l_quant_engine_id`) REFERENCES `quantification_engine` (`id`),
  CONSTRAINT `FKmeif1l8necpvq8fj7xwpyxt8w` FOREIGN KEY (`l_quant_param_settings_id`) REFERENCES `quantification_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_has_permission`
--

DROP TABLE IF EXISTS `role_has_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_has_permission` (
  `l_role_id` bigint(20) NOT NULL,
  `l_permission_id` bigint(20) NOT NULL,
  KEY `FKl2nvbt87gatts91152i5iwhpq` (`l_permission_id`),
  KEY `FK3l7bj8uc0germq5qeeetj4pim` (`l_role_id`),
  CONSTRAINT `FK3l7bj8uc0germq5qeeetj4pim` FOREIGN KEY (`l_role_id`) REFERENCES `group_role` (`id`),
  CONSTRAINT `FKl2nvbt87gatts91152i5iwhpq` FOREIGN KEY (`l_permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample`
--

DROP TABLE IF EXISTS `sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `sample_condition` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `storage_location` varchar(255) DEFAULT NULL,
  `l_experiment_id` bigint(20) DEFAULT NULL,
  `l_protocol_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq7wo6mx1mie0bvgh0gqjrbjbn` (`l_experiment_id`),
  KEY `FK7h9b9mhyc2kkudpdvd8qh6m0r` (`l_protocol_id`),
  CONSTRAINT `FK7h9b9mhyc2kkudpdvd8qh6m0r` FOREIGN KEY (`l_protocol_id`) REFERENCES `protocol` (`id`),
  CONSTRAINT `FKq7wo6mx1mie0bvgh0gqjrbjbn` FOREIGN KEY (`l_experiment_id`) REFERENCES `experiment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_binary_file`
--

DROP TABLE IF EXISTS `sample_binary_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_binary_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `content` longblob NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `l_sample_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK76mk600o8fxbichw0v3215u0m` (`l_sample_id`),
  CONSTRAINT `FK76mk600o8fxbichw0v3215u0m` FOREIGN KEY (`l_sample_id`) REFERENCES `sample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_has_material`
--

DROP TABLE IF EXISTS `sample_has_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_has_material` (
  `l_sample_id` bigint(20) NOT NULL,
  `l_material_id` bigint(20) NOT NULL,
  KEY `FKxna6w7shfrqeawfnk90xnspm` (`l_material_id`),
  KEY `FKejc7sflcm1scs07ujask8210` (`l_sample_id`),
  CONSTRAINT `FKejc7sflcm1scs07ujask8210` FOREIGN KEY (`l_sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FKxna6w7shfrqeawfnk90xnspm` FOREIGN KEY (`l_material_id`) REFERENCES `material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_and_validation_settings`
--

DROP TABLE IF EXISTS `search_and_validation_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_and_validation_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `l_analytical_run_id` bigint(20) DEFAULT NULL,
  `l_search_engine_id` bigint(20) DEFAULT NULL,
  `l_search_parameters_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK55c3ddut84yvvrf7lj2ppg01m` (`l_analytical_run_id`),
  KEY `FK4mfeudo2uprqiy3t0t2qm53cm` (`l_search_engine_id`),
  KEY `FKsyl42a6frbo1ggtcf2jgytty1` (`l_search_parameters_id`),
  CONSTRAINT `FK4mfeudo2uprqiy3t0t2qm53cm` FOREIGN KEY (`l_search_engine_id`) REFERENCES `search_engine` (`id`),
  CONSTRAINT `FK55c3ddut84yvvrf7lj2ppg01m` FOREIGN KEY (`l_analytical_run_id`) REFERENCES `analytical_run` (`id`),
  CONSTRAINT `FKsyl42a6frbo1ggtcf2jgytty1` FOREIGN KEY (`l_search_parameters_id`) REFERENCES `search_parameters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_cv_param`
--

DROP TABLE IF EXISTS `search_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_cv_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `cv_property` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_engine`
--

DROP TABLE IF EXISTS `search_engine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_engine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ontology` varchar(255) NOT NULL,
  `param_value` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_modification`
--

DROP TABLE IF EXISTS `search_modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_modification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(255) DEFAULT NULL,
  `average_mass_shift` double DEFAULT NULL,
  `monoisotopic_mass_shift` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `utilities_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_parameters`
--

DROP TABLE IF EXISTS `search_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `search_ion_type_1` int(11) DEFAULT NULL,
  `fragment_mass_tolerance` double DEFAULT NULL,
  `fragment_mass_tolerance_unit` int(11) DEFAULT NULL,
  `lower_charge` int(11) DEFAULT NULL,
  `missed_cleavages` int(11) DEFAULT NULL,
  `precursor_mass_tolerance` double DEFAULT NULL,
  `precursor_mass_tolerance_unit` int(11) DEFAULT NULL,
  `search_ion_type_2` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `upper_charge` int(11) DEFAULT NULL,
  `l_search_enzyme_cv_id` bigint(20) DEFAULT NULL,
  `l_search_type_cv_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9mtfih93hmxyqvghcv8cpq6wo` (`l_search_enzyme_cv_id`),
  KEY `FKjum5v2h68ave00gpvn413fjj` (`l_search_type_cv_id`),
  CONSTRAINT `FK9mtfih93hmxyqvghcv8cpq6wo` FOREIGN KEY (`l_search_enzyme_cv_id`) REFERENCES `search_cv_param` (`id`),
  CONSTRAINT `FKjum5v2h68ave00gpvn413fjj` FOREIGN KEY (`l_search_type_cv_id`) REFERENCES `search_cv_param` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_parameters_has_other_cv_param`
--

DROP TABLE IF EXISTS `search_parameters_has_other_cv_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_parameters_has_other_cv_param` (
  `l_search_parameters_id` bigint(20) NOT NULL,
  `l_other_search_cv_param_id` bigint(20) NOT NULL,
  KEY `FKbwof864854r0mkmka95lel5u0` (`l_other_search_cv_param_id`),
  KEY `FKmtlux8vhowxo82xwtewks87vt` (`l_search_parameters_id`),
  CONSTRAINT `FKbwof864854r0mkmka95lel5u0` FOREIGN KEY (`l_other_search_cv_param_id`) REFERENCES `search_cv_param` (`id`),
  CONSTRAINT `FKmtlux8vhowxo82xwtewks87vt` FOREIGN KEY (`l_search_parameters_id`) REFERENCES `search_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_params_has_modification`
--

DROP TABLE IF EXISTS `search_params_has_modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_params_has_modification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modification_type` int(11) DEFAULT NULL,
  `residues` varchar(255) DEFAULT NULL,
  `l_search_modification_id` bigint(20) DEFAULT NULL,
  `l_search_parameters_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK30b5scgrsqdqc9vtlqa9tgay` (`l_search_modification_id`),
  KEY `FKj9l3us6wasvnnwkgxu631ucqf` (`l_search_parameters_id`),
  CONSTRAINT `FK30b5scgrsqdqc9vtlqa9tgay` FOREIGN KEY (`l_search_modification_id`) REFERENCES `search_modification` (`id`),
  CONSTRAINT `FKj9l3us6wasvnnwkgxu631ucqf` FOREIGN KEY (`l_search_parameters_id`) REFERENCES `search_parameters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_settings_has_fasta_db`
--

DROP TABLE IF EXISTS `search_settings_has_fasta_db`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_settings_has_fasta_db` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fasta_db_type` int(11) NOT NULL,
  `l_fasta_db_id` bigint(20) DEFAULT NULL,
  `l_search_and_val_settings_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK39tyom3eugvh5iwfij8hsw2ku` (`l_fasta_db_id`),
  KEY `FKsy775royxs80n8n76v2nt7drw` (`l_search_and_val_settings_id`),
  CONSTRAINT `FK39tyom3eugvh5iwfij8hsw2ku` FOREIGN KEY (`l_fasta_db_id`) REFERENCES `fasta_db` (`id`),
  CONSTRAINT `FKsy775royxs80n8n76v2nt7drw` FOREIGN KEY (`l_search_and_val_settings_id`) REFERENCES `search_and_validation_settings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spectrum`
--

DROP TABLE IF EXISTS `spectrum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spectrum` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accession` varchar(500) NOT NULL,
  `charge` int(11) DEFAULT NULL,
  `fragmentation_type` varchar(255) DEFAULT NULL,
  `intensity` double DEFAULT NULL,
  `mz_ratio` double DEFAULT NULL,
  `retention_time` double DEFAULT NULL,
  `scan_number` varchar(255) NOT NULL,
  `scan_time` double DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `l_analytical_run_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKof7knxyxuasqygwpmg300rgsr` (`l_analytical_run_id`),
  CONSTRAINT `FKof7knxyxuasqygwpmg300rgsr` FOREIGN KEY (`l_analytical_run_id`) REFERENCES `analytical_run` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spectrum_file`
--

DROP TABLE IF EXISTS `spectrum_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spectrum_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longblob NOT NULL,
  `l_spectrum_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpevnbq7t0v1hrnugv0gyf577q` (`l_spectrum_id`),
  CONSTRAINT `FKpevnbq7t0v1hrnugv0gyf577q` FOREIGN KEY (`l_spectrum_id`) REFERENCES `spectrum` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kas9w8ead0ska5n3csefp2bpp` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_has_group`
--

DROP TABLE IF EXISTS `user_has_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_has_group` (
  `l_user_id` bigint(20) NOT NULL,
  `l_group_id` bigint(20) NOT NULL,
  KEY `FKn3a9e9a4s5iiflt9d7i80on4l` (`l_group_id`),
  KEY `FK4a2dwswmgrxjtwjee6ts3ievy` (`l_user_id`),
  CONSTRAINT `FK4a2dwswmgrxjtwjee6ts3ievy` FOREIGN KEY (`l_user_id`) REFERENCES `colims_user` (`id`),
  CONSTRAINT `FKn3a9e9a4s5iiflt9d7i80on4l` FOREIGN KEY (`l_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_query`
--

DROP TABLE IF EXISTS `user_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_query` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `modification_date` datetime NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `query_string` varchar(500) NOT NULL,
  `usage_count` int(11) DEFAULT NULL,
  `l_user_query_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe1ujshd4acio6sjeg8tnr434j` (`l_user_query_user_id`),
  CONSTRAINT `FKe1ujshd4acio6sjeg8tnr434j` FOREIGN KEY (`l_user_query_user_id`) REFERENCES `colims_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-14  9:51:02



-- create default value insertions
-- insert default admin and distributed users
INSERT INTO colims_user (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin@admin.com','admin','admin','admin','ud5JjaDO0ztrMKdcQDXxhq8G21LuDCOj'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed@distributed.com','distributed','distributed','distributed','4a1zr6paBPFaEP8ixjm3hDSVLpXvzP98');

-- insert default admin and distributed groups
INSERT INTO user_group (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin group description','admin'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed group description','distributed');

-- insert default user_has_group
INSERT INTO user_has_group (l_user_id, l_group_id) VALUES (1,1),(2,2);

-- insert default admin and distributed roles
INSERT INTO group_role (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin role description','admin'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed role description','distributed');

-- insert default group_has_roles
INSERT INTO group_has_role (l_group_id, l_role_id) VALUES (1,1),(2,2);

-- insert default permissions
INSERT INTO permission (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','read','read'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','create','create'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','update','update'),(4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','delete','delete');

-- insert default role_has_permissions
INSERT INTO role_has_permission (l_role_id, l_permission_id) VALUES (1,1),(1,2),(1,3),(1,4),(2,1),(2,2),(2,3);

-- insert default search engines
INSERT INTO search_engine (id, accession, label, name, ontology, type, version) VALUES (1,'N/A','N/A','PeptideShaker','N/A','PEPTIDESHAKER', '0.0.0'),(2,'MS:1001583','MS','MaxQuant','PSI Mass Spectrometry Ontology [MS]','MAXQUANT', '0.0.0');

-- insert search parameter cv params
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (1, 'MS:1001251', 'PSI-MS', 'Trypsin', 'PSI-MS', 'SEARCH_PARAM_ENZYME');
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'PSI-MS', 'SEARCH_TYPE');
