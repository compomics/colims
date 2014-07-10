
    create table colims.analytical_run (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        name varchar(100) not null,
        start_date timestamp,
        l_instrument_id int8,
        l_sample_id int8,
        primary key (id)
    );

    create table colims.colims_user (
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

    create table colims.experiment (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        number int8,
        storage_location varchar(255),
        title varchar(100) not null,
        l_project_id int8,
        primary key (id)
    );

    create table colims.experiment_binary_file (
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

    create table colims.fasta_db (
        id  bigserial not null,
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
        l_group_id int8 not null,
        l_role_id int8 not null
    );

    create table colims.group_role (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.identification_file (
        id  bigserial not null,
        file_type varchar(255),
        content oid,
        file_name varchar(255) not null,
        file_path varchar(255),
        l_search_and_val_settings_id int8,
        primary key (id)
    );

    create table colims.institution (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        abbreviation varchar(10) not null,
        city varchar(30) not null,
        country varchar(30) not null,
        name varchar(30) not null,
        number int4 not null check (number>=1),
        postal_code int4 check (postal_code>=1),
        street varchar(20) not null,
        primary key (id)
    );

    create table colims.instrument (
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

    create table colims.instrument_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.instrument_has_analyzer (
        l_instrument_id int8 not null,
        l_instrument_cv_term_id int8 not null
    );

    create table colims.instrument_type (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(30) not null,
        primary key (id)
    );

    create table colims.material (
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

    create table colims.material_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.modification (
        id  bigserial not null,
        accession varchar(255),
        alternative_accession varchar(255),
        average_mass float8,
        average_mass_shift float8,
        monoisotopic_mass float8,
        monoisotopic_mass_shift float8,
        name varchar(255) not null,
        primary key (id)
    );

    create table colims.peptide (
        id  bigserial not null,
        charge int4,
        psm_post_error_prob float8,
        psm_prob float8,
        peptide_sequence varchar(255) not null,
        theoretical_mass float8,
        l_identification_file_id int8,
        l_spectrum_id int8,
        primary key (id)
    );

    create table colims.peptide_has_modification (
        id  bigserial not null,
        alpha_score float8,
        delta_score float8,
        location int4,
        modification_type int4,
        l_modification_id int8,
        l_peptide_id int8,
        primary key (id)
    );

    create table colims.peptide_has_protein (
        id  bigserial not null,
        peptide_post_error_prob float8,
        peptide_prob float8,
        l_main_group_protein_id int8,
        l_peptide_id int8,
        l_protein_id int8,
        primary key (id)
    );

    create table colims.permission (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.project (
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

    create table colims.project_has_user (
        l_project_id int8 not null,
        l_user_id int8 not null
    );

    create table colims.protein (
        id  bigserial not null,
        protein_sequence text not null,
        primary key (id)
    );

    create table colims.protein_accession (
        id  bigserial not null,
        accession varchar(255) not null,
        l_protein_id int8,
        primary key (id)
    );

    create table colims.protocol (
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

    create table colims.protocol_cv_term (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.protocol_has_chemical_labeling (
        l_protocol_id int8 not null,
        l_chemical_labeling_cv_term_id int8 not null
    );

    create table colims.protocol_has_other_cv_term (
        l_protocol_id int8 not null,
        l_other_cv_term_id int8 not null
    );

    create table colims.quant_parameter_settings (
        id  bigserial not null,
        error float8,
        include_modifications boolean,
        label_count int4,
        minimum_ratio_count int4,
        primary key (id)
    );

    create table colims.quantification (
        id  bigserial not null,
        intensity float8 not null,
        weight int4 not null,
        l_quantification_file_id int8,
        primary key (id)
    );

    create table colims.quantification_engine (
        id  bigserial not null,
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.quantification_file (
        id  bigserial not null,
        file_type varchar(255),
        content oid,
        file_name varchar(255) not null,
        file_path varchar(255),
        l_quant_settings_id int8,
        primary key (id)
    );

    create table colims.quantification_group (
        id  bigserial not null,
        l_peptide_id int8,
        l_quantification_id int8,
        primary key (id)
    );

    create table colims.quantification_settings (
        id  bigserial not null,
        l_experiment_id int8,
        l_quant_engine_id int8,
        l_quant_param_settings_id int8,
        primary key (id)
    );

    create table colims.role_has_permission (
        l_role_id int8 not null,
        l_permission_id int8 not null
    );

    create table colims.sample (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        sample_condition varchar(255),
        name varchar(100) not null,
        storage_location varchar(255),
        l_experiment_id int8,
        l_protocol_id int8,
        primary key (id)
    );

    create table colims.sample_binary_file (
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

    create table colims.sample_has_material (
        l_sample_id int8 not null,
        l_material_id int8 not null
    );

    create table colims.search_and_validation_settings (
        id  bigserial not null,
        l_experiment_id int8,
        l_fasta_db_id int8,
        l_search_engine_id int8,
        l_search_param_settings_id int8,
        primary key (id)
    );

    create table colims.search_engine (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        ontology varchar(255) not null,
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.search_parameter_settings (
        id  bigserial not null,
        enzyme varchar(255),
        evalue_cutoff float8,
        search_ion_type_1 int4,
        fragment_mass_tolerance float8,
        fragment_mass_tolerance_unit int4,
        lower_charge int4,
        missed_cleavages int4,
        precursor_mass_tolerance float8,
        precursor_mass_tolerance_unit int4,
        search_ion_type_2 int4,
        upper_charge int4,
        primary key (id)
    );

    create table colims.spectrum (
        id  bigserial not null,
        accession varchar(255) not null,
        charge int4,
        fragmentation_type varchar(255),
        intensity float8,
        mz_ratio float8,
        retention_time float8,
        scan_number varchar(255) not null,
        scan_time float8,
        title varchar(255),
        l_analytical_run_id int8,
        primary key (id)
    );

    create table colims.spectrum_file (
        id  bigserial not null,
        content oid not null,
        l_spectrum_id int8,
        primary key (id)
    );

    create table colims.user_group (
        id  bigserial not null,
        creation_date timestamp not null,
        modification_date timestamp not null,
        user_name varchar(255) not null,
        description varchar(500),
        name varchar(20) not null,
        primary key (id)
    );

    create table colims.user_has_group (
        l_user_id int8 not null,
        l_group_id int8 not null
    );

    alter table colims.colims_user 
        add constraint UK_7qy96sq9o6jh5517or8yh758  unique (name);

    alter table colims.experiment 
        add constraint UK_1h78kyl2aslkb3kxh0rwaujeg  unique (title);

    alter table colims.group_role 
        add constraint UK_7kvrlnisllgg2md5614ywh82g  unique (name);

    alter table colims.instrument 
        add constraint UK_11wfouotl7vb11u6ebomnbsrr  unique (name);

    alter table colims.instrument_type 
        add constraint UK_2khnihpbgyekjsr8cj9lrr0r7  unique (name);

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
        references colims.instrument;

    alter table colims.analytical_run 
        add constraint FK_jqv7xoqlrkpc5qo13mrpmp7w8 
        foreign key (l_sample_id) 
        references colims.sample;

    alter table colims.colims_user 
        add constraint FK_6qvbirmm26e50fj41xifn0jg0 
        foreign key (l_institution_id) 
        references colims.institution;

    alter table colims.experiment 
        add constraint FK_4myk0t1pgujkgng7qm1umgmig 
        foreign key (l_project_id) 
        references colims.project;

    alter table colims.experiment_binary_file 
        add constraint FK_myh8uaq7sva2od08mhwolbgrx 
        foreign key (l_experiment_id) 
        references colims.experiment;

    alter table colims.group_has_role 
        add constraint FK_97ah8s48bdpbq6nbkqpniremq 
        foreign key (l_role_id) 
        references colims.group_role;

    alter table colims.group_has_role 
        add constraint FK_d5j6dccj6ksooehvhrh20frjv 
        foreign key (l_group_id) 
        references colims.user_group;

    alter table colims.identification_file 
        add constraint FK_jqdmoeocgmo37fgetf0t0p0pd 
        foreign key (l_search_and_val_settings_id) 
        references colims.search_and_validation_settings;

    alter table colims.instrument 
        add constraint FK_nv913lhryr1tf3jil03u2j699 
        foreign key (l_detector_cv_id) 
        references colims.instrument_cv_term;

    alter table colims.instrument 
        add constraint FK_9mg83ll40kt1k3vs8wwrcxx84 
        foreign key (l_instrument_type_id) 
        references colims.instrument_type;

    alter table colims.instrument 
        add constraint FK_amxbo4ld6m7kbyr2w00mmamf4 
        foreign key (l_source_cv_id) 
        references colims.instrument_cv_term;

    alter table colims.instrument_has_analyzer 
        add constraint FK_p0fc15jd6hhc75kw5cmq041pj 
        foreign key (l_instrument_cv_term_id) 
        references colims.instrument_cv_term;

    alter table colims.instrument_has_analyzer 
        add constraint FK_7xy0454upil50qvcv1ongxlcm 
        foreign key (l_instrument_id) 
        references colims.instrument;

    alter table colims.material 
        add constraint FK_2emr349t8q9hje4hjcd4jwuas 
        foreign key (l_cell_type_cv_id) 
        references colims.material_cv_term;

    alter table colims.material 
        add constraint FK_sfmjhs53n62t8v7xstsb9ln2 
        foreign key (l_compartment_cv_id) 
        references colims.material_cv_term;

    alter table colims.material 
        add constraint FK_1w1sodocqmm73kw7ll3uslwqi 
        foreign key (l_project_id) 
        references colims.project;

    alter table colims.material 
        add constraint FK_e8ustpafqp4yhgp0ycperpy6u 
        foreign key (l_species_cv_id) 
        references colims.material_cv_term;

    alter table colims.material 
        add constraint FK_8yfpw6v3pfg5f8wrrkgboud1r 
        foreign key (l_tissue_cv_id) 
        references colims.material_cv_term;

    alter table colims.peptide 
        add constraint FK_nouta5locpa5t9o6yl2smm0xe 
        foreign key (l_identification_file_id) 
        references colims.identification_file;

    alter table colims.peptide 
        add constraint FK_obt49wd19s38x8lpknbne8a3y 
        foreign key (l_spectrum_id) 
        references colims.spectrum;

    alter table colims.peptide_has_modification 
        add constraint FK_fon9f459diy97xrnqjb3dfj04 
        foreign key (l_modification_id) 
        references colims.modification;

    alter table colims.peptide_has_modification 
        add constraint FK_tnhhusqmuwc88nl8yo0t3r7f1 
        foreign key (l_peptide_id) 
        references colims.peptide;

    alter table colims.peptide_has_protein 
        add constraint FK_7d9fvws08o48ai89aqi3ph8l9 
        foreign key (l_main_group_protein_id) 
        references colims.protein;

    alter table colims.peptide_has_protein 
        add constraint FK_nljccjaj0g8pbmfy9sdjfghce 
        foreign key (l_peptide_id) 
        references colims.peptide;

    alter table colims.peptide_has_protein 
        add constraint FK_i6ufytgonthuo7d6246l7rkme 
        foreign key (l_protein_id) 
        references colims.protein;

    alter table colims.project 
        add constraint FK_nont146vvfurex1thyophrki0 
        foreign key (l_owner_user_id) 
        references colims.colims_user;

    alter table colims.project_has_user 
        add constraint FK_t0ksfthfrcpvmr7fdnmk81rhc 
        foreign key (l_user_id) 
        references colims.colims_user;

    alter table colims.project_has_user 
        add constraint FK_7l0pdupx29tr1yh2cv2omkla4 
        foreign key (l_project_id) 
        references colims.project;

    alter table colims.protein_accession 
        add constraint FK_7mnvsvxdpjtoi386cssgoyp00 
        foreign key (l_protein_id) 
        references colims.protein;

    alter table colims.protocol 
        add constraint FK_68woyi6fqi6j99t2511tiayb2 
        foreign key (l_cell_based_cv_id) 
        references colims.protocol_cv_term;

    alter table colims.protocol 
        add constraint FK_r8omo1sbwto3f96hycuqgosxw 
        foreign key (l_enzyme_cv_id) 
        references colims.protocol_cv_term;

    alter table colims.protocol 
        add constraint FK_ipxj4jmmfsh21ebk41sir499o 
        foreign key (l_reduction_cv_id) 
        references colims.protocol_cv_term;

    alter table colims.protocol_has_chemical_labeling 
        add constraint FK_hg4pc56r12d348ibd3q4mexk 
        foreign key (l_chemical_labeling_cv_term_id) 
        references colims.protocol_cv_term;

    alter table colims.protocol_has_chemical_labeling 
        add constraint FK_lti01qugh58dw133ahsm7p7in 
        foreign key (l_protocol_id) 
        references colims.protocol;

    alter table colims.protocol_has_other_cv_term 
        add constraint FK_chjm79t4wyytdkud9yrtdflua 
        foreign key (l_other_cv_term_id) 
        references colims.protocol_cv_term;

    alter table colims.protocol_has_other_cv_term 
        add constraint FK_obdu1ny455r0vb44a86qapib 
        foreign key (l_protocol_id) 
        references colims.protocol;

    alter table colims.quantification 
        add constraint FK_o1pngv9c5nym7t3guesme7guy 
        foreign key (l_quantification_file_id) 
        references colims.quantification_file;

    alter table colims.quantification_file 
        add constraint FK_7uqibw8f0ebtk6kyd9yyne6mt 
        foreign key (l_quant_settings_id) 
        references colims.quantification_settings;

    alter table colims.quantification_group 
        add constraint FK_jjmhtgpt9pj69yftuldr43byk 
        foreign key (l_peptide_id) 
        references colims.peptide;

    alter table colims.quantification_group 
        add constraint FK_rndw3g424dyq25bytkskynfr6 
        foreign key (l_quantification_id) 
        references colims.quantification;

    alter table colims.quantification_settings 
        add constraint FK_7yyahob7fruseylo348enfa7d 
        foreign key (l_experiment_id) 
        references colims.experiment;

    alter table colims.quantification_settings 
        add constraint FK_opawj8lyblvsk6bifd8g898id 
        foreign key (l_quant_engine_id) 
        references colims.quantification_engine;

    alter table colims.quantification_settings 
        add constraint FK_cjhxlyajclvflr45jxcw09oet 
        foreign key (l_quant_param_settings_id) 
        references colims.quant_parameter_settings;

    alter table colims.role_has_permission 
        add constraint FK_sp2yl1puui1jbdknkehlvhxq4 
        foreign key (l_permission_id) 
        references colims.permission;

    alter table colims.role_has_permission 
        add constraint FK_30ri4nqak6uechie8onfsy489 
        foreign key (l_role_id) 
        references colims.group_role;

    alter table colims.sample 
        add constraint FK_fx5esu8n3umhihw8kt19593yt 
        foreign key (l_experiment_id) 
        references colims.experiment;

    alter table colims.sample 
        add constraint FK_pbpctnedgs4yjfsu6vo6b7et8 
        foreign key (l_protocol_id) 
        references colims.protocol;

    alter table colims.sample_binary_file 
        add constraint FK_ek4x2rdy0on1ncwt4xa445d6c 
        foreign key (l_sample_id) 
        references colims.sample;

    alter table colims.sample_has_material 
        add constraint FK_8yuapgtl822resfo0cuhm1uv0 
        foreign key (l_material_id) 
        references colims.material;

    alter table colims.sample_has_material 
        add constraint FK_82ptqcd4sp8nrghikp7i9crpv 
        foreign key (l_sample_id) 
        references colims.sample;

    alter table colims.search_and_validation_settings 
        add constraint FK_ris4310042dlrxtaocuejb2x7 
        foreign key (l_experiment_id) 
        references colims.experiment;

    alter table colims.search_and_validation_settings 
        add constraint FK_4o2jjp2a6a1vaff68fl6kj4ck 
        foreign key (l_fasta_db_id) 
        references colims.fasta_db;

    alter table colims.search_and_validation_settings 
        add constraint FK_bnrf68wq188wbw6fq69yk83hi 
        foreign key (l_search_engine_id) 
        references colims.search_engine;

    alter table colims.search_and_validation_settings 
        add constraint FK_1c0io12fbf8qsoebhe6n9201r 
        foreign key (l_search_param_settings_id) 
        references colims.search_parameter_settings;

    alter table colims.spectrum 
        add constraint FK_mpjgedldeff5qugyrqangh6so 
        foreign key (l_analytical_run_id) 
        references colims.analytical_run;

    alter table colims.spectrum_file 
        add constraint FK_8unfql3wnogbervsi1gl607ds 
        foreign key (l_spectrum_id) 
        references colims.spectrum;

    alter table colims.user_has_group 
        add constraint FK_j85c8a2vhyfvwiooom79aenls 
        foreign key (l_group_id) 
        references colims.user_group;

    alter table colims.user_has_group 
        add constraint FK_e2guk3ak57dupnvkd5k3u9dfk 
        foreign key (l_user_id) 
        references colims.colims_user;
