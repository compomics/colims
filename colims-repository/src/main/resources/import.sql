-- insert test institution
INSERT INTO `institution` (id, creation_date, modification_date, user_name, abbreviation, city, country, name, number, postal_code, street) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','CFO','Paris','France','Centre for Proteomics','8', '8520', 'Rue De Masspec');

-- insert test users
INSERT INTO `user` (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password, l_institution_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','user1@test.com','user1_first_name','user1_last_name','user1_name','7xyb63kC2ILEWopdoLiakOW4s8C9H5j/',1),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','user2@test.com','user2_first_name','user2_last_name','user2_name','jFkoyvRMKioGbr0Y6Ruhdw==', 1),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','user3@test.com','user3_first_name','user3_last_name','user3_name','Ne+eQXzpoGE/7l/qEOv88dtSrYzmoPON',1);

-- insert test groups
INSERT INTO `user_group` (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testGroup1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testGroup2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testGroup3');

-- insert test user_has_groups
INSERT INTO `user_has_group` VALUES (1,1),(2,1),(3,2);

-- insert test roles
INSERT INTO `group_role` (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testRole1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testRole2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testRole3');

-- insert test group_has_roles
INSERT INTO `group_has_role` VALUES (1,1),(1,2),(3,2);

-- insert test permissions
INSERT INTO `permission` (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testPermission1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testPermission2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testPermission3');

-- insert test role_has_permissions
INSERT INTO `role_has_permission` VALUES (1,1),(2,1),(3,2);

-- insert test instrument types
INSERT INTO `instrument_type` (id, creation_date, modification_date, user_name, name, description) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_type_1_name','instrument type 1 description'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','instr_type_2_name','instrument type 2 description');

-- insert test instrument cv terms
INSERT INTO `instrument_cv_term` (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000073','MS','electrospray ionization', 'PSI Mass Spectrometry','SOURCE'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000111','MS','Microchannel Plate Detector', 'PSI Mass Spectrometry','DETECTOR'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000621','MS','photodiode array detector', 'PSI Mass Spectrometry Ontology','DETECTOR'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000140','MS','4700 Proteomics Analyzer', 'PSI Mass Spectrometry Ontology','ANALYZER'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000658','MS','4800 Proteomics Analyzer', 'PSI Mass Spectrometry Ontology','ANALYZER');

-- insert test instruments
INSERT INTO `instrument` (id, creation_date, modification_date, user_name, name, l_instrument_type_id, l_detector_cv_id, l_source_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_1','1','3','1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_2','2','3','1');

-- insert test instrument_has_analyzer
INSERT INTO `instrument_has_analyzer` (l_instrument_id, l_instrument_cv_term_id) VALUES (1,4),(1,5),(2,5);

-- insert test protocol cv terms
INSERT INTO `protocol_cv_term` (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_1','MS','reduction_1', 'PSI Mass Spectrometry','REDUCTION'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_2','MS','trypsin', 'PSI Mass Spectrometry','ENZYME'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_3','MS','enzyme_2', 'PSI Mass Spectrometry','ENZYME'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_4','MS','MALDI', 'PSI Mass Spectrometry','CHEMICAL_LABELING'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_5','MS','MALDI', 'PSI Mass Spectrometry','CELL_BASED');

-- insert test protocols
INSERT INTO `protocol` (id, creation_date, modification_date, user_name, l_cell_based_cv_id, l_enzyme_cv_id, l_reduction_cv_id, name) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin',5, 2, 1, 'protocol_1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin',5, 3, 1, 'protocol_2');

-- insert test protocol_has_chemical_labeling
INSERT INTO `protocol_has_chemical_labeling` (l_protocol_id, l_chemical_labeling_cv_term_id) VALUES (1,4),(1,5),(2,5);

-- insert test projects
INSERT INTO `project` (id, creation_date, modification_date, user_name, description, label, title, l_owner_user_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 1 description', 'PR1', 'Project 1 title', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 2 description', 'PR2', 'Project 2 title', '1'),(3,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 3 description', 'PR3', 'Project 3 title', '2');

-- insert project users
INSERT INTO `project_has_user` (l_project_id, l_user_id) VALUES (1,2),(1,3),(2,2);

-- insert test experiments
INSERT INTO `experiment` (id, creation_date, modification_date, user_name, description, number, storage_location, title, l_project_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','experiment 1 description', '114', 'C://project//114','Experiment 1 title', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','experiment 2 description', '115', 'C://project//115','Experiment 2 title', '1');

-- insert test material cv terms
INSERT INTO `material_cv_term` (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_1','MS','species_1', 'PSI Mass Spectrometry','SPECIES'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_2','MS','tissue_1', 'PSI Mass Spectrometry','TISSUE'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_3','MS','cell_type_1', 'PSI Mass Spectrometry','CELL_TYPE'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_4','MS','compartment', 'PSI Mass Spectrometry','COMPARTMENT');

-- insert test material
INSERT INTO `material` (id, creation_date, modification_date, user_name, name, l_cell_type_cv_id, l_compartment_cv_id, l_project_id, l_species_cv_id, l_tissue_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 1', '3', '4', '1', '1', '2'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 2', '3', '4', '1', '1', '2');

-- insert test modifications
INSERT INTO `modification` (id, creation_date, modification_date, user_name, name, accession, average_mass, average_mass_shift, monoisotopic_mass, monoisotopic_mass_shift) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'methionine oxidation with neutral loss of 64 Da', 'MOD:00935', '83.09', '-64.1', '83.037114', '-63.998286');