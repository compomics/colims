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

-- insert some test data
INSERT INTO project (id,creation_date,modification_date,user_name,description,label,title,l_owner_user_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','test project 1 description','test project 1 label','test project 1',1);

INSERT INTO project_has_user (l_project_id,l_user_id) VALUES (1,1);

INSERT INTO experiment (id,creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','experiment 1 description',2222,'','experiment 1',1);
INSERT INTO experiment (id,creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','',15166,'','experiment 2',1);

INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1001603','SOURCE','MS','ProteomeDiscoverer:Spectrum Selector:Ionization Source','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1002308','DETECTOR','MS','fluorescence detector','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (3,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1000254','ANALYZER','MS','electrostatic energy analyzer','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (4,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1000449','TYPE','MS','LTQ Orbitrap','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (5,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1002416','TYPE','MS','Orbitrap Fusion','PSI Mass Spectrometry Ontology [MS]');

INSERT INTO instrument (id,creation_date,modification_date,user_name,name,l_detector_cv_id,l_type_cv_id,l_source_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 1',2,4,1);
INSERT INTO instrument (id,creation_date,modification_date,user_name,name,l_detector_cv_id,l_type_cv_id,l_source_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 3',2,5,1);

INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_param_id) VALUES (1,3);
INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_param_id) VALUES (2,3);

INSERT INTO material_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','9606','SPECIES','NEWT','Homo sapiens (Human)','NEWT UniProt Taxonomy Database[NEWT]');

INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_species_cv_id,l_tissue_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 1',null,null,1,null);
INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_species_cv_id,l_tissue_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 2',null,null,1,null);

INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 7',null,null,null);
INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 1',null,null,null);

INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (1, 'MS:1001251', 'PSI-MS', 'Trypsin', 'PSI-MS', 'SEARCH_PARAM_ENZYME');
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'PSI-MS', 'SEARCH_TYPE');

INSERT INTO sample (id, creation_date, modification_date, user_name, name, l_experiment_id, l_protocol_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','sample 1',1,1);
