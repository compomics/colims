
    create table colims.analytical_run (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        name varchar(100) not null,
        start_date datetime,
        l_instrument_id bigint,
        l_sample_id bigint,
        primary key (id)
    );

    create table colims.colims_user (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        email varchar(255) not null,
        first_name varchar(20) not null,
        last_name varchar(30) not null,
        name varchar(20) not null,
        password varchar(255) not null,
        l_institution_id bigint,
        primary key (id)
    );

    create table colims.experiment (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        description varchar(500),
        number bigint,
        storage_location varchar(255),
        title varchar(100) not null,
        l_project_id bigint,
        primary key (id)
    );

    create table colims.experiment_binary_file (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        file_type varchar(255) not null,
        content longblob not null,
        file_name varchar(255) not null,
        l_experiment_id bigint,
        primary key (id)
    );

    create table colims.fasta_db (
        id bigint not null auto_increment,
        file_name varchar(200) not null,
        file_path varchar(250) not null,
        md5_checksum varchar(255),
        name varchar(100) not null,
        species varchar(255),
        taxonomy_accession varchar(255),
        version varchar(20) not null,
        primary key (id)
    );

    create table colims.group_has_role (
        l_group_id bigint not null,
        l_role_id bigint not null
    );

    create table colims.group_role (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.identification_file (
        id bigint not null auto_increment,
        file_type varchar(255),
        content longblob,
        file_name varchar(255) not null,
        file_path varchar(255),
        l_search_and_val_settings_id bigint,
        primary key (id)
    );

    create table colims.institution (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        abbreviation varchar(10) not null,
        city varchar(30) not null,
        country varchar(30) not null,
        name varchar(30) not null,
        number integer not null,
        postal_code integer,
        street varchar(20) not null,
        primary key (id)
    );

    create table colims.instrument (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_detector_cv_id bigint not null,
        l_source_cv_id bigint not null,
        l_type_cv_id bigint not null,
        primary key (id)
    );

    create table colims.instrument_cv_param (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.instrument_has_analyzer (
        l_instrument_id bigint not null,
        l_instrument_cv_param_id bigint not null
    );

    create table colims.material (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_cell_type_cv_id bigint,
        l_compartment_cv_id bigint,
        l_project_id bigint,
        l_species_cv_id bigint not null,
        l_tissue_cv_id bigint,
        primary key (id)
    );

    create table colims.material_cv_param (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.modification (
        id bigint not null auto_increment,
        accession varchar(255),
        alternative_accession varchar(255),
        average_mass_shift double precision,
        monoisotopic_mass_shift double precision,
        name varchar(255) not null,
        primary key (id)
    );

    create table colims.peptide (
        id bigint not null auto_increment,
        charge integer,
        psm_post_error_prob double precision,
        psm_prob double precision,
        peptide_sequence varchar(255) not null,
        theoretical_mass double precision,
        l_identification_file_id bigint,
        l_spectrum_id bigint,
        primary key (id)
    );

    create table colims.peptide_has_modification (
        id bigint not null auto_increment,
        delta_score double precision,
        location integer,
        modification_type integer,
        prob_score double precision,
        l_modification_id bigint,
        l_peptide_id bigint,
        primary key (id)
    );

    create table colims.peptide_has_protein (
        id bigint not null auto_increment,
        peptide_post_error_prob double precision,
        peptide_prob double precision,
        l_main_group_protein_id bigint,
        l_peptide_id bigint,
        l_protein_id bigint,
        primary key (id)
    );

    create table colims.permission (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.project (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        description varchar(500),
        label varchar(20) not null,
        title varchar(100) not null,
        l_owner_user_id bigint not null,
        primary key (id)
    );

    create table colims.project_has_user (
        l_project_id bigint not null,
        l_user_id bigint not null
    );

    create table colims.protein (
        id bigint not null auto_increment,
        protein_sequence longtext not null,
        primary key (id)
    );

    create table colims.protein_accession (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        l_protein_id bigint,
        primary key (id)
    );

    create table colims.protocol (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        name varchar(30) not null,
        l_cell_based_cv_id bigint,
        l_enzyme_cv_id bigint,
        l_reduction_cv_id bigint,
        primary key (id)
    );

    create table colims.protocol_cv_param (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.protocol_has_chemical_labeling (
        l_protocol_id bigint not null,
        l_chemical_labeling_cv_param_id bigint not null
    );

    create table colims.protocol_has_other_cv_param (
        l_protocol_id bigint not null,
        l_other_protocol_cv_param_id bigint not null
    );

    create table colims.quant_param_settings_has_reagent (
        l_quant_parameters_id bigint not null,
        l_quant_cv_param_id bigint not null
    );

    create table colims.quantification (
        id bigint not null auto_increment,
        intensity double precision not null,
        weight integer not null,
        l_quantification_file_id bigint,
        primary key (id)
    );

    create table colims.quantification_cv_param (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.quantification_engine (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.quantification_file (
        id bigint not null auto_increment,
        file_type varchar(255),
        content longblob,
        file_name varchar(255) not null,
        file_path varchar(255),
        l_quant_settings_id bigint,
        primary key (id)
    );

    create table colims.quantification_group (
        id bigint not null auto_increment,
        l_peptide_id bigint,
        l_quantification_id bigint,
        primary key (id)
    );

    create table colims.quantification_parameters (
        id bigint not null auto_increment,
        error double precision,
        include_modifications bit,
        label_count integer,
        minimum_ratio_count integer,
        l_method_cv_id bigint,
        primary key (id)
    );

    create table colims.quantification_settings (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        l_experiment_id bigint,
        l_quant_engine_id bigint,
        l_quant_param_settings_id bigint,
        primary key (id)
    );

    create table colims.role_has_permission (
        l_role_id bigint not null,
        l_permission_id bigint not null
    );

    create table colims.sample (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        sample_condition varchar(255),
        name varchar(100) not null,
        storage_location varchar(255),
        l_experiment_id bigint,
        l_protocol_id bigint,
        primary key (id)
    );

    create table colims.sample_binary_file (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        file_type varchar(255) not null,
        content longblob not null,
        file_name varchar(255) not null,
        l_sample_id bigint,
        primary key (id)
    );

    create table colims.sample_has_material (
        l_sample_id bigint not null,
        l_material_id bigint not null
    );

    create table colims.search_and_validation_settings (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        l_experiment_id bigint,
        l_fasta_db_id bigint,
        l_search_engine_id bigint,
        l_search_parameters_id bigint,
        primary key (id)
    );

    create table colims.search_cv_param (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.search_engine (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        param_value varchar(255),
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.search_modification (
        id bigint not null auto_increment,
        accession varchar(255),
        alternative_accession varchar(255),
        average_mass_shift double precision,
        monoisotopic_mass_shift double precision,
        name varchar(255) not null,
        primary key (id)
    );

    create table colims.search_parameters (
        id bigint not null auto_increment,
        search_ion_type_1 integer,
        fragment_mass_tolerance double precision,
        fragment_mass_tolerance_unit integer,
        lower_charge integer,
        missed_cleavages integer,
        precursor_mass_tolerance double precision,
        precursor_mass_tolerance_unit integer,
        search_ion_type_2 integer,
        threshold double precision,
        upper_charge integer,
        l_search_enzyme_cv_id bigint,
        l_search_type_cv_id bigint,
        primary key (id)
    );

    create table colims.search_parameters_has_other_cv_param (
        l_search_parameters_id bigint not null,
        l_other_search_cv_param_id bigint not null
    );

    create table colims.search_params_has_modification (
        id bigint not null auto_increment,
        modification_type integer,
        l_search_modification_id bigint,
        l_search_parameters_id bigint,
        primary key (id)
    );

    create table colims.spectrum (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        charge integer,
        fragmentation_type varchar(255),
        intensity double precision,
        mz_ratio double precision,
        retention_time double precision,
        scan_number varchar(255) not null,
        scan_time double precision,
        title varchar(255),
        l_analytical_run_id bigint,
        primary key (id)
    );

    create table colims.spectrum_file (
        id bigint not null auto_increment,
        content longblob not null,
        l_spectrum_id bigint,
        primary key (id)
    );

    create table colims.user_group (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.user_has_group (
        l_user_id bigint not null,
        l_group_id bigint not null
    );

    alter table colims.colims_user
        add constraint UK_7qy96sq9o6jh5517or8yh758  unique (name);

    alter table colims.experiment
        add constraint UK_1h78kyl2aslkb3kxh0rwaujeg  unique (title);

    alter table colims.group_role
        add constraint UK_7kvrlnisllgg2md5614ywh82g  unique (name);

    alter table colims.instrument
        add constraint UK_11wfouotl7vb11u6ebomnbsrr  unique (name);

    alter table colims.permission
        add constraint UK_2ojme20jpga3r4r79tdso17gi  unique (name);

    alter table colims.project
        add constraint UK_etb9i6krbg45bl5o1kt0cc4q8  unique (title);

    alter table colims.protocol
        add constraint UK_lidqee66itlhns030fyykvptc  unique (name);

    alter table colims.user_group
        add constraint UK_kas9w8ead0ska5n3csefp2bpp  unique (name);

    alter table colims.analytical_run
        add constraint FK_peeqymojpv3ng8ve6kvvvfw0r
        foreign key (l_instrument_id)
        references colims.instrument (id);

    alter table colims.analytical_run
        add constraint FK_jqv7xoqlrkpc5qo13mrpmp7w8
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.colims_user
        add constraint FK_6qvbirmm26e50fj41xifn0jg0
        foreign key (l_institution_id)
        references colims.institution (id);

    alter table colims.experiment
        add constraint FK_4myk0t1pgujkgng7qm1umgmig
        foreign key (l_project_id)
        references colims.project (id);

    alter table colims.experiment_binary_file
        add constraint FK_myh8uaq7sva2od08mhwolbgrx
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.group_has_role
        add constraint FK_97ah8s48bdpbq6nbkqpniremq
        foreign key (l_role_id)
        references colims.group_role (id);

    alter table colims.group_has_role
        add constraint FK_d5j6dccj6ksooehvhrh20frjv
        foreign key (l_group_id)
        references colims.user_group (id);

    alter table colims.identification_file
        add constraint FK_jqdmoeocgmo37fgetf0t0p0pd
        foreign key (l_search_and_val_settings_id)
        references colims.search_and_validation_settings (id);

    alter table colims.instrument
        add constraint FK_nv913lhryr1tf3jil03u2j699
        foreign key (l_detector_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument
        add constraint FK_amxbo4ld6m7kbyr2w00mmamf4
        foreign key (l_source_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument
        add constraint FK_doq0jgu8jf6ycvanskyox0iet
        foreign key (l_type_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument_has_analyzer
        add constraint FK_3rwpcqbak74mnqe69klnq0cq5
        foreign key (l_instrument_cv_param_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument_has_analyzer
        add constraint FK_7xy0454upil50qvcv1ongxlcm
        foreign key (l_instrument_id)
        references colims.instrument (id);

    alter table colims.material
        add constraint FK_2emr349t8q9hje4hjcd4jwuas
        foreign key (l_cell_type_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FK_sfmjhs53n62t8v7xstsb9ln2
        foreign key (l_compartment_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FK_1w1sodocqmm73kw7ll3uslwqi
        foreign key (l_project_id)
        references colims.project (id);

    alter table colims.material
        add constraint FK_e8ustpafqp4yhgp0ycperpy6u
        foreign key (l_species_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FK_8yfpw6v3pfg5f8wrrkgboud1r
        foreign key (l_tissue_cv_id)
        references colims.material_cv_param (id);

    alter table colims.peptide
        add constraint FK_nouta5locpa5t9o6yl2smm0xe
        foreign key (l_identification_file_id)
        references colims.identification_file (id);

    alter table colims.peptide
        add constraint FK_obt49wd19s38x8lpknbne8a3y
        foreign key (l_spectrum_id)
        references colims.spectrum (id);

    alter table colims.peptide_has_modification
        add constraint FK_fon9f459diy97xrnqjb3dfj04
        foreign key (l_modification_id)
        references colims.modification (id);

    alter table colims.peptide_has_modification
        add constraint FK_tnhhusqmuwc88nl8yo0t3r7f1
        foreign key (l_peptide_id)
        references colims.peptide (id);

    alter table colims.peptide_has_protein
        add constraint FK_7d9fvws08o48ai89aqi3ph8l9
        foreign key (l_main_group_protein_id)
        references colims.protein (id);

    alter table colims.peptide_has_protein
        add constraint FK_nljccjaj0g8pbmfy9sdjfghce
        foreign key (l_peptide_id)
        references colims.peptide (id);

    alter table colims.peptide_has_protein
        add constraint FK_i6ufytgonthuo7d6246l7rkme
        foreign key (l_protein_id)
        references colims.protein (id);

    alter table colims.project
        add constraint FK_nont146vvfurex1thyophrki0
        foreign key (l_owner_user_id)
        references colims.colims_user (id);

    alter table colims.project_has_user
        add constraint FK_t0ksfthfrcpvmr7fdnmk81rhc
        foreign key (l_user_id)
        references colims.colims_user (id);

    alter table colims.project_has_user
        add constraint FK_7l0pdupx29tr1yh2cv2omkla4
        foreign key (l_project_id)
        references colims.project (id);

    alter table colims.protein_accession
        add constraint FK_7mnvsvxdpjtoi386cssgoyp00
        foreign key (l_protein_id)
        references colims.protein (id);

    alter table colims.protocol
        add constraint FK_68woyi6fqi6j99t2511tiayb2
        foreign key (l_cell_based_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol
        add constraint FK_r8omo1sbwto3f96hycuqgosxw
        foreign key (l_enzyme_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol
        add constraint FK_ipxj4jmmfsh21ebk41sir499o
        foreign key (l_reduction_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_chemical_labeling
        add constraint FK_ae7lq5vutry4hnjayfe6fr4d9
        foreign key (l_chemical_labeling_cv_param_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_chemical_labeling
        add constraint FK_lti01qugh58dw133ahsm7p7in
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.protocol_has_other_cv_param
        add constraint FK_7nwavlxqhp0vtr6q9l4mq44y3
        foreign key (l_other_protocol_cv_param_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_other_cv_param
        add constraint FK_pji4i6m4pb9u15v2fmjoswpwl
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.quant_param_settings_has_reagent
        add constraint FK_e1eistno7ee0yyj3xh1il6icj
        foreign key (l_quant_cv_param_id)
        references colims.quantification_cv_param (id);

    alter table colims.quant_param_settings_has_reagent
        add constraint FK_rkd4el7re84mngd9pmccwmcyy
        foreign key (l_quant_parameters_id)
        references colims.quantification_parameters (id);

    alter table colims.quantification
        add constraint FK_o1pngv9c5nym7t3guesme7guy
        foreign key (l_quantification_file_id)
        references colims.quantification_file (id);

    alter table colims.quantification_file
        add constraint FK_7uqibw8f0ebtk6kyd9yyne6mt
        foreign key (l_quant_settings_id)
        references colims.quantification_settings (id);

    alter table colims.quantification_group
        add constraint FK_jjmhtgpt9pj69yftuldr43byk
        foreign key (l_peptide_id)
        references colims.peptide (id);

    alter table colims.quantification_group
        add constraint FK_rndw3g424dyq25bytkskynfr6
        foreign key (l_quantification_id)
        references colims.quantification (id);

    alter table colims.quantification_parameters
        add constraint FK_g99ajdjvoliyurlibrotbuwqr
        foreign key (l_method_cv_id)
        references colims.quantification_cv_param (id);

    alter table colims.quantification_settings
        add constraint FK_7yyahob7fruseylo348enfa7d
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.quantification_settings
        add constraint FK_opawj8lyblvsk6bifd8g898id
        foreign key (l_quant_engine_id)
        references colims.quantification_engine (id);

    alter table colims.quantification_settings
        add constraint FK_cjhxlyajclvflr45jxcw09oet
        foreign key (l_quant_param_settings_id)
        references colims.quantification_parameters (id);

    alter table colims.role_has_permission
        add constraint FK_sp2yl1puui1jbdknkehlvhxq4
        foreign key (l_permission_id)
        references colims.permission (id);

    alter table colims.role_has_permission
        add constraint FK_30ri4nqak6uechie8onfsy489
        foreign key (l_role_id)
        references colims.group_role (id);

    alter table colims.sample
        add constraint FK_fx5esu8n3umhihw8kt19593yt
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.sample
        add constraint FK_pbpctnedgs4yjfsu6vo6b7et8
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.sample_binary_file
        add constraint FK_ek4x2rdy0on1ncwt4xa445d6c
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.sample_has_material
        add constraint FK_8yuapgtl822resfo0cuhm1uv0
        foreign key (l_material_id)
        references colims.material (id);

    alter table colims.sample_has_material
        add constraint FK_82ptqcd4sp8nrghikp7i9crpv
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.search_and_validation_settings
        add constraint FK_ris4310042dlrxtaocuejb2x7
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.search_and_validation_settings
        add constraint FK_4o2jjp2a6a1vaff68fl6kj4ck
        foreign key (l_fasta_db_id)
        references colims.fasta_db (id);

    alter table colims.search_and_validation_settings
        add constraint FK_bnrf68wq188wbw6fq69yk83hi
        foreign key (l_search_engine_id)
        references colims.search_engine (id);

    alter table colims.search_and_validation_settings
        add constraint FK_jynujosbdk8itph3yh7vybao9
        foreign key (l_search_parameters_id)
        references colims.search_parameters (id);

    alter table colims.search_parameters
        add constraint FK_6vjw0vbuohtoiqh2ogsoexwee
        foreign key (l_search_enzyme_cv_id)
        references colims.search_cv_param (id);

    alter table colims.search_parameters
        add constraint FK_c4887nijg14eonajt08kjd7y2
        foreign key (l_search_type_cv_id)
        references colims.search_cv_param (id);

    alter table colims.search_parameters_has_other_cv_param
        add constraint FK_sn1i0py7kln7t4t93s7vhlnu3
        foreign key (l_other_search_cv_param_id)
        references colims.protocol_cv_param (id);

    alter table colims.search_parameters_has_other_cv_param
        add constraint FK_gajty60g05dpw7uoxlie2jrgo
        foreign key (l_search_parameters_id)
        references colims.search_parameters (id);

    alter table colims.search_params_has_modification
        add constraint FK_md8klk0kk7o4nxw0yl3jycjcf
        foreign key (l_search_modification_id)
        references colims.search_modification (id);

    alter table colims.search_params_has_modification
        add constraint FK_bja38bxhwtbll4loolmjdfvdy
        foreign key (l_search_parameters_id)
        references colims.search_parameters (id);

    alter table colims.spectrum
        add constraint FK_mpjgedldeff5qugyrqangh6so
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.spectrum_file
        add constraint FK_8unfql3wnogbervsi1gl607ds
        foreign key (l_spectrum_id)
        references colims.spectrum (id);

    alter table colims.user_has_group
        add constraint FK_j85c8a2vhyfvwiooom79aenls
        foreign key (l_group_id)
        references colims.user_group (id);

    alter table colims.user_has_group
        add constraint FK_e2guk3ak57dupnvkd5k3u9dfk
        foreign key (l_user_id)
        references colims.colims_user (id);

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

INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_project_id,l_species_cv_id,l_tissue_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 1',null,null,null,1,null);
INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_project_id,l_species_cv_id,l_tissue_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 2',null,null,null,1,null);

INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 7',null,null,null);
INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 1',null,null,null);

INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (1, 'MS:1001251', 'PSI-MS', 'Trypsin', 'PSI-MS', 'SEARCH_PARAM_ENZYME');
INSERT INTO search_cv_param (id, accession, label, name, ontology, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'PSI-MS', 'SEARCH_TYPE');