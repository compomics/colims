package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Niels Hulstaert on 7/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class FastaDbAccessionParserTest {

    private FastaDb testFastaDb;
    private FastaDb contaminantsFastaDb;

    @Autowired
    private FastaDbAccessionParser fastaDbAccessionParser;

    public FastaDbAccessionParserTest() throws IOException {
        testFastaDb = new FastaDb();
        testFastaDb.setName("test fasta");
        testFastaDb.setFileName("SP_human.fasta");
        testFastaDb.setFilePath(new ClassPathResource("data" + File.separator + "SP_human.fasta").getFile().getPath());
        testFastaDb.setHeaderParseRule("&gt;.*\\|(.*)\\|");
        testFastaDb.setVersion("N/A");
        testFastaDb.setDatabaseName("test db");
        contaminantsFastaDb = new FastaDb();
        contaminantsFastaDb.setName("test contaminants fasta");
        contaminantsFastaDb.setFileName("contaminants.fasta");
        contaminantsFastaDb.setFilePath(new ClassPathResource("data" + File.separator + "contaminants.fasta").getFile().getPath());
        contaminantsFastaDb.setVersion("N/A");
        contaminantsFastaDb.setDatabaseName("N/A");
    }

    @Test
    public void testParse() throws IOException {
        List<FastaDb> fastaDbs = new ArrayList<>();
        fastaDbs.add(testFastaDb);
        fastaDbs.add(contaminantsFastaDb);

        Map<FastaDb, Set<String>> parsedFastas = fastaDbAccessionParser.parseFastas(fastaDbs);

        Assert.assertEquals(20380, parsedFastas.size());
        //look for the first protein
        Assert.assertTrue(parsedFastas.get(testFastaDb).contains("P24844"));
        //look for a protein
        Assert.assertTrue(parsedFastas.get(testFastaDb).contains("O00571"));
        //look for the last protein
        Assert.assertTrue(parsedFastas.get(testFastaDb).contains("E9PAV3"));
        //look for 3 contaminants proteins
        Assert.assertTrue(parsedFastas.get(contaminantsFastaDb).contains("P09870"));
        Assert.assertTrue(parsedFastas.get(contaminantsFastaDb).contains("P05784"));
        Assert.assertTrue(parsedFastas.get(contaminantsFastaDb).contains("H-INV:HIT000292931"));
    }
}
