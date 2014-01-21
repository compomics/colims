package com.compomics.colims.core.io.parser.impl;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.enums.QuantificationWeight;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantPsmParserTest {

    private File evidenceFile;
    private File proteinGroupFile;
    private File quantFile;
    @Autowired
    MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    MaxQuantPSMParser maxQuantEvidenceParser;
    /**
     * constructor for running the Identification integration tests
     * 
     * @throws java.io.IOException
     */
    public MaxQuantPsmParserTest() throws IOException {
        evidenceFile = new ClassPathResource("data/maxquant/evidence_subset_1000.tsv").getFile();
        proteinGroupFile = new ClassPathResource("data/maxquant/proteinGroups_subset.tsv").getFile();
        quantFile = new ClassPathResource("data/maxquant/evidence_subset_quant10.tsv").getFile();
        //  proteinGroupFileNoMatches = new File(getClass().getClassLoader().getResource("data/maxquant/proteinGroups.txt").getPath());
    }

    /**
     * this test represents the standard run through Max Quant for retrieving
     * PSMs
     */
    @Test
    public void goldenPath() throws Exception {
        System.out.println("golden path test");
        //atm cursory test

        Map<Integer, ProteinMatch> proteinGroupMap = maxQuantProteinGroupParser.parse(proteinGroupFile);

        //first test if the proteingroups are parsed correctly
        assertThat(proteinGroupMap.keySet().size(), is(1760));
        assertThat(proteinGroupMap.get(1438), is(notNullValue()));
        //assertThat(proteinGroupMap.get(1438).getPeptideCount(),is(7));
        //assertThat(Integer.parseInt(proteinGroupMap.get(1438).getPeptideMatches().get(3)),is(7150));
        //assertThat(proteinGroupMap.get(1438).isDecoy(), is(false));
        assertThat(proteinGroupMap.get(9999), is(nullValue()));
        //assertThat(proteinGroupMap.get(1759).isDecoy(), is(true));

        Map<Integer, PeptideAssumption> parsedPeptides = maxQuantEvidenceParser.parse(evidenceFile);

        //then test if the peptides were properly parsed
        assertThat(parsedPeptides.size(), is(2408));
        assertThat(parsedPeptides.get(4).getPeptide().getSequence(),is(not(nullValue())));
        assertThat(parsedPeptides.get(4).getPeptide().getSequence(), is("AAAAGENEEWTTDYPHFADVADQEGFPAIATMYR"));
        assertThat(parsedPeptides.get(4).getPeptide().getParentProteins().size(), is(2));
        assertThat(parsedPeptides.get(4).getPeptide().getMass(), closeTo(3743.6475, 0.0001));

        //is unmodified
        assertThat(parsedPeptides.get(4).getPeptide().getModificationMatches().isEmpty(), is(true));

        //test modifications
        //acetyl only
        assertThat(parsedPeptides.get(175),is(not(nullValue())));
        assertThat(parsedPeptides.get(175).getPeptide().getModificationMatches().size(), is(1));
        assertThat(parsedPeptides.get(175).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("acetyl (protein n-term)"));
        //is N-term
        assertThat(parsedPeptides.get(175).getPeptide().getModificationMatches().get(0).getModificationSite(), is(0));
        //oxidation only
        assertThat(parsedPeptides.get(2249).getPeptide().getModificationMatches().size(), is(1));
        assertThat(parsedPeptides.get(2249).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("oxidation (m)"));
        assertThat(parsedPeptides.get(2249).getPeptide().getModificationMatches().get(0).getModificationSite(), is(11));
        //both (don't have an entry for this yet)

        //and test if the assumptions were parsed correctly
        assertThat(parsedPeptides.get(2319).getScore(), is(109.6));
        assertThat(parsedPeptides.get(2407).getScore(), is(107.17));

        //test link between protein groups and peptides
        assertThat(proteinGroupMap.get(Integer.parseInt(parsedPeptides.get(2246).getPeptide().getParentProteins().get(0))).getMainMatch(), is("Q9VPR3"));
        assertThat(parsedPeptides.get(2).getPeptide().getParentProteins().size(), is(2));
        assertThat(Integer.parseInt(parsedPeptides.get(2).getPeptide().getParentProteins().get(1)), is(1100));

        Map<Integer, List<Quantification>> quantificationMap = MaxQuantQuantificationParser.parseMaxQuantQuantification(quantFile);

        //first test if the quantifications are parsed correctly
        assertThat(quantificationMap.keySet().size(), is(15));

        assertThat(quantificationMap.get(11).get(0).getIntensity(), is(2169200.0));
        assertThat(quantificationMap.get(11).get(1).getIntensity(), is(2294200.0));

        assertThat(quantificationMap.get(11).get(1).getWeight(), is(QuantificationWeight.HEAVY));
        assertThat(quantificationMap.get(11).get(0).getWeight(), is(QuantificationWeight.LIGHT));
    }

    /**
     * this test checks if not supplying the extra files needed for retrieving
     * the PSMs is still handled and reported in a sensible manner
     *
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void noProteinsFound() throws Exception {
        System.out.println("missing proteingroups file");

        //only parse the peptides and see if it throws any kinks next to nullpointers
        Map<Integer, PeptideAssumption> parsedPeptides = maxQuantEvidenceParser.parse(evidenceFile);
        assertThat(parsedPeptides.get(417).getPeptide().getParentProteins().size(), is(1));
        Map<Integer, ProteinMatch> proteinList = new HashMap<>();
        //it appears the peptides got parsed, now to blow up the proteingroups
        proteinList = maxQuantProteinGroupParser.parse(new File("this file does not exist"));
        assertThat(proteinList.get(Integer.parseInt(parsedPeptides.get(200).getPeptide().getParentProteins().get(0))), is(nullValue()));
    }
}
