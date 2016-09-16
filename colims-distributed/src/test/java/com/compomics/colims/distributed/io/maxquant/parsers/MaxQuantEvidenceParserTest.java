package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.distributed.io.maxquant.headers.EvidenceHeaders;
import com.compomics.colims.model.Peptide;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

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
        evidenceFile = new ClassPathResource("data/maxquant/evidence_unit_test.csv").getFile().toPath();
    }

    @Test
    public void testParse() throws Exception {
        maxQuantEvidenceParser.clear();
        List<String> ommittedProteinIds = new ArrayList<>();
        ommittedProteinIds.add("0");
        ommittedProteinIds.add("1");
//        maxQuantEvidenceParser.parse(MaxQuantTestSuite.evidenceFile, ommittedProteinIds);
        maxQuantEvidenceParser.parse(evidenceFile, ommittedProteinIds);

        assertThat(maxQuantEvidenceParser.getPeptides().size(), not(0));
    }

    @Test
    public void testCreatePeptide() throws Exception {
        maxQuantEvidenceParser.clear();

        Map<String, String> values = new HashMap<>();

        values.put(EvidenceHeaders.SCORE.getValue(), "106.2");
        values.put(EvidenceHeaders.CHARGE.getValue(), "2");
        values.put(EvidenceHeaders.SEQUENCE.getValue(), "TAVCDIPPR");
        values.put(EvidenceHeaders.MASS.getValue(), "1027.51206");
        values.put(EvidenceHeaders.PROTEINS.getValue(), "tr|B2RSN3|B2RSN3_MOUSE;sp|Q9CWF2|TBB2B_MOUSE;sp|Q7TMM9|TBB2A_MOUSE;tr|Q99J49|Q99J49_MOUSE;tr|Q62363|Q62363_MOUSE;sp|P68372|TBB4B_MOUSE;tr|Q9DCR1|Q9DCR1_MOUSE;tr|Q80ZV2|Q80ZV2_MOUSE;sp|Q9D6F9|TBB4A_MOUSE;sp|P99024|TBB5_MOUSE");
        values.put(EvidenceHeaders.PROTEIN_GROUP_IDS.getValue(), "218;407;234;295");
        values.put(EvidenceHeaders.MS_MS_IDS.getValue(), "982;983;984");
        values.put(EvidenceHeaders.PEP.getValue(), "0.9");
        values.put(EvidenceHeaders.MODIFICATIONS.getValue(), "Unmodified");

        Peptide peptide = maxQuantEvidenceParser.createPeptide(values);

        // check assumption details
        assertThat(peptide.getPsmProbability(), is(106.2));
        assertThat(peptide.getPsmPostErrorProbability(), is(0.9));
        assertThat(peptide.getTheoreticalMass(), is(1027.51206));
        assertThat(peptide.getCharge(), is(2));
        assertThat(peptide.getSequence(), is("TAVCDIPPR"));
    }

    @Test
    public void testCreateSingleModification() throws ModificationMappingException {
        maxQuantEvidenceParser.clear();

        Map<String, String> values = new HashMap<>();

        values.put(EvidenceHeaders.SCORE.getValue(), "106.2");
        values.put(EvidenceHeaders.CHARGE.getValue(), "2");
        values.put(EvidenceHeaders.SEQUENCE.getValue(), "TAVCDIPPR");
        values.put(EvidenceHeaders.MASS.getValue(), "1027.51206");
        values.put(EvidenceHeaders.MODIFICATIONS.getValue(), "Acetyl (Protein N-term)");
        values.put(EvidenceHeaders.ACETYL_PROTEIN_N_TERM.getValue(), "1");

        Peptide peptide = maxQuantEvidenceParser.createPeptide(values);

        assertThat(peptide.getPeptideHasModifications().size(), not(0));
        assertThat(peptide.getPeptideHasModifications().get(0).getProbabilityScore(), is(100.0));
        assertThat(peptide.getPeptideHasModifications().get(0).getLocation(), is(0));
    }

    @Test
    public void testCreateMultipleModifications() throws ModificationMappingException {
        maxQuantEvidenceParser.clear();

        Map<String, String> values = new HashMap<>();

        values.put(EvidenceHeaders.SCORE.getValue(), "106.2");
        values.put(EvidenceHeaders.CHARGE.getValue(), "2");
        values.put(EvidenceHeaders.SEQUENCE.getValue(), "TAVCDIPPR");
        values.put(EvidenceHeaders.MASS.getValue(), "1027.51206");
        values.put(EvidenceHeaders.MODIFICATIONS.getValue(), "3 Oxidation (M)");
        values.put(EvidenceHeaders.OXIDATION_M_PROBABILITIES.getValue(), "GFM(0.852)VTRSYTVGVM(0.716)M(0.716)M(0.716)HR");
        values.put(EvidenceHeaders.OXIDATION_M_SCORE_DIFFS.getValue(), "GFM(2.82)VTRSYTVGVM(0)M(0)M(0)HR");
        values.put(EvidenceHeaders.OXIDATION_M.getValue(), "3");

        Peptide peptide = maxQuantEvidenceParser.createPeptide(values);

        assertThat(peptide.getPeptideHasModifications().size(), not(0));
        assertThat(peptide.getPeptideHasModifications().get(0).getDeltaScore(), is(2.82));
        assertThat(peptide.getPeptideHasModifications().get(0).getLocation(), is(3));
        assertThat(peptide.getPeptideHasModifications().get(1).getLocation(), is(13));
        assertThat(peptide.getPeptideHasModifications().get(2).getLocation(), is(14));
        assertThat(peptide.getPeptideHasModifications().get(3).getLocation(), is(15));
    }
}
