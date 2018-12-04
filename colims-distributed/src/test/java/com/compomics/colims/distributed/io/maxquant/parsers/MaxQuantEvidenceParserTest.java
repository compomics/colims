package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.QuantificationMethod;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.compomics.colims.model.enums.QuantificationMethod.SILAC;

/**
 * Created by Iain on 19/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantEvidenceParserTest {

    private final Path evidenceFile;
    private final LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
    private final EnumMap<FastaDbType, List<FastaDb>> fastaDbEnumMap = new EnumMap<>(FastaDbType.class);

    @Autowired
    private MaxQuantEvidenceParser maxQuantEvidenceParser;
    @Autowired
    private MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    public MaxQuantEvidenceParserTest() throws IOException {
        evidenceFile = new ClassPathResource("data/maxquant/evidence_unit_test_modifications.txt").getFile().toPath();
    }

    @Test
    public void testParse() throws Exception {
        maxQuantEvidenceParser.clear();

        Set<Integer> omittedProteinIds = new HashSet<>();
        omittedProteinIds.add(0);
        omittedProteinIds.add(1);

        maxQuantEvidenceParser.parse(evidenceFile, null, omittedProteinIds, QuantificationMethod.SILAC, new ArrayList<>());

        //check the number of MBR identifications
        Assert.assertEquals(9, maxQuantEvidenceParser.getRunToMbrPeptides().size());

        //check the size of the spectrumToPeptides map
        Assert.assertEquals(14, maxQuantEvidenceParser.getSpectrumToPeptides().size());

        //check the size of the peptideToProteins map
        Assert.assertEquals(22, maxQuantEvidenceParser.getPeptideToProteinGroups().size());

        //check the parsed modifications
        //check a peptide with a terminal modification
        List<Peptide> peptides = maxQuantEvidenceParser.getPeptides().get(737);
        Peptide peptide = peptides.get(0);
        Assert.assertNull(peptide.getPsmProbability());
        Assert.assertNull(peptide.getPsmPostErrorProbability());
        Assert.assertEquals(2, peptide.getCharge().intValue());
        Assert.assertEquals(1029.52771, peptide.getTheoreticalMass(), 0.001);
        Assert.assertEquals(-1.3998, peptide.getMassError(), 0.001);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        PeptideHasModification peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(0, peptideHasModification.getLocation().intValue());
        Assert.assertNull(peptideHasModification.getProbabilityScore());
        Assert.assertNull(peptideHasModification.getDeltaScore());
        Modification modification = peptide.getPeptideHasModifications().get(0).getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:1", modification.getAccession());

        //check a peptide with a non-terminal modification
        peptides = maxQuantEvidenceParser.getPeptides().get(1154);
        //check if the this evidence ID has two associated peptides
        Assert.assertEquals(2, peptides.size());
        peptide = peptides.get(0);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(7, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(0.999, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(29.93, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:35", modification.getAccession());

        //check a peptide with 2 modifications
        peptides = maxQuantEvidenceParser.getPeptides().get(6239);
        peptide = peptides.get(0);
        Assert.assertEquals(43.066, peptide.getPsmProbability(), 0.001);
        Assert.assertEquals(0.018231, peptide.getPsmPostErrorProbability(), 0.001);
        Assert.assertEquals(2, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(5, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(1.0, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(43.07, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:35", modification.getAccession());
        peptideHasModification = peptide.getPeptideHasModifications().get(1);
        Assert.assertEquals(6, peptideHasModification.getLocation().intValue());
        Assert.assertEquals(1.0, peptideHasModification.getProbabilityScore(), 0.001);
        Assert.assertEquals(43.07, peptideHasModification.getDeltaScore(), 0.001);
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:21", modification.getAccession());

        //check a MBR peptide, modification scores should be empty
        peptides = maxQuantEvidenceParser.getPeptides().get(2619);
        peptide = peptides.get(0);
        Assert.assertEquals(1, peptide.getPeptideHasModifications().size());
        peptideHasModification = peptide.getPeptideHasModifications().get(0);
        Assert.assertEquals(3, peptideHasModification.getLocation().intValue());
        Assert.assertNull(peptideHasModification.getProbabilityScore());
        Assert.assertNull(peptideHasModification.getDeltaScore());
        modification = peptideHasModification.getModification();
        Assert.assertNotNull(modification);
        Assert.assertEquals("UNIMOD:21", modification.getAccession());
    }

    /**
     * SILAC quantification parsing test.
     *
     * @throws java.lang.Exception in case of an exception
     */
    @Test
    public void testSilac() throws Exception {
        Path evidenceFile = new ClassPathResource("data/maxquant/SILAC/combined/txt/evidence.txt").getFile().toPath();
        Path combinedDirectory = new ClassPathResource("data/maxquant/SILAC/combined").getFile().toPath();
        Path mqparFile = new ClassPathResource("data/maxquant/SILAC/mqpar.xml").getFile().toPath();

        fastaDbs.put(MaxQuantTestSuite.spHuman_01_2017_FastaDb, MaxQuantTestSuite.spHuman_01_2017_FastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.lloFastaDb, MaxQuantTestSuite.lloFastaDbPath);
        fastaDbs.put(MaxQuantTestSuite.contaminantsFastaDb, MaxQuantTestSuite.contaminantsFastaDbPath);

        fastaDbEnumMap.put(FastaDbType.PRIMARY, Arrays.asList(MaxQuantTestSuite.oryzaFastaDb));
        fastaDbEnumMap.put(FastaDbType.ADDITIONAL, Arrays.asList(MaxQuantTestSuite.lloFastaDb));
        fastaDbEnumMap.put(FastaDbType.CONTAMINANTS, Arrays.asList(MaxQuantTestSuite.contaminantsFastaDb));

        maxQuantSearchSettingsParser.clear();
        maxQuantSearchSettingsParser.parse(combinedDirectory, mqparFile, fastaDbEnumMap);

        maxQuantEvidenceParser.clear();
        maxQuantEvidenceParser.parse(evidenceFile, null, new HashSet<>(), SILAC, new ArrayList<>());

        Peptide peptide = maxQuantEvidenceParser.getPeptides().get(26).get(0);

        Assert.assertEquals("{\"none\":1689100.0,\"Arg6;Lys4\":1056500.0}", peptide.getLabels());
        Assert.assertEquals(2745500.0, peptide.getIntensity(), 0.001);
        Assert.assertEquals(Integer.valueOf(1), peptide.getMsmsCount());
    }

}
