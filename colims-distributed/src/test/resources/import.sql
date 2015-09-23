-- insert test institution
INSERT INTO institution (id, creation_date, modification_date, user_name, abbreviation, city, country, name, number, postal_code, street) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','CFO','Paris','France','Centre for Proteomics','8', '8520', 'Rue De Masspec');

-- insert test users
-- noinspection SqlDialectInspection
INSERT INTO colims_user (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password, l_institution_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin11@test.com','admin1_first_name','admin1_last_name','admin','/VcrldJXLdkuNRe5JHMtO4S/0plRymxt',1), (2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','lab1@test.com','lab1_first_name','lab1_last_name','lab1','21HkOuddgSKsFAEunzfJwgeHGus+Ny5a', 1), (3,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','lab2@test.com','lab2_first_name','lab2_last_name','lab2','5VCdz+RPeCu1dkUfpzNedNA49K5S/TTn', 1), (4,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','collab1@test.com','collab1_first_name','collab1_last_name','collab1','ohgCuzOjoAC5s+k6mzEJqFwFeZsR7TWI',1);

-- insert test groups
INSERT INTO user_group (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin group description','admin'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','lab group description','lab'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','collaboration 1 group description','collaboration 1');

-- insert test user_has_groups
INSERT INTO user_has_group (l_user_id, l_group_id) VALUES (1,1),(2,2),(3,2),(4,3);

-- insert test roles
INSERT INTO group_role (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin role description','admin'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','lab role description','lab'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','collaboration role description','collaboration');

-- insert test group_has_roles
INSERT INTO group_has_role (l_group_id, l_role_id) VALUES (1,1),(2,2),(3,3);

-- insert test permissions
INSERT INTO permission (id, creation_date, modification_date, user_name, description, name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','read','read'),(2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','create','create'),(3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','update','update'),(4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','delete','delete');

-- insert test role_has_permissions
INSERT INTO role_has_permission (l_role_id, l_permission_id) VALUES (1,1),(1,2),(1,3),(1,4),(2,1),(2,2),(2,3),(3,1);

-- insert test instrument cv params
INSERT INTO instrument_cv_param (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000073','MS','electrospray ionization', 'PSI Mass Spectrometry','SOURCE'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000111','MS','Microchannel Plate Detector', 'PSI Mass Spectrometry','DETECTOR'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000621','MS','photodiode array detector', 'PSI Mass Spectrometry Ontology','DETECTOR'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000140','MS','4700 Proteomics Analyzer', 'PSI Mass Spectrometry Ontology','ANALYZER'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000658','MS','4800 Proteomics Analyzer', 'PSI Mass Spectrometry Ontology','ANALYZER'), (6,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000449','MS','LTQ Orbitrap', 'PSI Mass Spectrometry Ontology','TYPE'), (7,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1002416','MS','Orbitrap Fusion', 'PSI Mass Spectrometry Ontology','TYPE');

-- insert test instruments
INSERT INTO instrument (id, creation_date, modification_date, user_name, name, l_type_cv_id, l_detector_cv_id, l_source_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_1','6','3','1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_2','7','3','1');

-- insert test instrument_has_analyzer
INSERT INTO instrument_has_analyzer (l_instrument_id, l_instrument_cv_param_id) VALUES (1,4),(1,5),(2,5);

-- insert test protocol cv params
INSERT INTO protocol_cv_param (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_1','MS','reduction_1', 'PSI Mass Spectrometry','REDUCTION'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_2','MS','trypsin', 'PSI Mass Spectrometry','ENZYME'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_3','MS','enzyme_2', 'PSI Mass Spectrometry','ENZYME'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_4','MS','MALDI', 'PSI Mass Spectrometry','CHEMICAL_LABELING'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_5','MS','MALDI', 'PSI Mass Spectrometry','CELL_BASED');

-- insert test protocols
INSERT INTO protocol (id, creation_date, modification_date, user_name, l_cell_based_cv_id, l_enzyme_cv_id, l_reduction_cv_id, name) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin',5, 2, 1, 'protocol_1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin',5, 3, 1, 'protocol_2');

-- insert test protocol_has_chemical_labeling
INSERT INTO protocol_has_chemical_labeling (l_protocol_id, l_chemical_labeling_cv_param_id) VALUES (1,4),(1,5),(2,5);

-- insert test projects
INSERT INTO project (id, creation_date, modification_date, user_name, description, label, title, l_owner_user_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 1 description', 'PR1', 'Project 1 title', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 2 description', 'PR2', 'Project 2 title', '1'),(3,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','project 3 description', 'PR3', 'Project 3 title', '2');

-- insert project users
INSERT INTO project_has_user (l_project_id, l_user_id) VALUES (1,2),(1,3),(2,2);

-- insert test experiments
INSERT INTO experiment (id, creation_date, modification_date, user_name, description, number, storage_location, title, l_project_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','experiment 1 description', '114', 'C://project//114','Experiment 1 title', '1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','experiment 2 description', '115', 'C://project//115','Experiment 2 title', '1');

-- insert test sample
INSERT INTO sample (id, creation_date, modification_date, user_name, name, l_experiment_id, l_protocol_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','sample 1',1,1);

-- insert test material cv params
INSERT INTO material_cv_param (id, creation_date, modification_date, user_name, accession, label, name, ontology, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_1','MS','species_1', 'PSI Mass Spectrometry','SPECIES'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_2','MS','tissue_1', 'PSI Mass Spectrometry','TISSUE'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_3','MS','cell_type_1', 'PSI Mass Spectrometry','CELL_TYPE'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_4','MS','compartment', 'PSI Mass Spectrometry','COMPARTMENT');

-- insert test material
INSERT INTO material (id, creation_date, modification_date, user_name, name, l_cell_type_cv_id, l_compartment_cv_id, l_species_cv_id, l_tissue_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 1', '3', '4', '1', '2'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 2', '3', '4', '1', '2');

-- insert test modifications
INSERT INTO modification (id, name, accession, average_mass_shift, monoisotopic_mass_shift) VALUES (1, 'methionine oxidation with neutral loss of 64 Da', 'methionine', '-64.1', '-63.998286');

-- insert test proteins
INSERT INTO protein (id, protein_sequence) VALUES (1, 'AAAAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA');
INSERT INTO protein (id, protein_sequence) VALUES (2, 'AAMAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA');
INSERT INTO protein (id, protein_sequence) VALUES (3, 'AACAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA');
INSERT INTO protein (id, protein_sequence) VALUES (4, 'AABAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA');

-- insert test protein accession
INSERT INTO protein_accession (id, accession, l_protein_id) VALUES (1, 'PROT_ACC_11', 1);
INSERT INTO protein_accession (id, accession, l_protein_id) VALUES (2, 'PROT_ACC_12', 1);
INSERT INTO protein_accession (id, accession, l_protein_id) VALUES (3, 'PROT_ACC_2', 2);
INSERT INTO protein_accession (id, accession, l_protein_id) VALUES (4, 'PROT_ACC_3', 3);
INSERT INTO protein_accession (id, accession, l_protein_id) VALUES (5, 'PROT_ACC_4', 4);

-- insert test search engine
INSERT INTO search_engine (id, accession, label, name, ontology, type, version) VALUES (1,'N/A','N/A','PeptideShaker','N/A','PEPTIDESHAKER','0.35.0-beta');
INSERT INTO search_engine (id, accession, label, name, ontology, type, version) VALUES (2,'MS:1001583','MS','MaxQuant','PSI Mass Spectrometry Ontology [MS]','MAXQUANT','0.0.0');

-- insert test fasta db
INSERT INTO fasta_db (id, file_name, file_path, md5_checksum, name, species, taxonomy_accession, version) VALUES (1, 'testfasta.fasta', 'C:\Users\colims\testfasta.fasta', null, 'test fasta', 'Homo sapiens (Human)', '9606', '1.2.3');

-- insert test search parameter cv params
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (1, 'MS:1001251', 'PSI-MS', 'Trypsin', 'PSI-MS', 'SEARCH_PARAM_ENZYME');
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'PSI-MS', 'SEARCH_TYPE');

-- insert test search modifications
INSERT INTO search_modification (id, name, accession, utilities_name, average_mass_shift, monoisotopic_mass_shift) VALUES (1, 'monohydroxylated residue', 'Oxidation of M', 'UNIMOD:35', '16.0', '15.994915'), (2, 'phosphorylated residue', 'MOD:00696', 'Phosphorylation of S', '79.98', '79.966331'), (3, 'fragment neutral loss', 'MS:1001524', null, 1.0, 2.5);

-- insert a test analytical run
INSERT INTO analytical_run (id, creation_date, modification_date, user_name, name, start_date, l_instrument_id, l_sample_id) VALUES (1, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 'run 1', '2012-11-08 16:51:13', 1, 1);

-- insert a test spectrum
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (1, 'MS:00000000', 1, 'CID', 1, 1, 5, 'no', 3, 'Test Spectrum', 1);

-- insert a spectrum file
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (1, 'AABBCC', 1);

-- insert a search parameters
INSERT INTO search_parameters (id, precursor_mass_tolerance, precursor_mass_tolerance_unit, fragment_mass_tolerance, fragment_mass_tolerance_unit, l_search_enzyme_cv_id) VALUES (1, 4, 1, 5, 1, 1);

-- insert a search params has mod
INSERT INTO search_params_has_modification (id, modification_type, residues, l_search_modification_id, l_search_parameters_id) VALUES (1, 1, 'CK', 3, 1);

-- insert a search and val settings set of settings
INSERT INTO search_and_validation_settings (id, creation_date, modification_date, user_name, l_analytical_run_id, l_fasta_db_id, l_search_engine_id, l_search_parameters_id) VALUES (1, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 1, 1, 1, 1);

-- insert a test identification file
INSERT INTO identification_file (id, file_type, content, file_name, file_path, l_search_and_val_settings_id) VALUES (1, 'TEXT', 'AAAAAA', 'test.xml', 'C:\test', 1);

-- insert test peptides
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_identification_file_id, l_spectrum_id) VALUES (1, 1, 0.5, 0.5, 'LENNART', 1, 1, 1);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_identification_file_id, l_spectrum_id) VALUES (2, 1, 0.5, 0.5, 'LENNART', 1, 1, 1);

-- insert a test peptide has modiciation
INSERT INTO peptide_has_modification (id,  delta_score,  location,  modification_type,  prob_score,  l_modification_id,  l_peptide_id) VALUES (1, 0.5, 1, 1, 1, 1, 1);

-- insert test protein groups
INSERT INTO protein_group (id) VALUES (1), (2);

-- insert test protein group has proteins
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (1, 1, 1, 'PROT_ACC_11', TRUE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (2, 2, 2, 'PROT_ACC_2', TRUE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (3, 3, 2, 'PROT_ACC_3', FALSE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (4, 4, 2, 'PROT_ACC_4', FALSE);

-- insert test peptide has protein groups
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (1, 0.1, 0.9, 1, 1);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (2, 0.1, 0.9, 2, 2);