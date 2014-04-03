-- set search path
SET search_path TO colims;

    create table analytical_run (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        name varchar(255) not null,
        start_date timestamp,
        l_instrument_id int8,
        l_sample_id int8,
        primary key (id)
    );

    create table colims_user (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        email varchar(255) not null,
        first_name varchar(20) not null,
        last_name varchar(30) not null,
        name varchar(20) not null,
        password varchar(255) not null,
        l_institution_id int8,
        primary key (id)
    );

    create table experiment (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        number int8 not null,
        storage_location varchar(255),
        title varchar(255) not null,
        l_project_id int8,
        primary key (id)
    );

    create table experiment_binary_file (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        file_type varchar(255) not null,
        content oid not null,
        file_name varchar(255) not null,
        l_experiment_id int8,
        primary key (id)
    );

    create table fasta_db (
        id  bigserial not null,
        file_name varchar(255) not null,
        md5_checksum varchar(255),
        name varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table group_has_role (
        l_group_id int8 not null,
        l_role_id int8 not null
    );

    create table group_role (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table identification_file (
        id  bigserial not null,
        l_search_and_val_set_id int8,
        primary key (id)
    );

    create table institution (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        abbreviation varchar(10) not null,
        city varchar(30) not null,
        country varchar(30) not null,
        name varchar(30) not null,
        number int4 not null,
        postal_code int4 not null,
        street varchar(20) not null,
        primary key (id)
    );

    create table instrument (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_detector_cv_id int8 not null,
        l_instrument_type_id int8 not null,
        l_source_cv_id int8 not null,
        primary key (id)
    );

    create table instrument_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        cv_property varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        primary key (id)
    );

    create table instrument_has_analyzer (
        l_instrument_id int8 not null,
        l_instrument_cv_term_id int8 not null
    );

    create table instrument_type (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(30) not null,
        primary key (id)
    );

    create table material (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_cell_type_cv_id int8,
        l_compartment_cv_id int8,
        l_project_id int8,
        l_species_cv_id int8 not null,
        l_tissue_cv_id int8,
        primary key (id)
    );

    create table material_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        cv_property varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        primary key (id)
    );

    create table modification (
        id  bigserial not null,
        accession varchar(255),
        average_mass float8,
        average_mass_shift float8,
        monoisotopic_mass float8,
        monoisotopic_mass_shift float8,
        name varchar(255) not null,
        primary key (id)
    );

    create table peptide (
        id  bigserial not null,
        experimental_mass float8,
        psm_post_error_prob float8,
        psm_prob float8,
        peptide_sequence varchar(255) not null,
        theoretical_mass float8,
        l_identification_file_id int8,
        l_spectrum_id int8,
        primary key (id)
    );

    create table peptide_has_modification (
        id  bigserial not null,
        alpha_score float8,
        delta_score float8,
        location int4,
        modification_type int4,
        l_modification_id int8,
        l_peptide_id int8,
        primary key (id)
    );

    create table peptide_has_protein (
        id  bigserial not null,
        peptide_post_error_prob float8,
        peptide_prob float8,
        l_main_group_protein_id int8,
        l_peptide_id int8,
        l_protein_id int8,
        primary key (id)
    );

    create table permission (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table project (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        label varchar(20) not null,
        title varchar(100) not null,
        l_owner_user_id int8 not null,
        primary key (id)
    );

    create table project_has_user (
        l_project_id int8 not null,
        l_user_id int8 not null
    );

    create table protein (
        id  bigserial not null,
        accession varchar(255) not null,
        database_type varchar(255) not null,
        protein_sequence text not null,
        primary key (id)
    );

    create table protocol (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_cell_based_cv_id int8,
        l_enzyme_cv_id int8,
        l_reduction_cv_id int8,
        primary key (id)
    );

    create table protocol_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        cv_property varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        primary key (id)
    );

    create table protocol_has_chemical_labeling (
        l_protocol_id int8 not null,
        l_chemical_labeling_cv_term_id int8 not null
    );

    create table protocol_has_other_cv_term (
        l_protocol_id int8 not null,
        l_other_cv_term_id int8 not null
    );

    create table quant_method_has_quant_engine (
        id  bigserial not null,
        l_quantification_engine_id int8,
        l_quantification_method_id int8,
        l_quant_param_settings_id int8,
        primary key (id)
    );

    create table quantification (
        id  bigserial not null,
        intensity float8,
        weight int4,
        l_quantification_group_id int8,
        l_spectrum_id int8,
        primary key (id)
    );

    create table quantification_engine (
        id  bigserial not null,
        primary key (id)
    );

    create table quantification_file (
        id  bigserial not null,
        l_quantification_method_id int8,
        primary key (id)
    );

    create table quantification_group (
        id  bigserial not null,
        l_quantification_file_id int8,
        primary key (id)
    );

    create table quantification_group_has_peptide (
        id  bigserial not null,
        l_peptide_id int8,
        l_quantification_group_id int8,
        primary key (id)
    );

    create table quantification_method (
        id  bigserial not null,
        l_experiment_id int8,
        primary key (id)
    );

    create table quantification_parameter_setting (
        id  bigserial not null,
        primary key (id)
    );

    create table role_has_permission (
        l_role_id int8 not null,
        l_permission_id int8 not null
    );

    create table sample (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        sample_condition varchar(255),
        name varchar(255) not null,
        storage_location varchar(255),
        l_experiment_id int8,
        l_protocol_id int8,
        primary key (id)
    );

    create table sample_binary_file (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        file_type varchar(255) not null,
        content oid not null,
        file_name varchar(255) not null,
        l_sample_id int8,
        primary key (id)
    );

    create table sample_has_material (
        l_sample_id int8 not null,
        l_material_id int8 not null
    );

    create table search_and_val_set_has_search_engine (
        id  bigserial not null,
        l_s_and_val_set_id int8,
        l_search_engine_id int8,
        primary key (id)
    );

    create table search_and_validation_settings (
        id  bigserial not null,
        l_experiment_id int8,
        primary key (id)
    );

    create table search_engine (
        id  bigserial not null,
        primary key (id)
    );

    create table search_parameter_settings (
        id  bigserial not null,
        enzyme varchar(255),
        evalue_cutoff float8,
        fragment_mass_tolerance float8,
        fragment_mass_tolerance_unit int4,
        fragment_ion_1_type int4,
        fragment_ion_2_type int4,
        hitlist_length int4,
        max_missed_cleavages int4,
        precursor_mass_tolerance float8,
        precursor_mass_tolerance_unit int4,
        precursor_lower_charge int4,
        precursor_upper_charge int4,
        l_fasta_db_id int8,
        l_s_and_val_set_has_s_eng_id int8,
        primary key (id)
    );

    create table spectrum (
        id  bigserial not null,
        accession varchar(255) not null,
        charge int4,
        fragmentation_type varchar(255),
        intensity float8,
        mz_ratio float8 not null,
        retention_time float8,
        scan_number varchar(255) not null,
        scan_time float8,
        title varchar(255),
        l_analytical_run_id int8,
        primary key (id)
    );

    create table spectrum_file (
        id  bigserial not null,
        content oid not null,
        l_spectrum_id int8,
        primary key (id)
    );

    create table user_group (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500) not null,
        name varchar(20) not null,
        primary key (id)
    );

    create table user_has_group (
        l_user_id int8 not null,
        l_group_id int8 not null
    );

    alter table colims_user 
        add constraint UK_7qy96sq9o6jh5517or8yh758  unique (name);

    alter table experiment 
        add constraint UK_1h78kyl2aslkb3kxh0rwaujeg  unique (title);

    alter table group_role 
        add constraint UK_7kvrlnisllgg2md5614ywh82g  unique (name);

    alter table instrument 
        add constraint UK_11wfouotl7vb11u6ebomnbsrr  unique (name);

    alter table instrument_cv_term 
        add constraint UK_p439oc8wuh3dr9wyptksrc0o4  unique (accession);

    alter table instrument_type 
        add constraint UK_2khnihpbgyekjsr8cj9lrr0r7  unique (name);

    alter table material_cv_term 
        add constraint UK_4t80knf2n488qnwc8d11xiwm4  unique (accession);

    alter table permission 
        add constraint UK_2ojme20jpga3r4r79tdso17gi  unique (name);

    alter table project 
        add constraint UK_etb9i6krbg45bl5o1kt0cc4q8  unique (title);

    alter table protocol 
        add constraint UK_lidqee66itlhns030fyykvptc  unique (name);

    alter table protocol_cv_term 
        add constraint UK_ertrjihdksltewuvpd6m46mkk  unique (accession);

    alter table user_group 
        add constraint UK_kas9w8ead0ska5n3csefp2bpp  unique (name);

    alter table analytical_run 
        add constraint FK_peeqymojpv3ng8ve6kvvvfw0r 
        foreign key (l_instrument_id) 
        references instrument;

    alter table analytical_run 
        add constraint FK_jqv7xoqlrkpc5qo13mrpmp7w8 
        foreign key (l_sample_id) 
        references sample;

    alter table colims_user 
        add constraint FK_6qvbirmm26e50fj41xifn0jg0 
        foreign key (l_institution_id) 
        references institution;

    alter table experiment 
        add constraint FK_4myk0t1pgujkgng7qm1umgmig 
        foreign key (l_project_id) 
        references project;

    alter table experiment_binary_file 
        add constraint FK_myh8uaq7sva2od08mhwolbgrx 
        foreign key (l_experiment_id) 
        references experiment;

    alter table group_has_role 
        add constraint FK_97ah8s48bdpbq6nbkqpniremq 
        foreign key (l_role_id) 
        references group_role;

    alter table group_has_role 
        add constraint FK_d5j6dccj6ksooehvhrh20frjv 
        foreign key (l_group_id) 
        references user_group;

    alter table identification_file 
        add constraint FK_pfxus7hv6x3fg3epvdmjy1ksc 
        foreign key (l_search_and_val_set_id) 
        references search_and_validation_settings;

    alter table instrument 
        add constraint FK_nv913lhryr1tf3jil03u2j699 
        foreign key (l_detector_cv_id) 
        references instrument_cv_term;

    alter table instrument 
        add constraint FK_9mg83ll40kt1k3vs8wwrcxx84 
        foreign key (l_instrument_type_id) 
        references instrument_type;

    alter table instrument 
        add constraint FK_amxbo4ld6m7kbyr2w00mmamf4 
        foreign key (l_source_cv_id) 
        references instrument_cv_term;

    alter table instrument_has_analyzer 
        add constraint FK_p0fc15jd6hhc75kw5cmq041pj 
        foreign key (l_instrument_cv_term_id) 
        references instrument_cv_term;

    alter table instrument_has_analyzer 
        add constraint FK_7xy0454upil50qvcv1ongxlcm 
        foreign key (l_instrument_id) 
        references instrument;

    alter table material 
        add constraint FK_2emr349t8q9hje4hjcd4jwuas 
        foreign key (l_cell_type_cv_id) 
        references material_cv_term;

    alter table material 
        add constraint FK_sfmjhs53n62t8v7xstsb9ln2 
        foreign key (l_compartment_cv_id) 
        references material_cv_term;

    alter table material 
        add constraint FK_1w1sodocqmm73kw7ll3uslwqi 
        foreign key (l_project_id) 
        references project;

    alter table material 
        add constraint FK_e8ustpafqp4yhgp0ycperpy6u 
        foreign key (l_species_cv_id) 
        references material_cv_term;

    alter table material 
        add constraint FK_8yfpw6v3pfg5f8wrrkgboud1r 
        foreign key (l_tissue_cv_id) 
        references material_cv_term;

    alter table peptide 
        add constraint FK_nouta5locpa5t9o6yl2smm0xe 
        foreign key (l_identification_file_id) 
        references identification_file;

    alter table peptide 
        add constraint FK_obt49wd19s38x8lpknbne8a3y 
        foreign key (l_spectrum_id) 
        references spectrum;

    alter table peptide_has_modification 
        add constraint FK_fon9f459diy97xrnqjb3dfj04 
        foreign key (l_modification_id) 
        references modification;

    alter table peptide_has_modification 
        add constraint FK_tnhhusqmuwc88nl8yo0t3r7f1 
        foreign key (l_peptide_id) 
        references peptide;

    alter table peptide_has_protein 
        add constraint FK_7d9fvws08o48ai89aqi3ph8l9 
        foreign key (l_main_group_protein_id) 
        references protein;

    alter table peptide_has_protein 
        add constraint FK_nljccjaj0g8pbmfy9sdjfghce 
        foreign key (l_peptide_id) 
        references peptide;

    alter table peptide_has_protein 
        add constraint FK_i6ufytgonthuo7d6246l7rkme 
        foreign key (l_protein_id) 
        references protein;

    alter table project 
        add constraint FK_nont146vvfurex1thyophrki0 
        foreign key (l_owner_user_id) 
        references colims_user;

    alter table project_has_user 
        add constraint FK_t0ksfthfrcpvmr7fdnmk81rhc 
        foreign key (l_user_id) 
        references colims_user;

    alter table project_has_user 
        add constraint FK_7l0pdupx29tr1yh2cv2omkla4 
        foreign key (l_project_id) 
        references project;

    alter table protocol 
        add constraint FK_68woyi6fqi6j99t2511tiayb2 
        foreign key (l_cell_based_cv_id) 
        references protocol_cv_term;

    alter table protocol 
        add constraint FK_r8omo1sbwto3f96hycuqgosxw 
        foreign key (l_enzyme_cv_id) 
        references protocol_cv_term;

    alter table protocol 
        add constraint FK_ipxj4jmmfsh21ebk41sir499o 
        foreign key (l_reduction_cv_id) 
        references protocol_cv_term;

    alter table protocol_has_chemical_labeling 
        add constraint FK_hg4pc56r12d348ibd3q4mexk 
        foreign key (l_chemical_labeling_cv_term_id) 
        references protocol_cv_term;

    alter table protocol_has_chemical_labeling 
        add constraint FK_lti01qugh58dw133ahsm7p7in 
        foreign key (l_protocol_id) 
        references protocol;

    alter table protocol_has_other_cv_term 
        add constraint FK_chjm79t4wyytdkud9yrtdflua 
        foreign key (l_other_cv_term_id) 
        references protocol_cv_term;

    alter table protocol_has_other_cv_term 
        add constraint FK_obdu1ny455r0vb44a86qapib 
        foreign key (l_protocol_id) 
        references protocol;

    alter table quant_method_has_quant_engine 
        add constraint FK_oasqydotqql6ck89k2ussm1gy 
        foreign key (l_quantification_engine_id) 
        references quantification_engine;

    alter table quant_method_has_quant_engine 
        add constraint FK_4gxen92cgpdm0md37a122fobt 
        foreign key (l_quantification_method_id) 
        references quantification_method;

    alter table quant_method_has_quant_engine 
        add constraint FK_lhjhago1pcj6fhc0bey8ylnba 
        foreign key (l_quant_param_settings_id) 
        references quantification_parameter_setting;

    alter table quantification 
        add constraint FK_pqbxof18u1h1hv14dmnyo44mr 
        foreign key (l_quantification_group_id) 
        references quantification_group;

    alter table quantification 
        add constraint FK_crk8scd9ju0k4q8ajo4g48s33 
        foreign key (l_spectrum_id) 
        references spectrum;

    alter table quantification_file 
        add constraint FK_rqi1kve6lkstnkaj90pgej8k2 
        foreign key (l_quantification_method_id) 
        references quantification_method;

    alter table quantification_group 
        add constraint FK_jx8ix1plsndh02i3cpcetcs54 
        foreign key (l_quantification_file_id) 
        references quantification_file;

    alter table quantification_group_has_peptide 
        add constraint FK_b9yiwf6dqhkcx01kdhm1t3it5 
        foreign key (l_peptide_id) 
        references peptide;

    alter table quantification_group_has_peptide 
        add constraint FK_3y5qhhppnpirl5g7efvnr611l 
        foreign key (l_quantification_group_id) 
        references quantification_group;

    alter table quantification_method 
        add constraint FK_qaaks315j06c978q4nj9nn6yf 
        foreign key (l_experiment_id) 
        references experiment;

    alter table role_has_permission 
        add constraint FK_sp2yl1puui1jbdknkehlvhxq4 
        foreign key (l_permission_id) 
        references permission;

    alter table role_has_permission 
        add constraint FK_30ri4nqak6uechie8onfsy489 
        foreign key (l_role_id) 
        references group_role;

    alter table sample 
        add constraint FK_fx5esu8n3umhihw8kt19593yt 
        foreign key (l_experiment_id) 
        references experiment;

    alter table sample 
        add constraint FK_pbpctnedgs4yjfsu6vo6b7et8 
        foreign key (l_protocol_id) 
        references protocol;

    alter table sample_binary_file 
        add constraint FK_ek4x2rdy0on1ncwt4xa445d6c 
        foreign key (l_sample_id) 
        references sample;

    alter table sample_has_material 
        add constraint FK_8yuapgtl822resfo0cuhm1uv0 
        foreign key (l_material_id) 
        references material;

    alter table sample_has_material 
        add constraint FK_82ptqcd4sp8nrghikp7i9crpv 
        foreign key (l_sample_id) 
        references sample;

    alter table search_and_val_set_has_search_engine 
        add constraint FK_5cjhcdnpuib22sq83ttapgkhy 
        foreign key (l_s_and_val_set_id) 
        references search_and_validation_settings;

    alter table search_and_val_set_has_search_engine 
        add constraint FK_iogx049qj83vcfh6r3v7echtr 
        foreign key (l_search_engine_id) 
        references search_engine;

    alter table search_and_validation_settings 
        add constraint FK_ris4310042dlrxtaocuejb2x7 
        foreign key (l_experiment_id) 
        references experiment;

    alter table search_parameter_settings 
        add constraint FK_htqwqkr941nrjnw8udkhlswsh 
        foreign key (l_fasta_db_id) 
        references fasta_db;

    alter table search_parameter_settings 
        add constraint FK_t06ciehin8u5bc0x1d9y0iuib 
        foreign key (l_s_and_val_set_has_s_eng_id) 
        references search_and_val_set_has_search_engine;

    alter table spectrum 
        add constraint FK_mpjgedldeff5qugyrqangh6so 
        foreign key (l_analytical_run_id) 
        references analytical_run;

    alter table spectrum_file 
        add constraint FK_8unfql3wnogbervsi1gl607ds 
        foreign key (l_spectrum_id) 
        references spectrum;

    alter table user_has_group 
        add constraint FK_j85c8a2vhyfvwiooom79aenls 
        foreign key (l_group_id) 
        references user_group;

    alter table user_has_group 
        add constraint FK_e2guk3ak57dupnvkd5k3u9dfk 
        foreign key (l_user_id) 
        references colims_user;
		
-- create default value insertions
-- insert default admin and distributed users
INSERT INTO colims_user (creation_date, modification_date, user_name, email, first_name, last_name, name, password) VALUES ('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin@admin.com','admin','admin','admin','lEGFv0p//m40EoHCJtk1c5QVxjIoIQiD'),('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed@distributed.com','distributed','distributed','distributed','Fe4BGoLvzGyUpcCVUTiBvb1Oy2qsrM3O');

-- insert default admin and distributed groups
INSERT INTO user_group (creation_date, modification_date, user_name, description, name) VALUES ('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin group description','admin'),('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed group description','distributed');

-- insert default user_has_group
INSERT INTO user_has_group (l_user_id, l_group_id) VALUES (1,1),(2,2);

-- insert default admin and distributed roles
INSERT INTO group_role (creation_date, modification_date, user_name, description, name) VALUES ('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','admin role description','admin'), ('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','distributed role description','distributed');

-- insert default group_has_roles
INSERT INTO group_has_role (l_group_id, l_role_id) VALUES (1,1),(2,2);

-- insert default permissions
INSERT INTO permission (creation_date, modification_date, user_name, description, name) VALUES ('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','read','read'),('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','create','create'),('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','update','update'),('2012-06-27 14:42:16','2012-06-27 14:49:46','admin','delete','delete');

-- insert default role_has_permissions
INSERT INTO role_has_permission (l_role_id, l_permission_id) VALUES (1,1),(1,2),(1,3),(1,4),(2,1),(2,2),(2,3);

-- insert some test data
INSERT INTO project (creation_date,modification_date,user_name,description,label,title,l_owner_user_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','test project 1 description','test project 1 label','test project 1',1);

INSERT INTO project_has_user (l_project_id,l_user_id) VALUES (1,1);

INSERT INTO experiment (creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','experiment 1 description',2222,'','experiment 1',1);
INSERT INTO experiment (creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','',15166,'','experiment 2',1);

INSERT INTO instrument_cv_term (creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1001603','SOURCE','MS','ProteomeDiscoverer:Spectrum Selector:Ionization Source','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_term (creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1002308','DETECTOR','MS','fluorescence detector','PSI Mass Spectrometry Ontology [MS]');
INSERT INTO instrument_cv_term (creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1000254','ANALYZER','MS','electrostatic energy analyzer','PSI Mass Spectrometry Ontology [MS]');

INSERT INTO instrument_type (creation_date,modification_date,user_name,description,name) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument type 1 description','instrument type 1');
INSERT INTO instrument_type (creation_date,modification_date,user_name,description,name) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument type 2 description','instrument type 2');

INSERT INTO instrument (creation_date,modification_date,user_name,name,l_detector_cv_id,l_instrument_type_id,l_source_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 1',2,1,1);
INSERT INTO instrument (creation_date,modification_date,user_name,name,l_detector_cv_id,l_instrument_type_id,l_source_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 3',2,2,1);

INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_term_id) VALUES (1,3);
INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_term_id) VALUES (2,3);

INSERT INTO material_cv_term (creation_date,modification_date,user_name,accession,cv_property,label,name,ontology) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','9606','SPECIES','NEWT','Homo sapiens (Human)','NEWT UniProt Taxonomy Database[NEWT]');

INSERT INTO material (creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_project_id,l_species_cv_id,l_tissue_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 1',null,null,null,1,null);
INSERT INTO material (creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_project_id,l_species_cv_id,l_tissue_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 2',null,null,null,1,null);

INSERT INTO protocol (creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 7',null,null,null);
INSERT INTO protocol (creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES ('2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 1',null,null,null);