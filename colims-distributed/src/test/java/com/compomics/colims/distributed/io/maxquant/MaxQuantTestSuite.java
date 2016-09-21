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
        MaxQuantParserTest.class,
        MaxQuantProteinGroupsParserTest.class,
        MaxQuantSpectraParserTest.class,
        TabularFileIteratorTest.class,
        FixedTabularFileIteratorTest.class,
        MaxQuantHeadersTest.class
})
public class MaxQuantTestSuite {

    private static final String maxQuantVersion = "1528";

    public static Path maxQuantDirectory;
    public static Path maxQuantCombinedDirectory;
    public static Path maxQuantTextDirectory;
    public static Path maxQuantAndromedaDirectory;
    public static Path msmsFile;
    public static Path proteinGroupsFile;
    public static Path evidenceFile;
    public static Path parameterFile;
    public static Path peptidesFile;
    public static FastaDb testFastaDb;
    public static FastaDb contaminantsFastaDb;
    public static Path parameterDirectory;

    static {
        try {
            maxQuantDirectory = new ClassPathResource("data" + File.separator + "maxquant_" + maxQuantVersion).getFile().toPath();
            maxQuantCombinedDirectory = new ClassPathResource("data" + File.separator + "maxquant_" + maxQuantVersion + File.separator + "combined").getFile().toPath();
            maxQuantAndromedaDirectory = new ClassPathResource("data" + File.separator + "maxquant_" + maxQuantVersion + File.separator + "combined" + File.separator + "andromeda").getFile().toPath();
            String txtDirectoryPath = "data" + File.separator + "maxquant_" + maxQuantVersion + File.separator + "combined" + File.separator + "txt";
            maxQuantTextDirectory = new ClassPathResource(txtDirectoryPath).getFile().toPath();
            txtDirectoryPath += File.separator;
            msmsFile = new ClassPathResource(txtDirectoryPath + "msms.txt").getFile().toPath();
            proteinGroupsFile = new ClassPathResource(txtDirectoryPath + "proteinGroups.txt").getFile().toPath();
            evidenceFile = new ClassPathResource(txtDirectoryPath + "evidence.txt").getFile().toPath();
            parameterFile = new ClassPathResource(txtDirectoryPath + "parameters.txt").getFile().toPath();
            peptidesFile = new ClassPathResource(txtDirectoryPath + "peptides.txt").getFile().toPath();
            String txtParameterDirectory = "data" + File.separator + "maxquant_" + maxQuantVersion + File.separator + "mqpar.xml";
            parameterDirectory = new ClassPathResource(txtParameterDirectory).getFile().toPath();
            testFastaDb = new FastaDb();
            testFastaDb.setName("test fasta");
            testFastaDb.setFileName("uniprot-mouse.fasta");
            testFastaDb.setFilePath(new ClassPathResource(txtDirectoryPath + "uniprot-mouse.fasta").getFile().getAbsolutePath());
            testFastaDb.setHeaderParseRule("&gt;([^ ]*)");
            contaminantsFastaDb = new FastaDb();
            contaminantsFastaDb.setName("test contaminants fasta");
            contaminantsFastaDb.setFileName("contaminants.fasta");
            contaminantsFastaDb.setFilePath(new ClassPathResource(txtDirectoryPath + "contaminants.fasta").getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}