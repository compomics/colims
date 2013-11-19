package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Davy
 */
public class MaxQuantIdentificationIntegrationTest {

    private File evidenceFile;
    private File proteinGroupFile;
    private MaxQuantEvidenceParser evidenceParser;

    /**
     * constructor for running the Identification integration tests
     */
    public MaxQuantIdentificationIntegrationTest() {
        evidenceFile = new File(getClass().getClassLoader().getResource("testdata/evidence_subset_1000.tsv").getPath());
        proteinGroupFile = new File(getClass().getClassLoader().getResource("testdata/proteinGroups.txt").getPath());
        //  proteinGroupFileNoMatches = new File(getClass().getClassLoader().getResource("testdata/proteinGroups.txt").getPath());
        evidenceParser = new MaxQuantEvidenceParser();
    }

    /**
     * this test represents the standard run through Max Quant for retrieving
     * PSMs
     */
    @Test
    public void goldenPath() throws Exception {
        System.out.println("golden path test");
        //atm cursory test

        Map<Integer, ProteinMatch> proteinGroupMap = MaxQuantProteinGroupParser.parseMaxQuantProteinGroups(proteinGroupFile);

        //first test if the proteingroups are parsed correctly

        assertThat(proteinGroupMap.keySet().size(), is(1760));
        assertThat(proteinGroupMap.get(1438), is(notNullValue()));
        //assertThat(proteinGroupMap.get(1438).isDecoy(), is(false));
        assertThat(proteinGroupMap.get(9999), is(nullValue()));
        //assertThat(proteinGroupMap.get(1759).isDecoy(), is(true));

        List<PeptideAssumption> parsedPeptides = evidenceParser.parse(evidenceFile, null);

        //then test if the peptides were properly parsed

        assertThat(parsedPeptides.size(), is(999));
        assertThat(parsedPeptides.get(4).getPeptide().getSequence(), is("AAAAGENEEWTTDYPHFADVADQEGFPAIATMYR"));
        assertThat(parsedPeptides.get(4).getPeptide().getParentProteins().size(), is(2));
        assertThat(new BigDecimal(parsedPeptides.get(4).getPeptide().getMass()).setScale(4, RoundingMode.UP).doubleValue(), is(3743.6475));

        //is unmodified
        assertThat(parsedPeptides.get(4).getPeptide().getModificationMatches().isEmpty(), is(true));

        //test modifications
        //acetyl only
        assertThat(parsedPeptides.get(86).getPeptide().getModificationMatches().size(), is(1));
        assertThat(parsedPeptides.get(86).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("Acetyl (Protein N-term)"));
        //is N-term
        assertThat(parsedPeptides.get(86).getPeptide().getModificationMatches().get(0).getModificationSite(), is(0));
        //oxidation only
        assertThat(parsedPeptides.get(903).getPeptide().getModificationMatches().size(), is(1));
        assertThat(parsedPeptides.get(903).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("Oxidation (M) Probabilities"));
        assertThat(parsedPeptides.get(903).getPeptide().getModificationMatches().get(0).getModificationSite(), is(10));
        //both (don't have an entry for this yet)

        //and test if the assumptions were parsed correctly
        assertThat(parsedPeptides.get(950).getScore(), is(109.6));
        assertThat(parsedPeptides.get(998).getScore(), is(107.17));

        //test link between protein groups and peptides
        assertThat(proteinGroupMap.get(Integer.parseInt(parsedPeptides.get(900).getPeptide().getParentProteins().get(0))).getMainMatch(), is("Q9VPR3"));
        assertThat(parsedPeptides.get(1).getPeptide().getParentProteins().size(), is(2));
        assertThat(Integer.parseInt(parsedPeptides.get(1).getPeptide().getParentProteins().get(1)), is(1100));
    }

    /**
     * this test checks if not supplying the extra files needed for retrieving
     * the PSMs is still handled and reported in a sensible manner
     *
     * @throws Exception
     */
    @Test
    public void noProteinsFound() throws Exception {
        System.out.println("missing proteingroups file");

        //only parse the peptides and see if it throws any kinks next to nullpointers
        List<PeptideAssumption> parsedPeptides = evidenceParser.parse(evidenceFile, null);
        assertThat(parsedPeptides.get(200).getPeptide().getParentProteins().size(), is(1));
        Map<Integer, ProteinMatch> proteinList = new HashMap<>();
        //it appears the peptides got parsed, now to blow up the proteingroups
        boolean errorThrown = false;
        try {
            proteinList = MaxQuantProteinGroupParser.parseMaxQuantProteinGroups(new File("this file does not exist"));
        } catch (FileNotFoundException fnfe) {
            errorThrown = true;
        }
        assertThat(errorThrown, is(true));
        assertThat(proteinList.get(Integer.parseInt(parsedPeptides.get(200).getPeptide().getParentProteins().get(0))),is(nullValue()));
    }
}