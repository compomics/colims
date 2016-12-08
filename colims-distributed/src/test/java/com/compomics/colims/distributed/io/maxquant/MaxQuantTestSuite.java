package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantHeadersTest;
import com.compomics.colims.distributed.io.maxquant.parsers.*;
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
        MaxQuantHeadersTest.class,
        FastaDbParserTest.class
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
    public static FastaDb contaminantsFastaDb;

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
            testFastaDb = new FastaDb();
            testFastaDb.setName("test fasta");
            testFastaDb.setFileName("SP_human.fasta");
            testFastaDb.setFilePath(maxQuantIntegrationProjectDirectory.resolve(testFastaDb.getFileName()).toString());
            testFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
            testFastaDb.setVersion("N/A");
            testFastaDb.setDatabaseName("test db");
            contaminantsFastaDb = new FastaDb();
            contaminantsFastaDb.setName("test contaminants fasta");
            contaminantsFastaDb.setFileName("contaminants.fasta");
            contaminantsFastaDb.setFilePath(maxQuantIntegrationProjectDirectory.resolve(contaminantsFastaDb.getFileName()).toString());
            contaminantsFastaDb.setVersion("N/A");
            contaminantsFastaDb.setDatabaseName("N/A");
        } catch (IOException e) {
            //do nothing
        }
    }
}