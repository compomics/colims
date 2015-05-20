package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.core.io.maxquant.PeptidePosition;
import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.model.enums.QuantificationWeight;
import com.compomics.util.experiment.identification.PeptideAssumption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Iain on 19/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantEvidenceParserTest {

    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;

    @Test
    public void testParse() throws Exception {
        maxQuantEvidenceParser.parse(MaxQuantTestSuite.maxQuantTextFolder, "1");

        assertThat(maxQuantEvidenceParser.peptideAssumptions.size(), not(0));
        assertThat(maxQuantEvidenceParser.quantifications.size(), not(0));
        assertThat(maxQuantEvidenceParser.quantifications.get(0).get(0).getIntensity(), is(7431500.0));
        assertThat(maxQuantEvidenceParser.quantifications.get(0).get(0).getWeight(), is(QuantificationWeight.LIGHT));
    }

    @Test
    public void testParseIntensity() {
        // typical value
        assertThat(maxQuantEvidenceParser.parseIntensity("1285500"), is(1285500.0));
        // empty
        assertThat(maxQuantEvidenceParser.parseIntensity(""), is(0.0));
        // null
        assertThat(maxQuantEvidenceParser.parseIntensity(null), is(0.0));
        // non-numeric
        assertThat(maxQuantEvidenceParser.parseIntensity("NaN (Niet-een-getal)"), is(0.0));
    }

    @Test
    public void testCreatePeptideAssumption() throws Exception {
        TabularFileLineValuesIterator iterator = new TabularFileLineValuesIterator(MaxQuantTestSuite.evidenceFile);
        Map<String, String> values = iterator.next();

        PeptideAssumption assumption = maxQuantEvidenceParser.createPeptideAssumption(values);

        // check assumption details
        assertThat(assumption.getScore(), is(81.854));
        assertThat(assumption.getRank(), is(1));
        assertThat(assumption.getIdentificationCharge().getChargeAsFormattedString(), is("+++"));
        assertThat(assumption.getPeptide().getSequence(), is("AAHVFFTDSCPDALFNELVK"));

        MatchScore matchScore = (MatchScore) assumption.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY));

        // check the damned urparam
        assertThat(matchScore.getProbability(), is(81.854));
        assertThat(matchScore.getPostErrorProbability(), is(3.5521E-4));
    }

    @Test
    public void testGetPeptidePositions() throws IOException {
        Map<String, PeptidePosition> peptidePositions = maxQuantEvidenceParser.getPeptidePositions(MaxQuantTestSuite.peptidesFile);

        assertThat(peptidePositions.size(), not(0));
        assertTrue(peptidePositions.get("1220").getStart() < peptidePositions.get("1220").getEnd());
    }
}