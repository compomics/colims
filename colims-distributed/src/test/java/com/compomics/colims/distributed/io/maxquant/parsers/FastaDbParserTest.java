package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Niels Hulstaert on 7/10/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-simple-test-context.xml"})
public class FastaDbParserTest {

    @Autowired
    private FastaDbParser fastaDbParser;

    @Test
    public void testParse() throws IOException {
        List<FastaDb> fastaDbs = new ArrayList<>();
        fastaDbs.add(MaxQuantTestSuite.testFastaDb);
        fastaDbs.add(MaxQuantTestSuite.contaminantsFastaDb);

        Map<String, String> parsedContaminantsFasta = fastaDbParser.parseFastas(fastaDbs);

        Assert.assertEquals(33322, parsedContaminantsFasta.size());
        //look for the first protein
        Assert.assertTrue(parsedContaminantsFasta.containsKey("sp|Q9D103|IFM1_MOUSE"));
        //look for a protein
        Assert.assertTrue(parsedContaminantsFasta.containsKey("sp|P59764|DOCK4_MOUSE"));
        //look for the last protein
        Assert.assertTrue(parsedContaminantsFasta.containsKey("sp|Q6IUF9|Z_MACHU"));
        //look for a contaminants protein
        Assert.assertTrue(parsedContaminantsFasta.containsKey("P09870"));
    }
}
