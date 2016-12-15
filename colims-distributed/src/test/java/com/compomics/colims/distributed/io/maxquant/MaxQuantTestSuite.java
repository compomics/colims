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
    public static Path msmsFile;
    public static Path proteinGroupsFile;
    public static Path evidenceFile;
    public static Path mqparFile;
    public static Path peptidesFile;
    public static FastaDb testFastaDb;
    public static Path testFastaDbPath;
    public static FastaDb contaminantsFastaDb;
    public static Path contaminantsFastaDbPath;

    static {
        try {
            maxQuantTestDataDirectory = new ClassPathResource("data" + File.separator + "maxquant").getFile().toPath();
            maxQuantIntegrationProjectDirectory = maxQuantTestDataDirectory.resolve(MAXQUANT_INTEGRATION_PROJECT);
            maxQuantCombinedDirectory = maxQuantIntegrationProjectDirectory.resolve("combined");
            maxQuantAndromedaDirectory = maxQuantCombinedDirectory.resolve("andromeda");
            maxQuantTextDirectory = maxQuantCombinedDirectory.resolve("txt");
            msmsFile = maxQuantTextDirectory.resolve("msms.txt");
            proteinGroupsFile = maxQuantTextDirectory.resolve("proteinGroups.txt");
            evidenceFile = maxQuantTextDirectory.resolve("evidence.txt");
            peptidesFile = maxQuantTextDirectory.resolve("peptides.txt");
            mqparFile = maxQuantIntegrationProjectDirectory.resolve("mqpar.xml");
            testFastaDbPath = maxQuantIntegrationProjectDirectory.resolve("SP_human.fasta");
            testFastaDb = new FastaDb();
            testFastaDb.setName("test fasta");
            testFastaDb.setFileName("SP_human.fasta");
            testFastaDb.setFilePath("SP_human.fasta");
            testFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            testFastaDb.setVersion("N/A");
            testFastaDb.setDatabaseName("test db");
            contaminantsFastaDbPath = maxQuantIntegrationProjectDirectory.resolve("contaminants.fasta");
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