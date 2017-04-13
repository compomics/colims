# noinspection SqlNoDataSourceInspectionForFile

-- insert test institution
INSERT INTO institution (id, creation_date, modification_date, user_name, abbreviation, city, country, name, number, postal_code, street, email) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','CFO','Paris','France','Centre for Proteomics','8', '8520', 'Rue De Masspec', 'proteomics@proteomics.org');

-- insert test users
-- noinspection SqlDialectInspection
INSERT INTO colims_user (id, creation_date, modification_date, user_name, email, first_name, last_name, name, password, l_institution_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin11@test.com','admin1_first_name','admin1_last_name','admin','/VcrldJXLdkuNRe5JHMtO4S/0plRymxt', 1), (2,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','lab1@test.com','lab1_first_name','lab1_last_name','lab1','21HkOuddgSKsFAEunzfJwgeHGus+Ny5a', 1), (3,'2012-06-28 11:05:58','2012-06-28 11:05:58','admin','lab2@test.com','lab2_first_name','lab2_last_name','lab2','5VCdz+RPeCu1dkUfpzNedNA49K5S/TTn', 1), (4,'2012-06-28 11:19:49','2012-10-11 12:02:33','admin','collab1@test.com','collab1_first_name','collab1_last_name','collab1','ohgCuzOjoAC5s+k6mzEJqFwFeZsR7TWI', 1);

-- insert test user query
INSERT INTO user_query (id, creation_date, modification_date, user_name, query_string, usage_count, l_user_query_user_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin', 'test user query string', 3, 1), (2,'2011-06-27 14:42:16','2011-06-27 14:49:46','admin', 'test user query string', 1, 1), (3,'2011-06-27 14:42:16','2012-06-27 14:49:46','admin', 'test user query string', 1, 1)

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
INSERT INTO instrument_cv_param (id, creation_date, modification_date, user_name, accession, label, name, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000073','MS','electrospray ionization','SOURCE'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000111','MS','Microchannel Plate Detector','DETECTOR'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000621','MS','photodiode array detector','DETECTOR'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000140','MS','4700 Proteomics Analyzer','ANALYZER'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000658','MS','4800 Proteomics Analyzer','ANALYZER'), (6,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1000449','MS','LTQ Orbitrap','TYPE'), (7,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','MS:1002416','MS','Orbitrap Fusion','TYPE');

-- insert test instruments
INSERT INTO instrument (id, creation_date, modification_date, user_name, name, l_type_cv_id, l_detector_cv_id, l_source_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_1','6','3','1'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','instrument_2','7','3','1');

-- insert test instrument_has_analyzer
INSERT INTO instrument_has_analyzer (l_instrument_id, l_instrument_cv_param_id) VALUES (1,4),(1,5),(2,5);

-- insert test protocol cv params
INSERT INTO protocol_cv_param (id, creation_date, modification_date, user_name, accession, label, name, cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_1','MS','reduction_1','REDUCTION'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_2','MS','trypsin','ENZYME'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_3','MS','enzyme_2','ENZYME'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_4','MS','MALDI','CHEMICAL_LABELING'), (5,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','protocol_cv_acc_5','MS','MALDI','CELL_BASED');

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
INSERT INTO material_cv_param (id, creation_date, modification_date, user_name, accession, label, name,cv_property) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_1','MS','species_1','SPECIES'), (2,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_2','MS','tissue_1','TISSUE'), (3,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_3','MS','cell_type_1','CELL_TYPE'), (4,'2012-06-27 14:42:16','2012-06-27 14:49:46','admin','material_cv_acc_4','MS','compartment','COMPARTMENT');

-- insert test material
INSERT INTO material (id, creation_date, modification_date, user_name, name, l_cell_type_cv_id, l_compartment_cv_id, l_species_cv_id, l_tissue_cv_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 1', '3', '4', '1', '2'),(2,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin', 'material 2', '3', '4', '1', '2');

-- insert test modifications
INSERT INTO modification (id, name, accession, utilities_name, average_mass_shift, monoisotopic_mass_shift) VALUES (1, 'monohydroxylated residue', 'MOD:00425', 'Oxidation of M', '16.0', '15.994915');

-- insert test proteins
INSERT INTO protein (id, protein_sequence) VALUES (1, 'MATASPAADGGRGRPWEGGLVSWPPAPPLTLPWTWMGPSWGQHPGHWGFPALTEPSASPAAGLGIFEVRRVLDASGCSMLAPLQTGAARFSSYLLSRARKVLGSHLFSPCGVPEFCSISTRKLAAHGFGASMAAMVSFPPQRYHYFLVLDFEATCDKPQIHPQEIIEFPILKLNGRTMEIESTFHMYVQPVVHPQLTPFCTELTGIIQAMVDGQPSLQQVLERVDEWMAKEGLLDPNVKSIFVTCGDWDLKVMLPGQCQYLGLPVADYFKQWINLKKAYSFAMGCWPKNGLLDMNKGLSLQHIGRPHSGIDDCKNIANIMKTLAYRGFIFKQTSKPF');
INSERT INTO protein (id, protein_sequence) VALUES (2, 'MTCGFRTGNFSCASACGPRPGRCCISAAPYRGISCYRGLSGGFGSQSVCGAFRSGSCGRSFGYRSGGICGPSPPCITTVSVNESLLTPLNLEIDPNAQCVKHEEKEQIKCLNSRFAAFIDKVRFLEQQNKLLETKWQFYQNRKCCESNMEPLFEGYIETLRREAECVEADSGRLAAELNHAQESMEGYKKRYEEEVALRATAENEFVALKKDVDCAYLRKSDLEANAEALTQETDFLRRMYDEETRILHSHISDTSVIVKMDNSRDLNMDCVVAEIKAQYDDIASRSRAEAESWYRTKCEEMKATVIRHGETLRRTREEINELNRMIQRLTAEIENAKCQNTKLEAAVTQSEQQGEAALADARCKLAELEGALQKAKQDMACLLKEYQEVMNSKLGLDVEIITYRRLLEGEEQRLCEGVGAVNVCVSSSRGGVVCGDLCVSGSRPVTGSVCSAPCSGNVAVSTGLCAPCGSGPCHPGRC');
INSERT INTO protein (id, protein_sequence) VALUES (3, 'MTCGFRTGNFSCASACGPRPGRCCISAAPYRGISCYRGLSGGFGSQSVCGAFRSGSCGRSFGYRSGGICGPSPPCITTVSVNESLLTPLNLEIDPNAQCVKHEEKEQIKCLNSRFAAFIDKVRFLEQQNKLLETKWQFYQNRKCCESNMEPLFEGYIETLRREAECVEADSGRLAAELNHAQESMEGYKKRYEEEVALRATAENEFVALKKDVDCAYLRKSDLEANAEALTQETDFLRRMYDEETRILHSHISDTSVIVKMDNSRDLNMDCVVAEIKAQYDDIASRSRAEAESWYRTKCEEMKATVIRHGETLRRTREEINELNRMIQRLTAEIENAKCQNTKLEAAVTQSEQQGEAALADARCKLAELEGALQKAKQDMACLLKEYQEVMNSKLGLDVEIITYRRLLEGEEQRLCEGVGAVNVCVSSSRGGVVCGDLCVSGSRPVTGSVCSAPCSGNVAVRC');
INSERT INTO protein (id, protein_sequence) VALUES (4, 'MTCGFRTGNFSCASACGPRPGRCCISAAPYRGISCYRGLSGGFGSQSVCGAFRSGSCGRSFGYRSGGICGPSPPCITTVSVNESLLTPLNLEIDPNAQCVKHEEKEQIKCLNSRFAAFIDKVRFLEQQNKLLETKWQFYQNRKCCESNMEPLFEGYIETLRREAECVEADSGRLAAELNHAQESMEGYKKRYEEEVALRATAENEFVALKKDVDCAYLRKSDLEANAEALTQETDFLRRMYDEETRILHSHISDTSVIVKMDNSRDLNMDCVVAEIKAQYDDIASRSRAEAESWYRTKCEEMKATVIRHGETLRRTREEINELNRMIQRLTAEIENAKCQNTKLEAAVTQSEQQGEAALADARCKLAELEGALQKAKQDMACLLKEYQEVMNSKLGLDVEIITYRRLLEGEEQRLCEGVGAVNVCVAVSTGLCAPCGSGPCHPGRC');
INSERT INTO protein (id, protein_sequence) VALUES (5, 'MNITNCTTEASMAIRPKTITEKMLICMTLVVITTLTTLLNLAVIMAIGTTKKLHQPANYLICSLAVTDLLVAVLVMPLSIIYIVMDRWKLGYFLCEVWLSVDMTCCTCSILHLCVIALDRYWAITNAIEYARKRTAKRAALMILTVWTISIFISMPPLFWRSHRRLSPPPSQCTIQHDHVIYTIYSTLGAFYIPLTLILILYYRIYHAAKSLYQKRGSSRHLSNRSTDSQNSFASCKLTQTFCVSDFSTSDPTTEFEKFHASIRIPPFDNDLDHPGERQQISSTRERKAARILGLILGAFILSWLPFFIKELIVGLSIYTVSSEVADFLTWLGYVNSLINPLLYTSFNEDFKLAFKKLIRCREHT');

-- insert test search engine
INSERT INTO search_engine (id, accession, label, name, type, version) VALUES (1,'N/A','N/A','PeptideShaker','PEPTIDESHAKER','0.35.0-beta');
INSERT INTO search_engine (id, accession, label, name, type, version) VALUES (2,'MS:1001583','MS','MaxQuant','MAXQUANT','0.0.0');

-- insert test taxonomy cv param
INSERT INTO taxonomy_cv_param (id, accession, label, name) VALUES (1,'NCBITaxon:9606','NCBITaxon','Homo sapiens');

-- insert test fasta db
INSERT INTO fasta_db (id, file_name, file_path, md5_checksum, name, version, l_taxonomy_cv_id, database_name, header_parse_rule) VALUES (1, 'SP_human.fasta', 'SP_human.fasta', null, 'test fasta', '1.2.3', 1, 'UNIPROT', '&gt;.*\|(.*)\|'), (2, 'contaminants.fasta', 'contaminants.fasta', null, 'contaminants test fasta', null, null, 'CONTAMINANTS', null);

-- insert test search parameter cv params
INSERT INTO search_cv_param (id, accession, label, name, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'SEARCH_TYPE');

-- insert test search modifications
INSERT INTO search_modification (id, name, accession, utilities_name, average_mass_shift, monoisotopic_mass_shift) VALUES (1, 'monohydroxylated residue', 'MOD:00425', 'Oxidation of M', '16.0', '15.994915'), (2, 'phosphorylated residue', 'MOD:00696', 'Phosphorylation of S', '79.98', '79.966331'), (3, 'iTRAQ4plex', 'UNIMOD:214', 'iTRAQ 4-plex of Y', '1.0', '2.5');

-- insert a test analytical run
INSERT INTO analytical_run (id, creation_date, modification_date, user_name, name, start_date, l_instrument_id, l_sample_id) VALUES (1, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 'run 1', '2012-11-08 16:51:13', 1, 1);
INSERT INTO analytical_run (id, creation_date, modification_date, user_name, name, start_date, l_instrument_id, l_sample_id) VALUES (2, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 'run 1', '2012-11-08 16:51:13', 1, 1);

-- insert a test spectrum
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (1, 'MS:00000001', 1, 'CID', 1788, 115.7, 52, 45, 3887, 'Test Spectrum 1', 1);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (2, 'MS:00000002', 1, 'CID', 187, 325.56, 745, 44745, 345, 'Test Spectrum 2', 1);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (3, 'MS:00000003', 2, 'CID', 149494, 1494.5, 7435, 447545, 3445, 'Test Spectrum 3', 1);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (4, 'MS:00000004', 2, 'CID', 149423, 894.5, 7535, 447845, 34445, 'Test Spectrum 4', 1);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (5, 'MS:00000005', 3, 'CID', 149423, 894.5, 7535, 447845, 34445, 'Test Spectrum 5', 1);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (6, 'MS:00000005', 3, 'CID', 149423, 894.5, 7535, 447845, 34445, 'Test Spectrum 5', 2);
INSERT INTO spectrum (id, accession, charge, fragmentation_type, intensity, mz_ratio, retention_time, scan_number, scan_time, title, l_analytical_run_id) VALUES (7, 'RawFile: 20130607_FI_Ubiquitin_9 Index: 1700', 3, 'CID', 0, 465.88441, 18.705, 1700, 5, 'RawFile: 20130607_FI_Ubiquitin_9 Index: 1700 Precursor: 0 _multi_', 2);

-- insert a spectrum file
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (1, '1f8b08000000000000005d55c96e1b47103d67bea2a16b8246ed8b011e6c8b710424b461e907148107038aed28cef6f7793d9413201792989adade527c757c73731a376f4fb7dbddcddd8fc7c3afc7bfee1fbe7cf8e34c5cdcd3bbf2f221e3fb0f8fe71757ff7be1e9fecfabefc6e97e3db9b97e71f5f0e9e397a74f8f8fe7a7bbbf3f9f0f34fe7b70fafd979fcf4f071ebf3ddc7f3caca257dbbbe3bb9f5edede1e98da26598b345b557d636412398d427c7b7f7773ba3dbe7e7bbac69bee36b538b6d73fbc7cffe678906f37669a945cdd3634d4272949aa69fac6d293495c9b8750d734e610f4d3da587392704af948a129ce1656b687f0a67899e8f0ac299d11659e1b5b4e6629ad1c6d36232b57b9d5cbd1dad9a57a78a36f2999996a6c1c3c55b5bb03219fe6a8566c862cec994689072350bc859d2aab79e374f46a15cd21e232c39bb3a356b3622c9665582c43a69b52530929423159bc2d46a0b8a85927e573c4c22b02491433c4004463e90de04cb4a54e196531b135de2675d943c0b0d56b64b300dff234e1429af0eaa51d89318021666e5245d64217b595d00b70a6177306be3641fd485b50a3a0a1387622de435885553a9c071b310055c3161dbd49232605886bb4dac4afcbc6bce96a5052a88ae969b10c8101fedcb47502b434b00c71d12c2feddad561b4b314e53af0ed53d2381a14f466c0db62df06dd6436da410605405c740aa0835c8603cda920497d17818b0171251519cc0bc85208ab1d536229080ebea915434d102966d83336fc9a028599f7ca4303a754b1350a5003bf096d6314945421f7883dad14428554dd06f4875501cf45ef1bb8829a00e01a056d67100c66fb76a53cad0170c590c06e93f682b01be69d4e9aa2a080d9e1040b68fa39a8081aa8cea1f02848f837cf1cce1409453ba42c88002c0c115b6563cc08f03ca01b748374a19b8d31c184e5333162f46c4520800d3c48f0644385bc8f0f24bd4a730db262b84ebe8a600cf6652e9868491d3125c005110b2cef3d2328e0346518806a9d0728aa00176a2c7b5e88db43801f96c58851309b816d4cbfa7c52460d11833f60bd01748560c0273c32231c0061069a79def3d86bbe6a000799012547431ef7aee26a240dfc363d1665f4398d0e0c41810184fb04818429e436016108d5820f67e4174c7112ccc0b3cb88168e497c3b6c067f03c21f2843970f3004e7c4d83797020cc72d90d0b4182386ed910fa763c5d5ffe1ab67f00a9cb27f227060000', 1);
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (2, '1f8b08000000000000005d55c96e1b47103d67bea2a16b8246ed8b011e6c8b710424b461e907148107038aed28cef6f7793d9413201792989adade527c757c73731a376f4fb7dbddcddd8fc7c3afc7bfee1fbe7cf8e34c5cdcd3bbf2f221e3fb0f8fe71757ff7be1e9fecfabefc6e97e3db9b97e71f5f0e9e397a74f8f8fe7a7bbbf3f9f0f34fe7b70fafd979fcf4f071ebf3ddc7f3caca257dbbbe3bb9f5edede1e98da26598b345b557d636412398d427c7b7f7773ba3dbe7e7bbac69bee36b538b6d73fbc7cffe678906f37669a945cdd3634d4272949aa69fac6d293495c9b8750d734e610f4d3da587392704af948a129ce1656b687f0a67899e8f0ac299d11659e1b5b4e6629ad1c6d36232b57b9d5cbd1dad9a57a78a36f2999996a6c1c3c55b5bb03219fe6a8566c862cec994689072350bc859d2aab79e374f46a15cd21e232c39bb3a356b3622c9665582c43a69b52530929423159bc2d46a0b8a85927e573c4c22b02491433c4004463e90de04cb4a54e196531b135de2675d943c0b0d56b64b300dff234e1429af0eaa51d89318021666e5245d64217b595d00b70a6177306be3641fd485b50a3a0a1387622de435885553a9c071b310055c3161dbd49232605886bb4dac4afcbc6bce96a5052a88ae969b10c8101fedcb47502b434b00c71d12c2feddad561b4b314e53af0ed53d2381a14f466c0db62df06dd6436da410605405c740aa0835c8603cda920497d17818b0171251519cc0bc85208ab1d536229080ebea915434d102966d83336fc9a028599f7ca4303a754b1350a5003bf096d6314945421f7883dad14428554dd06f4875501cf45ef1bb8829a00e01a056d67100c66fb76a53cad0170c590c06e93f682b01be69d4e9aa2a080d9e1040b68fa39a8081aa8cea1f02848f837cf1cce1409453ba42c88002c0c115b6563cc08f03ca01b748374a19b8d31c184e5333162f46c4520800d3c48f0644385bc8f0f24bd4a730db262b84ebe8a600cf6652e9868491d3125c005110b2cef3d2328e0346518806a9d0728aa00176a2c7b5e88db43801f96c58851309b816d4cbfa7c52460d11833f60bd01748560c0273c32231c0061069a79def3d86bbe6a000799012547431ef7aee26a240dfc363d1665f4398d0e0c41810184fb04818429e436016108d5820f67e4174c7112ccc0b3cb88168e497c3b6c067f03c21f2843970f3004e7c4d83797020cc72d90d0b4182386ed910fa763c5d5ffe1ab67f00a9cb27f227060000', 2);
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (3, '1f8b08000000000000005d55c96e1b47103d67bea2a16b8246ed8b011e6c8b710424b461e907148107038aed28cef6f7793d9413201792989adade527c757c73731a376f4fb7dbddcddd8fc7c3afc7bfee1fbe7cf8e34c5cdcd3bbf2f221e3fb0f8fe71757ff7be1e9fecfabefc6e97e3db9b97e71f5f0e9e397a74f8f8fe7a7bbbf3f9f0f34fe7b70fafd979fcf4f071ebf3ddc7f3caca257dbbbe3bb9f5edede1e98da26598b345b557d636412398d427c7b7f7773ba3dbe7e7bbac69bee36b538b6d73fbc7cffe678906f37669a945cdd3634d4272949aa69fac6d293495c9b8750d734e610f4d3da587392704af948a129ce1656b687f0a67899e8f0ac299d11659e1b5b4e6629ad1c6d36232b57b9d5cbd1dad9a57a78a36f2999996a6c1c3c55b5bb03219fe6a8566c862cec994689072350bc859d2aab79e374f46a15cd21e232c39bb3a356b3622c9665582c43a69b52530929423159bc2d46a0b8a85927e573c4c22b02491433c4004463e90de04cb4a54e196531b135de2675d943c0b0d56b64b300dff234e1429af0eaa51d89318021666e5245d64217b595d00b70a6177306be3641fd485b50a3a0a1387622de435885553a9c071b310055c3161dbd49232605886bb4dac4afcbc6bce96a5052a88ae969b10c8101fedcb47502b434b00c71d12c2feddad561b4b314e53af0ed53d2381a14f466c0db62df06dd6436da410605405c740aa0835c8603cda920497d17818b0171251519cc0bc85208ab1d536229080ebea915434d102966d83336fc9a028599f7ca4303a754b1350a5003bf096d6314945421f7883dad14428554dd06f4875501cf45ef1bb8829a00e01a056d67100c66fb76a53cad0170c590c06e93f682b01be69d4e9aa2a080d9e1040b68fa39a8081aa8cea1f02848f837cf1cce1409453ba42c88002c0c115b6563cc08f03ca01b748374a19b8d31c184e5333162f46c4520800d3c48f0644385bc8f0f24bd4a730db262b84ebe8a600cf6652e9868491d3125c005110b2cef3d2328e0346518806a9d0728aa00176a2c7b5e88db43801f96c58851309b816d4cbfa7c52460d11833f60bd01748560c0273c32231c0061069a79def3d86bbe6a000799012547431ef7aee26a240dfc363d1665f4398d0e0c41810184fb04818429e436016108d5820f67e4174c7112ccc0b3cb88168e497c3b6c067f03c21f2843970f3004e7c4d83797020cc72d90d0b4182386ed910fa763c5d5ffe1ab67f00a9cb27f227060000', 3);
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (4, '1f8b08000000000000005d55c96e1b47103d67bea2a16b8246ed8b011e6c8b710424b461e907148107038aed28cef6f7793d9413201792989adade527c757c73731a376f4fb7dbddcddd8fc7c3afc7bfee1fbe7cf8e34c5cdcd3bbf2f221e3fb0f8fe71757ff7be1e9fecfabefc6e97e3db9b97e71f5f0e9e397a74f8f8fe7a7bbbf3f9f0f34fe7b70fafd979fcf4f071ebf3ddc7f3caca257dbbbe3bb9f5edede1e98da26598b345b557d636412398d427c7b7f7773ba3dbe7e7bbac69bee36b538b6d73fbc7cffe678906f37669a945cdd3634d4272949aa69fac6d293495c9b8750d734e610f4d3da587392704af948a129ce1656b687f0a67899e8f0ac299d11659e1b5b4e6629ad1c6d36232b57b9d5cbd1dad9a57a78a36f2999996a6c1c3c55b5bb03219fe6a8566c862cec994689072350bc859d2aab79e374f46a15cd21e232c39bb3a356b3622c9665582c43a69b52530929423159bc2d46a0b8a85927e573c4c22b02491433c4004463e90de04cb4a54e196531b135de2675d943c0b0d56b64b300dff234e1429af0eaa51d89318021666e5245d64217b595d00b70a6177306be3641fd485b50a3a0a1387622de435885553a9c071b310055c3161dbd49232605886bb4dac4afcbc6bce96a5052a88ae969b10c8101fedcb47502b434b00c71d12c2feddad561b4b314e53af0ed53d2381a14f466c0db62df06dd6436da410605405c740aa0835c8603cda920497d17818b0171251519cc0bc85208ab1d536229080ebea915434d102966d83336fc9a028599f7ca4303a754b1350a5003bf096d6314945421f7883dad14428554dd06f4875501cf45ef1bb8829a00e01a056d67100c66fb76a53cad0170c590c06e93f682b01be69d4e9aa2a080d9e1040b68fa39a8081aa8cea1f02848f837cf1cce1409453ba42c88002c0c115b6563cc08f03ca01b748374a19b8d31c184e5333162f46c4520800d3c48f0644385bc8f0f24bd4a730db262b84ebe8a600cf6652e9868491d3125c005110b2cef3d2328e0346518806a9d0728aa00176a2c7b5e88db43801f96c58851309b816d4cbfa7c52460d11833f60bd01748560c0273c32231c0061069a79def3d86bbe6a000799012547431ef7aee26a240dfc363d1665f4398d0e0c41810184fb04818429e436016108d5820f67e4174c7112ccc0b3cb88168e497c3b6c067f03c21f2843970f3004e7c4d83797020cc72d90d0b4182386ed910fa763c5d5ffe1ab67f00a9cb27f227060000', 4);
INSERT INTO spectrum_file (id, content, l_spectrum_id) VALUES (5, '1f8b08000000000000002596cd6e66b71144f7f729b80f40906cf60f07988563cbb680441e8cc66b21896721c07690b107c9e3e7d4d54a1f4a64dfeeeaea6afef5e187c7a7f6f8d3d3f3f5e9f1d3df1ede7ffcc77fbf7ffdf5f3bbb6c6b411235fbe7f7cf9f99faffff9fafae7ebef2fa73dfefecbe7ffbd6b33c7681fbe7cfed7d72f7ffcfbcbbb36dacb6f5f7ffdf3f5e5faf8e9f1e9f9e1db9f9ebe7b7e3fabe7f0ebc3c387bf7ff3fcfc7e87f7aabd7d54cc74bbbefdf19b8f3f3cbcb7bf5c73591fbe47b4e2afedbaa6459fb32adb9cabcf7dfc864ead3acdea0eb481aaafdab3dae9cb33aeb9571f7552c099d32691b6f51991dea6f5bd4619d0ee3657cd16bd66cd4ca0ece31caf96defd6c101f7d2d7eb679fa1c6b2ca0cd2f6eb639fad8392790f36bccb67675733b37727caed52c7b7926693a65adedde6274b7b77bd5cf195ca42a5b44b866908279edb6922f97120d5df44deebbd39349aca0d4793cdb9a40003974cdf65b522b4921272cd821abd1632f237842ccb4bd9bf75de92108624666341fb393942eee3e083655f33e7beb1405c6598477eb91036632e852129ed6d4d9f30daa3c5584a721474527150e87d120f8f1a08335fbf49974106e8dce002d2e58d8cd321497f5a493e4997c6d2d3228a781c3619d76fbd2e72ac4d030714c441558fc2a5b0d62531f0129fe774e36f15aa69c8ebe7ba098465add2c1ce4e535a7e40111ba08747688abd543e500a9cd61d16008e1844ed11b0a35f19ef3cee11c4ed9be7be31e35af3546e7e442c783a8560934154b829c67f7130768497d5b08890d213467a1bf36513da50ada52913a181c61b2d6806d04099fbb6f1b77707ab319b0b649e12801a9374e48558784393369c33ce66d156240438bda47f18b26d38f711f822246c3544be52c12983482d0a7add30b11129dec66ac639242869a4c7f8845b50dc9399c091255c6a952501bba78903db3dd5674af8c75ad5bd076484b5a65888196083da5539467a48a554017843238fa22792d9a9380e20a1bd117394f83b3312ddbc2150a7d300a8e1a9cd145c728886c363ade43667304d141a2d22e050d7b830af99e16922a4c5c4897594765ca21909f206414c78f682e34e637c474a15b26d6301210c473a2b03993a7213ffe41ca9350784b30e9ba17cc1ed3a65000cae196f958f39ef1810a80b0be4dc724988d4f81c8046d472326a64152686041df9d549adc6261905494b7ab8d154244281dc3d5c97ce31f888ca486eef1b948c9618be3c40618585c54f4ed10ed74306f633690d43d5b6f628f2d085bc456f81e76c0e0089222211eb1311153543906c48c22d2d2bc48dbb71b1eac5d06878ef242f47de5bd00103eff3b97497f248211409133ee409401ed0d0e3b16b140d817b71ae0901dc488f3d55ec14662e2f0eb9576410f5b45ae06e3445f7e5984e6abd41c62b1144c59dd3b285d1c15d1a91e8ba2cda17ec9460d9b912fb6c2ba50ccb5b756812f0daadc0c425983f43b50766930f1980d0938ee489130f8194078522df48fbd31cf4ba7d0501e9361a2a275161082615be36a8898d40d881276501f8ac034874ee95fcc0dfea3c5bc2e50ca43283256148d5db183bba37af66788a1c5a92db16f89968196621c79f8d8549c92aa86c4e9e9b04156872919cca96b7d62ac68015fa1f57e39c9390164160cd59e76b90c99554e0a834523f21c5fc51752beaae5b36fa40695b6647800425d77cd1147c885f9e345012d43166a32d5f42b94f8cd14f391a21668418b968422e8718206e0ce39a42558b8c4c5f4739f5744db521e567d45e08f3c224880416270af383c2636ca428b2c70711ea469782f0c332c8eeb5cb04c9968b7698217195fac5e5238f76ed604d378f61d39ccfb5981b733e657a666f99003f263850f2e321906c6506a9aa5fe54d7969258b2df4d23a41c361df5f0d4402b3cb30ab77263addfebecc8058ac2e0c4a47572203120b43e70a0a6ffb13988952a68e23a2893121103aa066212788341008e07c4fcf0a44332c86a4709a246025a23423208f33aec335e1f2c50fcc34f90178f2c92985afd294b879c236b88fbd1a731c352a08046edc06590020de6c930d09345ddab0355d06a20571b8f2c05b6c8010843c054dad60753f7886ea957985e8da5071d6e0583a1eff11a295e08ac4a0ee5fd020ade508a546284e07aaa157e017464bb2c34643fc811883dcc77a449d46a536fbca9a663f8377fbc2ae6f5f0f4dddb7bfbfa3f93ed20607c0b0000', 7);

-- insert a search parameters
INSERT INTO search_parameters (id, precursor_mass_tolerance, precursor_mass_tolerance_unit, fragment_mass_tolerance, fragment_mass_tolerance_unit, enzymes, score_type, psm_threshold, peptide_threshold, protein_threshold, missed_cleavages) VALUES (1, 4, 1, 5, 1, 'Trypsin;Trypsin/P', 1, 0.01, 0.02, 0.03, '2;3');

-- insert a search params has mod
INSERT INTO search_params_has_modification (id, modification_type, l_search_modification_id, l_search_parameters_id) VALUES (1, 1, 3, 1);

-- insert a search and val settings set of settings
INSERT INTO search_and_validation_settings (id, creation_date, modification_date, user_name, l_analytical_run_id, l_search_engine_id, l_search_parameters_id) VALUES (1, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 1, 2, 1);

-- insert test search and val settings has fasta db
INSERT INTO search_settings_has_fasta_db (id, l_search_and_val_settings_id, l_fasta_db_id, fasta_db_type) VALUES (1, 1, 1, 1), (2, 1, 2, 2);

-- insert test quantification method cv param
INSERT INTO quantification_method (id, accession, label, name) VALUES (1, 'PRIDE', 'PRIDE:0000315', 'SILAC');

-- insert quantification reagent
INSERT INTO quantification_reagent (id, accession, label, name) VALUES (1, 'PRIDE', 'PRIDE:0000326', 'SILAC light');

-- insert quantification method has reagent
INSERT INTO quantification_method_has_reagent (id, l_quantification_method_id, l_quantification_reagent_id) VALUES (1, 1, 1);

-- insert quantification engine
INSERT INTO quantification_engine (id, accession, label, name, type, version) VALUES (1,'MS:1001583','MS','MaxQuant','MAXQUANT','0.0.0');

-- insert quantification settings
INSERT INTO quantification_settings (id, creation_date, modification_date, user_name, l_analytical_run_id, l_quant_engine_id, l_quant_method_id) VALUES (1, '2012-11-08 16:51:13', '2012-11-08 16:51:13', 'admin', 1, 1, 1);

-- insert test peptides
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (1, 1, 0.5, 0.55, 'PWEGGLVSWPPAP', 145.6, 1);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (5, 1, 0.5, 0.55, 'PWEGGLVSWPPAP', 145.6, 4);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (2, 1, 0.3, 0.33, 'SACGPRPGRCCI', 148.6, 2);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (3, 2, 0.43, 0.433, 'SACGPRPGRCCI', 148.6, 3);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (6, 3, 0.432, 0.4333, 'HEEKEQIKCLNSRFAAFIDKVRFLEQQ', 348.6, 5);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (4, 2, 0.4, 0.44, 'IVGLSIYTVSSEVADF', 1789.2, 2);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, l_spectrum_id) VALUES (7, 2, 0.4, 0.44, 'IVGLSIYTVSSEVADF', 1789.2, 6);
INSERT INTO peptide (id, charge, psm_post_error_prob, psm_prob, peptide_sequence, theoretical_mass, fragment_ions, fragment_masses, l_spectrum_id) VALUES (8, 3, 0.00518, 61.235, '1,394.63139', '1789.2', 'y3;y4;y5;y6;y8;y6-H2O;y6(2+);y8(2+);y9(2+);y10(2+);a2;b2;b3;b5;b6;b6-H2O;b6-NH3;b7;b8;b8-H2O;b9;b9-H2O;b8(2+)', '502.215484619141;617.270935058594;780.325988769531;894.308471679688;1080.45593261719;876.20654296875;447.855285644531;540.563781738281;598.112731933594;663.0166015625;173.107757568359;201.046752929688;316.124633789063;502.215484619141;616.156860351563;598.112731933594;598.802917480469;779.259887695313;894.308471679688;876.20654296875;1007.57025146484;989.174621582031;447.855285644531', 7);

-- insert a test peptide has modification
INSERT INTO peptide_has_modification (id,  delta_score,  location, prob_score, l_modification_id,  l_peptide_id) VALUES (1, 0.5, 1, 1, 1, 1);
INSERT INTO peptide_has_modification (id,  delta_score,  location, prob_score, l_modification_id,  l_peptide_id) VALUES (2, 0.5, 1, 1, 1, 5);

-- insert test protein groups
INSERT INTO protein_group (id, protein_prob, protein_post_error_prob) VALUES (1, 0.12, 0.25), (2, 0.13, 0.36), (3, 0.14, 0.47), (4, 0.14, 0.47);

-- insert test protein group has proteins
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (1, 1, 1, 'O43414', TRUE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (2, 2, 2, 'Q61726', TRUE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (3, 3, 2, 'Q3ZAW8', FALSE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (4, 4, 2, 'Q8VED5', FALSE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (5, 5, 3, 'P28566', TRUE);
INSERT INTO protein_group_has_protein (id, l_protein_id, l_protein_group_id, protein_accession, main_group_protein) VALUES (6, 5, 4, 'P28566', TRUE);

-- insert test peptide has protein groups
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (1, 0.1, 0.9, 1, 1);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (5, 0.1, 0.9, 5, 1);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (2, 0.2, 0.22, 2, 2);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (3, 0.2, 0.22, 3, 2);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (4, 0.3, 0.33, 4, 3);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (6, 0.6, 0.66, 6, 2);
INSERT INTO peptide_has_protein_group (id,  peptide_post_error_prob,  peptide_prob, l_peptide_id,  l_protein_group_id) VALUES (7, 0.6, 0.66, 7, 4);