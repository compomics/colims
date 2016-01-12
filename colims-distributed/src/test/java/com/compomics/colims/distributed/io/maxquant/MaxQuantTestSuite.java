package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.parsers.*;
import com.compomics.colims.model.FastaDb;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * Created by Iain on 07/04/2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MaxQuantEvidenceParserTest.class,
        MaxQuantParameterParserTest.class,
        MaxQuantParserTest.class,
        MaxQuantProteinGroupParserTest.class,
        MaxQuantSpectrumParserTest.class,
        TabularFileLineValuesIteratorTest.class
})
public class MaxQuantTestSuite {

    private static final String maxQuantVersion = "1.5.2.8";

    public static File maxQuantTextFolder;
    public static File msmsFile;
    public static File proteinGroupsFile;
    public static File evidenceFile;
    public static File parameterFile;
    public static File peptidesFile;
    public static FastaDb testFastaDb;
    public static FastaDb contaminantsFastaDb;

    static {
        try {
            String path = "data/maxquant_" + maxQuantVersion;
            maxQuantTextFolder = new ClassPathResource(path).getFile();
            msmsFile = new ClassPathResource(path + "/msms.txt").getFile();
            proteinGroupsFile = new ClassPathResource(path + "/proteinGroups.txt").getFile();
            evidenceFile = new ClassPathResource(path + "/evidence.txt").getFile();
            parameterFile = new ClassPathResource(path + "/parameters.txt").getFile();
            peptidesFile = new ClassPathResource(path + "/peptides.txt").getFile();
            testFastaDb = new FastaDb();
            testFastaDb.setName("test fasta");
            testFastaDb.setFileName("uniprot-taxonomy%3A10090.fasta");
            testFastaDb.setFilePath(new ClassPathResource(path + "/uniprot-taxonomy%3A10090.fasta").getFile().getAbsolutePath());
            contaminantsFastaDb = new FastaDb();
            contaminantsFastaDb.setName("test contaminants fasta");
            contaminantsFastaDb.setFileName("contaminants.fasta");
            contaminantsFastaDb.setFilePath(new ClassPathResource(path + "/contaminants.fasta").getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}