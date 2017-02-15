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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
        contaminantsFastaDb.setHeaderParseRule("&gt;([^ ]*)");
        contaminantsFastaDb.setDatabaseName("N/A");
    }

    @Test
    public void testParse() throws IOException {
        LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
        fastaDbs.put(testFastaDb, Paths.get(testFastaDb.getFilePath()));
        fastaDbs.put(contaminantsFastaDb, Paths.get(contaminantsFastaDb.getFilePath()));

        Map<String, String> parsedFastas = fastaDbParser.parse(fastaDbs);

        Assert.assertFalse(parsedFastas.containsKey(null));
        Assert.assertEquals(20379, parsedFastas.size());
        //look for the first protein
        Assert.assertTrue(parsedFastas.containsKey("P24844"));
        System.out.println(parsedFastas.get("P24844"));
        //look for a protein
        Assert.assertTrue(parsedFastas.containsKey("O00571"));
        //look for the last protein
        Assert.assertTrue(parsedFastas.containsKey("E9PAV3"));
        //look for 3 contaminants proteins
        Assert.assertTrue(parsedFastas.containsKey("P09870"));
        Assert.assertTrue(parsedFastas.containsKey("P05784"));
        Assert.assertTrue(parsedFastas.containsKey("H-INV:HIT000292931"));

        //test a duplicate accession between the 2 FASTA DB files
        Assert.assertTrue(parsedFastas.containsKey("P19013"));
        //check if the duplicate accession is associated with the sequence from the primary FASTA
        Assert.assertEquals("MIARQQCVRGGPRGFSCGSAIVGGGKRGAFSSVSMSGGAGRCSSGG" +
                "FGSRSLYNLRGNKSISMSVAGSRQGACFGGAGGFGTGGFGAGGFGAGFGTGGFGGGFGGSFSGKGGP" +
                "GFPVCPAGGIQEVTINQSLLTPLHVEIDPEIQKVRTEEREQIKLLNNKFASFIDKVQFLEQQNKVLE" +
                "TKWNLLQQQTTTTSSKNLEPLFETYLSVLRKQLDTLGNDKGRLQSELKTMQDSVEDFKTKYEEEINKR" +
                "TAAENDFVVLKKDVDAAYLNKVELEAKVDSLNDEINFLKVLYDAELSQMQTHVSDTSVVLSMDNNRNL" +
                "DLDSIIAEVRAQYEEIAQRSKAEAEALYQTKVQQLQISVDQHGDNLKNTKSEIAELNRMIQRLRAEIE" +
                "NIKKQCQTLQVSVADAEQRGENALKDAHSKRVELEAALQQAKEELARMLREYQELMSVKLALDIEIAT" +
                "YRKLLEGEEYRMSGECQSAVSISVVSGSTSTGGISGGLGSGSGFGLSSGFGSGSGSGFGFGGSVSGSS" +
                "SSKIISTTTLNKRR", parsedFastas.get("P19013"));
    }

    @Test
    public void testParseAccessions() throws IOException {
        LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap();
        fastaDbs.put(testFastaDb, Paths.get(testFastaDb.getFilePath()));
        fastaDbs.put(contaminantsFastaDb, Paths.get(contaminantsFastaDb.getFilePath()));

        Map<FastaDb, Set<String>> parsedFastas = fastaDbParser.parseAccessions(fastaDbs);

        Assert.assertEquals(2, parsedFastas.size());
        Assert.assertEquals(20195, parsedFastas.get(testFastaDb).size());
        Assert.assertEquals(245, parsedFastas.get(contaminantsFastaDb).size());
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

        //check duplicates: both FASTA DBs contain the protein with accession "P19013"
        Assert.assertTrue(parsedFastas.get(testFastaDb).contains("P19013"));
        Assert.assertTrue(parsedFastas.get(contaminantsFastaDb).contains("P19013"));
    }

    @Test
    public void testTestParseRule() throws IOException {
        Map<String, String> headers = fastaDbParser.testParseRule(Paths.get(testFastaDb.getFilePath()), testFastaDb.getHeaderParseRule(), 10);

        System.out.println("========");

        headers = fastaDbParser.testParseRule(Paths.get(contaminantsFastaDb.getFilePath()), contaminantsFastaDb.getHeaderParseRule(), 10);

        System.out.println("========");
    }
}
