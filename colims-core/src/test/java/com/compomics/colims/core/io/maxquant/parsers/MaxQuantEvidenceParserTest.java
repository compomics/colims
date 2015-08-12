package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.enums.QuantificationWeight;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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

        assertThat(maxQuantEvidenceParser.peptides.size(), not(0));
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
    public void testCreatePeptide() throws Exception {
        Map<String, String> values = new HashMap<>();

        values.put(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName(), "106.2");
        values.put(MaxQuantEvidenceHeaders.CHARGE.getDefaultColumnName(), "2");
        values.put(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName(), "TAVCDIPPR");
        values.put(MaxQuantEvidenceHeaders.MASS.getDefaultColumnName(), "1027.51206");
        values.put(MaxQuantEvidenceHeaders.PROTEINS.getDefaultColumnName(), "tr|B2RSN3|B2RSN3_MOUSE;sp|Q9CWF2|TBB2B_MOUSE;sp|Q7TMM9|TBB2A_MOUSE;tr|Q99J49|Q99J49_MOUSE;tr|Q62363|Q62363_MOUSE;sp|P68372|TBB4B_MOUSE;tr|Q9DCR1|Q9DCR1_MOUSE;tr|Q80ZV2|Q80ZV2_MOUSE;sp|Q9D6F9|TBB4A_MOUSE;sp|P99024|TBB5_MOUSE");
        values.put(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getDefaultColumnName(), "218;407;234;295");
        values.put(MaxQuantEvidenceHeaders.MS_MS_IDS.getDefaultColumnName(), "982;983;984");
        values.put(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName(), "0.9");
        values.put(MaxQuantEvidenceHeaders.MODIFICATIONS.getDefaultColumnName(), "Unmodified");

        Peptide peptide = maxQuantEvidenceParser.createPeptide(values);

        // check assumption details
        assertThat(peptide.getPsmProbability(), is(106.2));
        assertThat(peptide.getPsmPostErrorProbability(), is(0.9));
        assertThat(peptide.getTheoreticalMass(), is(1027.51206));
        assertThat(peptide.getCharge(), is(2));
        assertThat(peptide.getSequence(), is("TAVCDIPPR"));
    }
}