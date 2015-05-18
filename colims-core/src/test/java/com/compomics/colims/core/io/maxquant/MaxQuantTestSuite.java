package com.compomics.colims.core.io.maxquant;

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
    MaxQuantImportMapperTest.class,
    MaxQuantParameterParserTest.class,
    MaxQuantProteinGroupParserTest.class,
    MaxQuantSpectrumParser.class,
    TabularFileLineValuesIteratorTest.class
})

public class MaxQuantTestSuite {

    public static File maxQuantTextFolder;
    public static File fastaFile;
    public static File msmsFile;
    public static File proteinGroupsFile;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            System.out.println("initialising maxquant folder");

            maxQuantTextFolder = new ClassPathResource("data/maxquant_1.5.2.8").getFile();
            fastaFile = new ClassPathResource("data/maxquant_1.5.2.8/uniprot-taxonomy%3A10090.fasta").getFile();
            msmsFile = new ClassPathResource("data/maxquant_1.5.2.8/msms.txt").getFile();
            proteinGroupsFile = new ClassPathResource("data/maxquant_1.5.2.8/proteinGroups.txt").getFile();
//            maxQuantTextFolder = new ClassPathResource("data/maxquant").getFile();
//            fastaFile = new ClassPathResource("data/maxquant/testfasta.fasta").getFile();
        }

        @Override
        protected void after() {
            System.out.println("AFTER");
            // placeholder for any tidying
        }
    };
}