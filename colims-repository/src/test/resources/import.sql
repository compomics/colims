-- insert test users
INSERT INTO `user` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testuser1@test.com','test','user1','user1','7xyb63kC2ILEWopdoLiakOW4s8C9H5j/'),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','testuser2@test.com','test','user2','user2','jFkoyvRMKioGbr0Y6Ruhdw=='),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','testuser3@test.com','test','user3','user3','Ne+eQXzpoGE/7l/qEOv88dtSrYzmoPON');

-- insert test instruments
INSERT INTO `instrument` VALUES (3,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','instrument 1 description','instrument_1'),(4,'2012-11-08 16:51:11','2012-11-08 16:51:11','testUser','instrument 2 description','instrument_2');

-- insert test groups
INSERT INTO `user_group` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testGroup1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testGroup2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testGroup3');

-- insert test user_has_groups
INSERT INTO `user_has_group` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin',1,1),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin',2,1),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin',3,2);

-- insert test roles
INSERT INTO `group_role` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testRole1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testRole2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testRole3');

-- insert test group_has_roles
INSERT INTO `group_has_role` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin',1,1),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin',1,2),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin',3,2);

-- insert test permissions
INSERT INTO `permission` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription1','testPermission1'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription2','testPermission2'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','testDescription3','testPermission3');

-- insert test role_has_permissions
INSERT INTO `role_has_permission` VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin',1,1),(2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin',2,1),(3,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin',3,2);

