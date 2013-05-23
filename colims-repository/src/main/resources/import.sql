-- insert test institution
INSERT INTO `institution` ('id', 'creation_date', 'modification_date', 'user_name', 'abbreviation', 'city', 'country', 'name', 'number', 'postal_code', 'street') VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','CFO','Paris','France','Centre for Proteomics','8', '8520', 'Rue De Masspec');

-- insert test users
INSERT INTO `user` ('id', 'creation_date', 'modification_date', 'user_name', 'email', 'first_name', 'last_name', 'password', 'l_institution_id') VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','user1@test.com','user1_first_name','user1_last_name','user1_name','7xyb63kC2ILEWopdoLiakOW4s8C9H5j/',1),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','user2@test.com','user2_first_name','user2_last_name','user2_name','jFkoyvRMKioGbr0Y6Ruhdw=='),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','user3@test.com','user3_first_name','user3_last_name','user3_name','Ne+eQXzpoGE/7l/qEOv88dtSrYzmoPON');

-- insert test instrument cv terms
INSERT INTO `instrument_cv_term` ('id', 'creation_date', 'modification_date', 'user_name','ontology', 'instrumentCvProperty') VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_cv_acc_1','MS','MALDI', 'PSI Mass Spectrometry','SOURCE'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_cv_acc_2','MS','MALDI', 'PSI Mass Spectrometry','SOURCE'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_cv_acc_3','MS','MALDI', 'PSI Mass Spectrometry','DETECTOR'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_cv_acc_4','MS','MALDI', 'PSI Mass Spectrometry','ANALYZER'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_cv_acc_5','MS','MALDI', 'PSI Mass Spectrometry','ANALYZER');

-- insert test instrument_has_analyzer
INSERT INTO `instrument_has_analyzer` ('l_instrument_id', 'l_instrument_cv_term_id') VALUES (1,4),(1,4),(2,5);

-- insert test instruments
INSERT INTO `instrument` ('id', 'creation_date', 'modification_date', 'user_name', 'name', 'type', 'l_detector_cv_id', 'l_source_cv_id') VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_1','instrument_1_type', '3', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_2','instrument_2_type', '3', '2');

-- insert test protocol cv terms
INSERT INTO `protocol_cv_term` ('id', 'creation_date', 'modification_date', 'user_name','ontology', 'protocolCvProperty') VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_1','MS','reduction_1', 'PSI Mass Spectrometry','REDUCTION'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_2','MS','trypsin', 'PSI Mass Spectrometry','ENZYME'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_3','MS','enzyme_2', 'PSI Mass Spectrometry','ENZYME'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_4','MS','MALDI', 'PSI Mass Spectrometry','CHEMICAL_LABELING'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_5','MS','MALDI', 'PSI Mass Spectrometry','CELL_BASED');

-- insert test protocol_has_chemical_labeling
INSERT INTO `protocol_has_chemical_labeling` ('l_protocol_id', 'l_chemical_labeling_cv_term_id') VALUES (1,4),(1,4),(2,5);

-- insert test protocols
INSERT INTO `protocol` ('id', 'creation_date', 'modification_date', 'user_name', 'name', 'type', 'l_detector_cv_id', 'l_source_cv_id') VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_1','instrument_1_type', '3', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_2','instrument_2_type', '3', '2');

-- insert test groups
INSERT INTO `user_group` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testGroup1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testGroup2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testGroup3');

-- insert test user_has_groups
INSERT INTO `user_has_group` VALUES (1,1),(2,1),(3,2);

-- insert test roles
INSERT INTO `group_role` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testRole1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testRole2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testRole3');

-- insert test group_has_roles
INSERT INTO `group_has_role` VALUES (1,1),(1,2),(3,2);

-- insert test permissions
INSERT INTO `permission` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testPermission1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testPermission2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testPermission3');

-- insert test role_has_permissions
INSERT INTO `role_has_permission` VALUES (1,1),(2,1),(3,2);

