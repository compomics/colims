package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.maxquant.parsers.*;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 * Created by Iain on 07/04/2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MaxQuantEvidenceParserTest.class,
    MaxQuantParameterParserTest.class,
//    MaxQuantImportMapperTest.class,
    MaxQuantProteinGroupParserTest.class,
    MaxQuantSpectrumParserTest.class,
    TabularFileLineValuesIteratorTest.class,
    MaxQuantParserTest.class
})

public class MaxQuantTestSuite {

    private static final String maxQuantVersion = "1.5.2.8";

    public static File maxQuantTextFolder;
    public static File fastaFile;
    public static File msmsFile;
    public static File proteinGroupsFile;
    public static File evidenceFile;
    public static File parameterFile;
    public static File peptidesFile;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            System.out.println("initialising data sets");

            String path = "data/maxquant_" + maxQuantVersion;

            maxQuantTextFolder = new ClassPathResource(path).getFile();
            fastaFile = new ClassPathResource(path + "/uniprot-taxonomy%3A10090.fasta").getFile();
            msmsFile = new ClassPathResource(path + "/msms.txt").getFile();
            proteinGroupsFile = new ClassPathResource(path + "/proteinGroups.txt").getFile();
            evidenceFile = new ClassPathResource(path + "/evidence.txt").getFile();
            parameterFile = new ClassPathResource(path + "/parameters.txt").getFile();
            peptidesFile = new ClassPathResource(path + "/peptides.txt").getFile();
        }

        @Override
        protected void after() {
            // placeholder for any tidying
        }
    };
}