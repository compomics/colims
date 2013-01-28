
-- insert test users
INSERT INTO `user` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testuser1@test.com','test','user1','user1','7xyb63kC2ILEWopdoLiakOW4s8C9H5j/'),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','testuser2@test.com','test','user2','user2','jFkoyvRMKioGbr0Y6Ruhdw=='),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','testuser3@test.com','test','user3','user3','Ne+eQXzpoGE/7l/qEOv88dtSrYzmoPON');

-- insert test instruments
INSERT INTO `instrument` VALUES (3,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','instrument 1 description','instrument_1'),(4,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','instrument 2 description','instrument_2');

-- insert test instruments_params
INSERT INTO `instrument_param` VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','accession_1','cv_label_1','instrument_param_value_1',3),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','accession_2','cv_label_1','instrument_param_value_2',3),(3,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','accession_3','cv_label_1','instrument_param_value_3',4);
