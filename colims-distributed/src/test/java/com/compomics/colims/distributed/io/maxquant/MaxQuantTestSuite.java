package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantHeadersTest;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantEvidenceParserTest;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantProteinGroupsParserTest;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSearchSettingsParserTest;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSpectraParserTest;
import com.compomics.colims.model.FastaDb;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Iain on 07/04/2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MaxQuantEvidenceParserTest.class,
        MaxQuantSearchSettingsParserTest.class,
        MaxQuantProteinGroupsParserTest.class,
        MaxQuantSpectraParserTest.class,
        TabularFileIteratorTest.class,
        FixedTabularFileIteratorTest.class,
        MaxQuantHeadersTest.class
})
public class MaxQuantTestSuite {

    private static final String MAXQUANT_INTEGRATION_PROJECT = "maxquant_SILAC_integration";

    public static Path maxQuantTestDataDirectory;
    public static Path maxQuantIntegrationProjectDirectory;
    public static Path maxQuantCombinedDirectory;
    public static Path maxQuantTextDirectory;
    public static Path maxQuantAndromedaDirectory;
    public static Path maxQuantFastasDirectory;
    public static Path msmsFile;
    public static Path proteinGroupsFile;
    public static Path evidenceFile;
    public static Path mqparFile;
    public static Path peptidesFile;
    public static FastaDb spHumanFastaDb;
    public static Path spHumanFastaDbPath;
    public static FastaDb spHuman_01_2017_FastaDb;
    public static Path spHuman_01_2017_FastaDbPath;
    public static FastaDb oryzaFastaDb;
    public static Path oryzaFastaDbPath;
    public static FastaDb lloFastaDb;
    public static Path lloFastaDbPath;
    public static FastaDb contaminantsFastaDb;
    public static Path contaminantsFastaDbPath;

    static {
        try {
            maxQuantTestDataDirectory = new ClassPathResource("data" + File.separator + "maxquant").getFile().toPath();
            maxQuantIntegrationProjectDirectory = maxQuantTestDataDirectory.resolve(MAXQUANT_INTEGRATION_PROJECT);
            maxQuantFastasDirectory = maxQuantTestDataDirectory.resolve("fasta");
            maxQuantCombinedDirectory = maxQuantIntegrationProjectDirectory.resolve("combined");
            maxQuantAndromedaDirectory = maxQuantCombinedDirectory.resolve("andromeda");
            maxQuantTextDirectory = maxQuantCombinedDirectory.resolve("txt");
            msmsFile = maxQuantTextDirectory.resolve("msms.txt");
            proteinGroupsFile = maxQuantTextDirectory.resolve("proteinGroups.txt");
            evidenceFile = maxQuantTextDirectory.resolve("evidence.txt");
            peptidesFile = maxQuantTextDirectory.resolve("peptides.txt");
            mqparFile = maxQuantIntegrationProjectDirectory.resolve("mqpar.xml");
            //sp_human fasta
            spHumanFastaDbPath = maxQuantFastasDirectory.resolve("SP_human.fasta");
            spHumanFastaDb = new FastaDb();
            spHumanFastaDb.setName("sp_human");
            spHumanFastaDb.setFileName("SP_human.fasta");
            spHumanFastaDb.setFilePath("SP_human.fasta");
            spHumanFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            spHumanFastaDb.setVersion("N/A");
            spHumanFastaDb.setDatabaseName("test db");
            //sp_human fasta version 01_2017
            spHuman_01_2017_FastaDbPath = maxQuantFastasDirectory.resolve("SP_human_01_2017.fasta");
            spHuman_01_2017_FastaDb = new FastaDb();
            spHuman_01_2017_FastaDb.setName("sp_human");
            spHuman_01_2017_FastaDb.setFileName("SP_human.fasta");
            spHuman_01_2017_FastaDb.setFilePath("SP_human.fasta");
            spHuman_01_2017_FastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            spHuman_01_2017_FastaDb.setVersion("N/A");
            spHuman_01_2017_FastaDb.setDatabaseName("test db");
            //oryza sativa fasta
            oryzaFastaDbPath = maxQuantFastasDirectory.resolve("Oryza_sativa_RefSeq.fasta");
            oryzaFastaDb = new FastaDb();
            oryzaFastaDb.setName("Oryza_sativa_RefSeq");
            oryzaFastaDb.setFileName("Oryza_sativa_RefSeq.fasta");
            oryzaFastaDb.setFilePath("Oryza_sativa_RefSeq.fasta");
            oryzaFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            oryzaFastaDb.setVersion("N/A");
            oryzaFastaDb.setDatabaseName("test db");
            //llo fasta
            lloFastaDbPath = maxQuantFastasDirectory.resolve("LLO_sequence_EGD_RefSeq.fasta");
            lloFastaDb = new FastaDb();
            lloFastaDb.setName("LLO_sequence_EGD_RefSeq");
            lloFastaDb.setFileName("LLO_sequence_EGD_RefSeq.fasta");
            lloFastaDb.setFilePath("LLO_sequence_EGD_RefSeq.fasta");
            lloFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            lloFastaDb.setVersion("N/A");
            lloFastaDb.setDatabaseName("test db");
            //contaminants fasta
            contaminantsFastaDbPath = maxQuantFastasDirectory.resolve("contaminants.fasta");
            contaminantsFastaDb = new FastaDb();
            contaminantsFastaDb.setName("test contaminants fasta");
            contaminantsFastaDb.setFileName("contaminants.fasta");
            contaminantsFastaDb.setFilePath("contaminants.fasta");
            contaminantsFastaDb.setVersion("N/A");
            contaminantsFastaDb.setDatabaseName("N/A");
        } catch (IOException e) {
            //do nothing
        }
    }
}