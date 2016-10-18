use colims;

    create table colims.analytical_run (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        name varchar(100) not null,
        start_date datetime,
        storage_location varchar(255),
        l_instrument_id bigint,
        l_sample_id bigint,
        primary key (id)
    );

    create table colims.analytical_run_binary_file (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        file_type varchar(255) not null,
        content longblob not null,
        file_name varchar(255) not null,
        l_analytical_run_id bigint,
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
        database_name varchar(255) not null,
        file_name varchar(200) not null,
        file_path varchar(250) not null,
        header_parse_rule varchar(255),
        md5_checksum varchar(255),
        name varchar(100) not null,
        version varchar(20) not null,
        l_taxonomy_cv_id bigint,
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
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.modification (
        id bigint not null auto_increment,
        accession varchar(255),
        average_mass_shift double precision,
        monoisotopic_mass_shift double precision,
        name varchar(255) not null,
        utilities_name varchar(255),
        primary key (id)
    );

    create table colims.peptide (
        id bigint not null auto_increment,
        charge integer,
        psm_post_error_prob double precision,
        psm_prob double precision,
        peptide_sequence varchar(255) not null,
        theoretical_mass double precision,
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

    create table colims.peptide_has_protein_group (
        id bigint not null auto_increment,
        peptide_post_error_prob double precision,
        peptide_prob double precision,
        l_peptide_id bigint,
        l_protein_group_id bigint,
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

    create table colims.protein_group (
        id bigint not null auto_increment,
        protein_post_error_prob double precision,
        protein_prob double precision,
        primary key (id)
    );

    create table colims.protein_group_has_protein (
        id bigint not null auto_increment,
        main_group_protein bit not null,
        protein_accession varchar(255),
        l_protein_id bigint,
        l_protein_group_id bigint,
        primary key (id)
    );

    create table colims.protein_group_quant (
        id bigint not null auto_increment,
        ibaq double precision,
        intensity double precision,
        lfq_intensity double precision,
        msms_count integer,
        l_analytical_run_id bigint,
        l_protein_group_id bigint,
        primary key (id)
    );

    create table colims.protein_group_quant_labeled (
        id bigint not null auto_increment,
        label varchar(255),
        label_value double precision,
        l_analytical_run_id bigint,
        l_protein_group_id bigint,
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

    create table colims.quantification_engine (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.quantification_method_cv_param (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
        primary key (id)
    );

    create table colims.quantification_method_has_reagent (
        id bigint not null auto_increment,
        l_quantification_method_cv_param_id bigint,
        l_quantification_reagent_id bigint,
        primary key (id)
    );

    create table colims.quantification_reagent (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
        primary key (id)
    );

    create table colims.quantification_settings (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        l_analytical_run_id bigint,
        l_quant_engine_id bigint,
        l_quant_method_cv_param bigint,
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
        l_analytical_run_id bigint,
        l_search_engine_id bigint,
        l_search_parameters_id bigint,
        primary key (id)
    );

    create table colims.search_cv_param (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
        cv_property varchar(255) not null,
        primary key (id)
    );

    create table colims.search_engine (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
        type varchar(255) not null,
        version varchar(255),
        primary key (id)
    );

    create table colims.search_modification (
        id bigint not null auto_increment,
        accession varchar(255),
        average_mass_shift double precision,
        monoisotopic_mass_shift double precision,
        name varchar(255) not null,
        utilities_name varchar(255),
        primary key (id)
    );

    create table colims.search_parameters (
        id bigint not null auto_increment,
        enzymes varchar(255),
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
        l_search_type_cv_id bigint,
        primary key (id)
    );

    create table colims.search_params_has_modification (
        id bigint not null auto_increment,
        modification_type integer,
        l_search_modification_id bigint,
        l_search_parameters_id bigint,
        primary key (id)
    );

    create table colims.search_params_has_other_cv_param (
        l_search_params_id bigint not null,
        l_other_search_cv_param_id bigint not null
    );

    create table colims.search_settings_has_fasta_db (
        id bigint not null auto_increment,
        fasta_db_type integer not null,
        l_fasta_db_id bigint,
        l_search_and_val_settings_id bigint,
        primary key (id)
    );

    create table colims.spectrum (
        id bigint not null auto_increment,
        accession varchar(500) not null,
        charge integer,
        fragmentation_type varchar(255),
        intensity double precision,
        mz_ratio double precision,
        retention_time double precision,
        scan_number varchar(255) not null,
        scan_time double precision,
        title varchar(500),
        l_analytical_run_id bigint,
        primary key (id)
    );

    create table colims.spectrum_file (
        id bigint not null auto_increment,
        content longblob not null,
        l_spectrum_id bigint,
        primary key (id)
    );

    create table colims.taxonomy_cv_param (
        id bigint not null auto_increment,
        accession varchar(255) not null,
        label varchar(255) not null,
        name varchar(255) not null,
        param_value varchar(255),
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

    create table colims.user_query (
        id bigint not null auto_increment,
        creation_date datetime not null,
        modification_date datetime not null,
        user_name varchar(255) not null,
        query_string varchar(500) not null,
        usage_count integer,
        l_user_query_user_id bigint not null,
        primary key (id)
    );

    alter table colims.colims_user
        add constraint UK_7qy96sq9o6jh5517or8yh758 unique (name);

    alter table colims.group_role
        add constraint UK_7kvrlnisllgg2md5614ywh82g unique (name);

    alter table colims.instrument
        add constraint UK_11wfouotl7vb11u6ebomnbsrr unique (name);

    alter table colims.permission
        add constraint UK_2ojme20jpga3r4r79tdso17gi unique (name);

    alter table colims.project
        add constraint UK_etb9i6krbg45bl5o1kt0cc4q8 unique (title);

    alter table colims.protocol
        add constraint UK_lidqee66itlhns030fyykvptc unique (name);

    alter table colims.user_group
        add constraint UK_kas9w8ead0ska5n3csefp2bpp unique (name);

    alter table colims.analytical_run
        add constraint FKiteej35b4sjhfx12kd02jm6iw
        foreign key (l_instrument_id)
        references colims.instrument (id);

    alter table colims.analytical_run
        add constraint FKe9t2cb0e8d0xob2qfcoly21t5
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.analytical_run_binary_file
        add constraint FKhhc6e2qdpbe1lkvwn6iypoja0
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.colims_user
        add constraint FKmb059tuvfut3ocw5q05o35njn
        foreign key (l_institution_id)
        references colims.institution (id);

    alter table colims.experiment
        add constraint FK48dysdsbha1vdr40udltld10v
        foreign key (l_project_id)
        references colims.project (id);

    alter table colims.experiment_binary_file
        add constraint FKaqv506oy06s0geln513eam413
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.fasta_db
        add constraint FKnbentn9v6kkk4vqurumyu559q
        foreign key (l_taxonomy_cv_id)
        references colims.taxonomy_cv_param (id);

    alter table colims.group_has_role
        add constraint FKd4iuk0my83rtlhhulrcrtf76x
        foreign key (l_role_id)
        references colims.group_role (id);

    alter table colims.group_has_role
        add constraint FKd4jnxgpstfi5gntmi4m61ordn
        foreign key (l_group_id)
        references colims.user_group (id);

    alter table colims.instrument
        add constraint FK1wxnrvqujdygv9ll147322weo
        foreign key (l_detector_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument
        add constraint FKoildeovchu26pb5ewxjaak4hv
        foreign key (l_source_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument
        add constraint FKmihpkan5xf8nt1256tovnqlno
        foreign key (l_type_cv_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument_has_analyzer
        add constraint FKe59386f5p3dnckvgwqtlq0eay
        foreign key (l_instrument_cv_param_id)
        references colims.instrument_cv_param (id);

    alter table colims.instrument_has_analyzer
        add constraint FKf70hvf3t0rthby8x7in9gpjc6
        foreign key (l_instrument_id)
        references colims.instrument (id);

    alter table colims.material
        add constraint FKbn76m95hfcc93i84r7f2jd0fx
        foreign key (l_cell_type_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FKsu9ti70rc3fi9aqdsm9jttdji
        foreign key (l_compartment_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FKly75y5tu933dmvgdeqvffqgvu
        foreign key (l_species_cv_id)
        references colims.material_cv_param (id);

    alter table colims.material
        add constraint FKc4mhhee3lkceh33p7661vsvc2
        foreign key (l_tissue_cv_id)
        references colims.material_cv_param (id);

    alter table colims.peptide
        add constraint FKmn3ekc0ajyk0f1b99ys1cv3kt
        foreign key (l_spectrum_id)
        references colims.spectrum (id);

    alter table colims.peptide_has_modification
        add constraint FKjpyca1ms38npf9ydijom3uu8g
        foreign key (l_modification_id)
        references colims.modification (id);

    alter table colims.peptide_has_modification
        add constraint FK9nwv51l1qn930vkydlnqjmqjk
        foreign key (l_peptide_id)
        references colims.peptide (id);

    alter table colims.peptide_has_protein_group
        add constraint FKn1jstvoawyb1elxm61oy2jaql
        foreign key (l_peptide_id)
        references colims.peptide (id);

    alter table colims.peptide_has_protein_group
        add constraint FK18pu9uw3usnltmdj6bx1f8yso
        foreign key (l_protein_group_id)
        references colims.protein_group (id);

    alter table colims.project
        add constraint FKsa6nsrbeschu8w2g96r5oxpv8
        foreign key (l_owner_user_id)
        references colims.colims_user (id);

    alter table colims.project_has_user
        add constraint FKm5mj3iioe68fjwwy23c5o41xw
        foreign key (l_user_id)
        references colims.colims_user (id);

    alter table colims.project_has_user
        add constraint FKq3ifbm2kqtbgemvp9tyx2a6l0
        foreign key (l_project_id)
        references colims.project (id);

    alter table colims.protein_accession
        add constraint FKq08c89rewk8ag0hmyydalymv1
        foreign key (l_protein_id)
        references colims.protein (id);

    alter table colims.protein_group_has_protein
        add constraint FKyk152njh2ab03m0071buyv09
        foreign key (l_protein_id)
        references colims.protein (id);

    alter table colims.protein_group_has_protein
        add constraint FKkvk2epn94kvxekgpfertigmbq
        foreign key (l_protein_group_id)
        references colims.protein_group (id);

    alter table colims.protein_group_quant
        add constraint FKkgx6adlkrte967uuwsvniaaov
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.protein_group_quant
        add constraint FK2ox1qfmdd9imbq2xcn6un2dw3
        foreign key (l_protein_group_id)
        references colims.protein_group (id);

    alter table colims.protein_group_quant_labeled
        add constraint FKeuo2o1ppm296du364nuclhq7q
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.protein_group_quant_labeled
        add constraint FKwk0o9rnoxa33jj5iycabedk5
        foreign key (l_protein_group_id)
        references colims.protein_group (id);

    alter table colims.protocol
        add constraint FKg5vc0ccy9ejrdb5hpyepyphdi
        foreign key (l_cell_based_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol
        add constraint FK1tcjjud12cme93drk0t6fg7l9
        foreign key (l_enzyme_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol
        add constraint FK3euus878cvnitmx6pejx4f7px
        foreign key (l_reduction_cv_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_chemical_labeling
        add constraint FK3mqe9s11bj22eh0ayovjyrvd8
        foreign key (l_chemical_labeling_cv_param_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_chemical_labeling
        add constraint FKt3khhbo0mifwdh8amcwy2j73d
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.protocol_has_other_cv_param
        add constraint FKpsrx2auqgdyvrslnvwept04bu
        foreign key (l_other_protocol_cv_param_id)
        references colims.protocol_cv_param (id);

    alter table colims.protocol_has_other_cv_param
        add constraint FKhtd7224thnb0iokbcu2i43lad
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.quantification_method_has_reagent
        add constraint FKdo9icce14f4d0leltn0hn4m3k
        foreign key (l_quantification_method_cv_param_id)
        references colims.quantification_method_cv_param (id);

    alter table colims.quantification_method_has_reagent
        add constraint FK267phwwnpppqtv92uwhh4xa1o
        foreign key (l_quantification_reagent_id)
        references colims.quantification_reagent (id);

    alter table colims.quantification_settings
        add constraint FK47llb2g9m544eafyltelgxgh3
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.quantification_settings
        add constraint FKesojbmir3semmlud3auxfb5ds
        foreign key (l_quant_engine_id)
        references colims.quantification_engine (id);

    alter table colims.quantification_settings
        add constraint FKtbk9q7dr62s39vuwh31296ffn
        foreign key (l_quant_method_cv_param)
        references colims.quantification_method_cv_param (id);

    alter table colims.role_has_permission
        add constraint FKl2nvbt87gatts91152i5iwhpq
        foreign key (l_permission_id)
        references colims.permission (id);

    alter table colims.role_has_permission
        add constraint FK3l7bj8uc0germq5qeeetj4pim
        foreign key (l_role_id)
        references colims.group_role (id);

    alter table colims.sample
        add constraint FKq7wo6mx1mie0bvgh0gqjrbjbn
        foreign key (l_experiment_id)
        references colims.experiment (id);

    alter table colims.sample
        add constraint FK7h9b9mhyc2kkudpdvd8qh6m0r
        foreign key (l_protocol_id)
        references colims.protocol (id);

    alter table colims.sample_binary_file
        add constraint FK76mk600o8fxbichw0v3215u0m
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.sample_has_material
        add constraint FKxna6w7shfrqeawfnk90xnspm
        foreign key (l_material_id)
        references colims.material (id);

    alter table colims.sample_has_material
        add constraint FKejc7sflcm1scs07ujask8210
        foreign key (l_sample_id)
        references colims.sample (id);

    alter table colims.search_and_validation_settings
        add constraint FK55c3ddut84yvvrf7lj2ppg01m
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.search_and_validation_settings
        add constraint FK4mfeudo2uprqiy3t0t2qm53cm
        foreign key (l_search_engine_id)
        references colims.search_engine (id);

    alter table colims.search_and_validation_settings
        add constraint FKsyl42a6frbo1ggtcf2jgytty1
        foreign key (l_search_parameters_id)
        references colims.search_parameters (id);

    alter table colims.search_parameters
        add constraint FKjum5v2h68ave00gpvn413fjj
        foreign key (l_search_type_cv_id)
        references colims.search_cv_param (id);

    alter table colims.search_params_has_modification
        add constraint FK30b5scgrsqdqc9vtlqa9tgay
        foreign key (l_search_modification_id)
        references colims.search_modification (id);

    alter table colims.search_params_has_modification
        add constraint FKj9l3us6wasvnnwkgxu631ucqf
        foreign key (l_search_parameters_id)
        references colims.search_parameters (id);

    alter table colims.search_params_has_other_cv_param
        add constraint FKswo2824uycevh3hknnvkmjfxo
        foreign key (l_other_search_cv_param_id)
        references colims.search_cv_param (id);

    alter table colims.search_params_has_other_cv_param
        add constraint FKqn6aoilaqjm9g7h2cqflc8is0
        foreign key (l_search_params_id)
        references colims.search_parameters (id);

    alter table colims.search_settings_has_fasta_db
        add constraint FK39tyom3eugvh5iwfij8hsw2ku
        foreign key (l_fasta_db_id)
        references colims.fasta_db (id);

    alter table colims.search_settings_has_fasta_db
        add constraint FKsy775royxs80n8n76v2nt7drw
        foreign key (l_search_and_val_settings_id)
        references colims.search_and_validation_settings (id);

    alter table colims.spectrum
        add constraint FKof7knxyxuasqygwpmg300rgsr
        foreign key (l_analytical_run_id)
        references colims.analytical_run (id);

    alter table colims.spectrum_file
        add constraint FKpevnbq7t0v1hrnugv0gyf577q
        foreign key (l_spectrum_id)
        references colims.spectrum (id);

    alter table colims.user_has_group
        add constraint FKn3a9e9a4s5iiflt9d7i80on4l
        foreign key (l_group_id)
        references colims.user_group (id);

    alter table colims.user_has_group
        add constraint FK4a2dwswmgrxjtwjee6ts3ievy
        foreign key (l_user_id)
        references colims.colims_user (id);

    alter table colims.user_query
        add constraint FKe1ujshd4acio6sjeg8tnr434j
        foreign key (l_user_query_user_id)
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
INSERT INTO search_engine (id, accession, label, name, type, version) VALUES (1,'N/A','N/A','PeptideShaker','PEPTIDESHAKER', '0.0.0'),(2,'MS:1001583','MS','MaxQuant','MAXQUANT', '1.5.4.1');

-- insert default quantification engines
INSERT INTO quantification_engine (id, accession, label, name, type, version) VALUES (1,'N/A','N/A','PeptideShaker','PEPTIDESHAKER', '0.0.0'),(2,'MS:1001583','MS','MaxQuant','MAXQUANT', '1.5.4.1');

-- insert some test data
INSERT INTO project (id,creation_date,modification_date,user_name,description,label,title,l_owner_user_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','test project 1 description','test project 1 label','test project 1',1);

INSERT INTO project_has_user (l_project_id,l_user_id) VALUES (1,1);

INSERT INTO experiment (id,creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','experiment 1 description',2222,'','experiment 1',1);
INSERT INTO experiment (id,creation_date,modification_date,user_name,description,number,storage_location,title,l_project_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','',15166,'','experiment 2',1);

INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1001603','SOURCE','MS','ProteomeDiscoverer:Spectrum Selector:Ionization Source');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1002308','DETECTOR','MS','fluorescence detector');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (3,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1000254','ANALYZER','MS','electrostatic energy analyzer');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (4,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1000449','TYPE','MS','LTQ Orbitrap');
INSERT INTO instrument_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (5,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','MS:1002416','TYPE','MS','Orbitrap Fusion');

INSERT INTO instrument (id,creation_date,modification_date,user_name,name,l_detector_cv_id,l_type_cv_id,l_source_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 1',2,4,1);
INSERT INTO instrument (id,creation_date,modification_date,user_name,name,l_detector_cv_id,l_type_cv_id,l_source_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','instrument 3',2,5,1);

INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_param_id) VALUES (1,3);
INSERT INTO instrument_has_analyzer (l_instrument_id,l_instrument_cv_param_id) VALUES (2,3);

INSERT INTO material_cv_param (id,creation_date,modification_date,user_name,accession,cv_property,label,name) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','9606','SPECIES','NEWT','Homo sapiens (Human)');

INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_species_cv_id,l_tissue_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 1',null,null,1,null);
INSERT INTO material (id,creation_date,modification_date,user_name,name,l_cell_type_cv_id,l_compartment_cv_id,l_species_cv_id,l_tissue_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','material 2',null,null,1,null);

INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (1,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 7',null,null,null);
INSERT INTO protocol (id,creation_date,modification_date,user_name,name,l_cell_based_cv_id,l_enzyme_cv_id,l_reduction_cv_id) VALUES (2,'2012-06-27 14:42:16','2012-06-27 14:42:16','admin','protocol 1',null,null,null);

INSERT INTO search_cv_param (id, accession, label, name, cv_property) VALUES (2, 'MS:1001083', 'PSI-MS', 'ms-ms search', 'SEARCH_TYPE');

INSERT INTO sample (id, creation_date, modification_date, user_name, name, l_experiment_id, l_protocol_id) VALUES (1,'2012-11-08 16:51:11','2012-11-08 16:51:11','admin','sample 1',1,1);
