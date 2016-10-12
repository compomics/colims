package com.compomics.colims.distributed.io.maxquant.parsers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iain on 19/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantEvidenceParserTest {

    private Path evidenceFile;

    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    public MaxQuantEvidenceParserTest() throws IOException {
        evidenceFile = new ClassPathResource("data/maxquant/evidence_unit_test_modifications.txt").getFile().toPath();
    }

    @Test
    public void testParse() throws Exception {
        maxQuantEvidenceParser.clear();
        List<String> ommittedProteinIds = new ArrayList<>();
        ommittedProteinIds.add("0");
        ommittedProteinIds.add("1");

        maxQuantEvidenceParser.parse(evidenceFile, ommittedProteinIds);

        System.out.println("test");

        //check the number of MBR identifications
        Assert.assertEquals(9, maxQuantEvidenceParser.getMbrPeptides().size());

        //check the size of the spectrumToPeptides map
        Assert.assertEquals(13, maxQuantEvidenceParser.getSpectrumToPeptides().size());

        //check the size of the peptideToProteins map
        Assert.assertEquals(22, maxQuantEvidenceParser.getPeptideToProteinGroups().size());
    }

}
