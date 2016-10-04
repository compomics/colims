package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.parameters.PSParameter;
import eu.isas.peptideshaker.parameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesPeptideMapperTest {

    private PTM oxidation = PTMFactory.getInstance().getPTM("Oxidation of M");
    private PTM phosphorylation = PTMFactory.getInstance().getPTM("Phosphorylation of Y");
    private CvTerm nonUtilitiesPtm = new CvTerm("PSI-MOD", "MOD:01645", "L-proline removal", "-97.052764");
    private String nonUtilitiesPtmName = "L-proline removal";
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;

    /**
     * Test the mapping of an Compomics Utilities to a Colims peptide.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapPeptide() throws MappingException, IOException {
        //create the necessary Utilities objects
        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<>());
        sourcePeptide.setParentProteins(new ArrayList<>());
        PeptideAssumption peptideAssumption = new PeptideAssumption(sourcePeptide, 1, 1, new Charge(1, 2), 45.0);
        SpectrumMatch spectrumMatch = new SpectrumMatch("key", peptideAssumption);
        spectrumMatch.setBestPeptideAssumption(peptideAssumption);

        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch(oxidation.getName(), true, 7);
        modificationMatches.add(oxidationMatch);
        ModificationMatch phosphorylationMatch = new ModificationMatch(phosphorylation.getName(), true, 1);
        modificationMatches.add(phosphorylationMatch);
        ModificationMatch nonUtilitiesModificationMatch = new ModificationMatch(nonUtilitiesPtmName, true, 5);
        modificationMatches.add(nonUtilitiesModificationMatch);

        sourcePeptide.setModificationMatches(modificationMatches);

        //create PSPtmScores
        PSPtmScores ptmScores = new PSPtmScores();

        PtmScoring ptmScoring = new PtmScoring(oxidation.getName());
        double oxidationScore = 100.0;
        ptmScoring.setProbabilisticScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScoring.setDeltaScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScores.addPtmScoring(oxidation.getName(), ptmScoring);

        ptmScoring = new PtmScoring(phosphorylation.getName());
        double phosphorylationScore = 200.0;
        ptmScoring.setProbabilisticScore(phosphorylationMatch.getModificationSite(), phosphorylationScore);
        ptmScoring.setDeltaScore(phosphorylationMatch.getModificationSite(), phosphorylationScore);
        ptmScores.addPtmScoring(phosphorylation.getName(), ptmScoring);

        ptmScoring = new PtmScoring(nonUtilitiesPtmName);
        double nonUtilitiesPtmScore = 300.0;
        ptmScoring.setProbabilisticScore(nonUtilitiesModificationMatch.getModificationSite(), nonUtilitiesPtmScore);
        ptmScoring.setDeltaScore(nonUtilitiesModificationMatch.getModificationSite(), nonUtilitiesPtmScore);
        ptmScores.addPtmScoring(nonUtilitiesPtmName, ptmScoring);

        spectrumMatch.addUrParam(ptmScores);

        //create spectrum score
        PSParameter spectrumScore = new PSParameter();
        spectrumScore.setPsmProbability(0.99);

        Peptide targetPeptide = new Peptide();
        utilitiesPeptideMapper.map(spectrumMatch, spectrumScore, targetPeptide);

        Assert.assertEquals(sourcePeptide.getSequence(), targetPeptide.getSequence());
        Assert.assertEquals(sourcePeptide.getMass(), targetPeptide.getTheoreticalMass(), 0.001);
        Assert.assertEquals(2, targetPeptide.getCharge().intValue());
        Assert.assertEquals(spectrumScore.getPsmProbabilityScore(), targetPeptide.getPsmProbability(), 0.001);
        Assert.assertEquals(spectrumScore.getPsmProbability(), targetPeptide.getPsmPostErrorProbability(), 0.001);
        List<PeptideHasModification> peptideHasModifications = targetPeptide.getPeptideHasModifications();
        Assert.assertFalse(peptideHasModifications.isEmpty());
        Assert.assertEquals(3, peptideHasModifications.size());
        for (PeptideHasModification peptideHasModification : peptideHasModifications) {
            Assert.assertNotNull(peptideHasModification.getDeltaScore());
            Assert.assertNotNull(peptideHasModification.getProbabilityScore());
            Assert.assertNotNull(peptideHasModification.getProbabilityScore());
            Assert.assertNotNull(peptideHasModification.getLocation());

            Assert.assertNotNull(peptideHasModification.getPeptide());
            Assert.assertNotNull(peptideHasModification.getModification());
        }
    }
}
