package com.compomics.colims.core.io.fasta;

import com.compomics.colims.model.FastaDb;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Niels Hulstaert on 7/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-simple-test-context.xml"})
public class FastaDbParserTest {

    private FastaDb testFastaDb;
    private FastaDb contaminantsFastaDb;

    @Autowired
    private FastaDbParser fastaDbParser;

    public FastaDbParserTest() throws IOException {
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
        Map<FastaDb, Path> fastaDbs = new HashedMap();
        fastaDbs.put(testFastaDb, Paths.get(testFastaDb.getFilePath()));
        fastaDbs.put(contaminantsFastaDb, Paths.get(contaminantsFastaDb.getFilePath()));

        Map<String, String> parsedFastas = fastaDbParser.parseFastas(fastaDbs);

        Assert.assertEquals(20380, parsedFastas.size());
        //look for the first protein
        Assert.assertTrue(parsedFastas.containsKey("P24844"));
        //look for a protein
        Assert.assertTrue(parsedFastas.containsKey("O00571"));
        //look for the last protein
        Assert.assertTrue(parsedFastas.containsKey("E9PAV3"));
        //look for 3 contaminants proteins
        Assert.assertTrue(parsedFastas.containsKey("P09870"));
        Assert.assertTrue(parsedFastas.containsKey("P05784"));
        Assert.assertTrue(parsedFastas.containsKey("H-INV:HIT000292931"));
    }
}
