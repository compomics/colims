package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantEvidenceParser;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParameterParser;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantProteinGroupParser;
import com.compomics.colims.model.enums.QuantificationWeight;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    private Map<String, String> parameters = new HashMap<>();
    @Autowired
    private MaxQuantParameterParser maxQuantParameterParser;
    private File folder;
    private File proteinGroupFile;
    private File quantFile;
    @Autowired
    MaxQuantProteinGroupParser maxQuantProteinGroupParser;
    @Autowired
    MaxQuantEvidenceParser maxQuantEvidenceParser;

    /**
     * constructor for running the Identification integration tests
     * 
     * @throws java.io.IOException
     */
    public MaxQuantPsmParserTest() throws IOException, HeaderEnumNotInitialisedException {
        folder = new ClassPathResource("data/maxquant_1512").getFile();
        proteinGroupFile = new ClassPathResource("data/maxquant_1512/proteinGroups.txt").getFile();
        quantFile = new ClassPathResource("data/maxquant_1512/evidence.txt").getFile();
        //  proteinGroupFileNoMatches = new File(getClass().getClassLoader().getResource("data/maxquant/proteinGroups.txt").getPath());
        //parameters = maxQuantParameterParser.parseExperimentParams(new ClassPathResource("data/maxquant").getFile());
        parameters.put("multiplicity", "2");
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
        assertThat(proteinGroupMap.keySet().size(), is(131));
        assertThat(proteinGroupMap.get(128), is(notNullValue()));
        //assertThat(proteinGroupMap.get(1438).getPeptideCount(),is(7));
        //assertThat(Integer.parseInt(proteinGroupMap.get(1438).getPeptideMatches().get(3)),is(7150));
        //assertThat(proteinGroupMap.get(1438).isDecoy(), is(false));
        assertThat(proteinGroupMap.get(9999), is(nullValue()));
        //assertThat(proteinGroupMap.get(1759).isDecoy(), is(true));

        maxQuantEvidenceParser.parse(folder, parameters.get("multiplicity"));

        //then test if the peptides were properly parsed
        assertThat(maxQuantEvidenceParser.peptideAssumptions.size(), is(580));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(4).getPeptide().getSequence(),is(not(nullValue())));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(4).getPeptide().getSequence(), is("AAATPESQEPQAK"));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(4).getPeptide().getParentProteinsNoRemapping().size(), is(1));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(4).getPeptide().getMass(), closeTo(1326.6415, 0.0001));

        //is unmodified
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(4).getPeptide().getModificationMatches().isEmpty(), is(true));

        //test modifications
        //acetyl only
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(51),is(not(nullValue())));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(51).getPeptide().getModificationMatches().size(), is(1));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(51).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("acetyl (protein n-term)"));
        //is N-term
        //assertThat(maxQuantEvidenceParser.peptideAssumptions.get(175).getPeptide().getModificationMatches().get(0).getModificationSite(), is(0));
        //oxidation only
        //assertThat(maxQuantEvidenceParser.peptideAssumptions.get(2249).getPeptide().getModificationMatches().size(), is(1));
        //assertThat(maxQuantEvidenceParser.peptideAssumptions.get(2249).getPeptide().getModificationMatches().get(0).getTheoreticPtm(), is("oxidation (m)"));
        //assertThat(maxQuantEvidenceParser.peptideAssumptions.get(2249).getPeptide().getModificationMatches().get(0).getModificationSite(), is(11));
        //both (don't have an entry for this yet)

        //and test if the assumptions were parsed correctly
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(231).getScore(), is(136.7));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(240).getScore(), is(125.72));

        //test link between protein groups and peptides
        assertThat(proteinGroupMap.get(Integer.parseInt(maxQuantEvidenceParser.peptideAssumptions.get(246).getPeptide().getParentProteinsNoRemapping().get(0))).getMainMatch(), is("P29351-3"));
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(2).getPeptide().getParentProteinsNoRemapping().size(), is(1));
        assertThat(Integer.parseInt(maxQuantEvidenceParser.peptideAssumptions.get(1).getPeptide().getParentProteinsNoRemapping().get(0)), is(72));

        maxQuantEvidenceParser.clear();
        maxQuantEvidenceParser.parse(folder, parameters.get("multiplicity"));

        //first test if the quantifications are parsed correctly
        assertThat(maxQuantEvidenceParser.quantifications.keySet().size(), is(580));

        assertThat(maxQuantEvidenceParser.quantifications.get(11).get(0).getIntensity(), is(153270.0));
        assertThat(maxQuantEvidenceParser.quantifications.get(11).get(1).getIntensity(), is(27554.0));

        assertThat(maxQuantEvidenceParser.quantifications.get(11).get(1).getWeight(), is(QuantificationWeight.HEAVY));
        assertThat(maxQuantEvidenceParser.quantifications.get(11).get(0).getWeight(), is(QuantificationWeight.LIGHT));
    }

    /**
     * this test checks if not supplying the extra files needed for retrieving
     * the PSMs is still handled and reported in a sensible manner
     *
     * @throws Exception
     *//*
    @Test(expected = IOException.class)
    public void noProteinsFound() throws Exception {
        System.out.println("missing proteingroups file");

        //only parse the peptides and see if it throws any kinks next to nullpointers
        Map<Integer, PeptideAssumption> maxQuantEvidenceParser.peptideAssumptions = maxQuantEvidenceParser.parse(evidenceFile);
        assertThat(maxQuantEvidenceParser.peptideAssumptions.get(417).getPeptide().getParentProteinsNoRemapping().size(), is(1));
        Map<Integer, ProteinMatch> proteinList = new HashMap<>();
        //it appears the peptides got parsed, now to blow up the proteingroups
        proteinList = maxQuantProteinGroupParser.parse(new File("this file does not exist"));
        assertThat(proteinList.get(Integer.parseInt(maxQuantEvidenceParser.peptideAssumptions.get(200).getPeptide().getParentProteinsNoRemapping().get(0))), is(nullValue()));
    }*/
}
