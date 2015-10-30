package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.parameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesModificationMapperTest {

    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;
    @Autowired
    private ModificationService modificationService;
    @Autowired
    private OlsService olsService;
    private PTM oxidation;
    private PTM phosphorylation;
    private CvTerm nonUtilitiesPtm;
    private String nonUtilitiesPtmName;

    /**
     * Load the search parameters with the modifications.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XmlPullParserException
     */
    @Before
    public void loadSearchParameters() throws IOException, XmlPullParserException {
        //get PTMs from PTMFactory
        oxidation = PTMFactory.getInstance().getPTM("Oxidation of M");
        phosphorylation = PTMFactory.getInstance().getPTM("Phosphorylation of Y");
        nonUtilitiesPtmName = "L-proline removal";
        nonUtilitiesPtm = new CvTerm("PSI-MOD", "MOD:01645", "L-proline removal", "-97.052764");

        SearchParameters searchParameters = new SearchParameters();

        PtmSettings ptmSettings = new PtmSettings();
        ptmSettings.addFixedModification(oxidation);
        ptmSettings.addFixedModification(phosphorylation);

        searchParameters.setPtmSettings(ptmSettings);
    }

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache() {
        utilitiesModificationMapper.clear();
        olsService.getModificationsCache().clear();
    }

    /**
     * Test the mapping for a peptide with 3 modifications, none of them are present in the db.
     *
     * @throws MappingException
     */
    @Test
    public void testMapModification_1() throws MappingException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch(oxidation.getName(), true, 7);
        modificationMatches.add(oxidationMatch);
        ModificationMatch phosphorylationMatch = new ModificationMatch(phosphorylation.getName(), true, 1);
        modificationMatches.add(phosphorylationMatch);
        ModificationMatch nonUtilitiesModificationMatch = new ModificationMatch(nonUtilitiesPtmName, true, 5);
        modificationMatches.add(nonUtilitiesModificationMatch);

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

        //create new Colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, ptmScores, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(3, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());
            Assert.assertNotNull(peptideHasModification.getProbabilityScore());
            Assert.assertNotNull(peptideHasModification.getDeltaScore());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
            switch (modification.getName()) {
                case "Oxidation":
                    Assert.assertEquals("Oxidation", modification.getName());
                    Assert.assertEquals("UNIMOD:35", modification.getAccession());
                    Assert.assertEquals("Oxidation of M", modification.getUtilitiesName());
                    Assert.assertEquals(oxidation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                    Assert.assertEquals(oxidationMatch.getModificationSite(), (int) peptideHasModification.getLocation());
                    Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                    Assert.assertEquals(oxidationScore, peptideHasModification.getDeltaScore(), 0.001);
                    Assert.assertEquals(oxidationScore, peptideHasModification.getDeltaScore(), 0.001);
                    break;
                case "Phospho":
                    Assert.assertEquals("Phospho", modification.getName());
                    Assert.assertEquals("UNIMOD:21", modification.getAccession());
                    Assert.assertEquals("Phosphorylation of Y", modification.getUtilitiesName());
                    Assert.assertEquals(phosphorylation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                    Assert.assertEquals(phosphorylationMatch.getModificationSite(), (int) peptideHasModification.getLocation());
                    Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                    Assert.assertEquals(phosphorylationScore, peptideHasModification.getProbabilityScore(), 0.001);
                    Assert.assertEquals(phosphorylationScore, peptideHasModification.getDeltaScore(), 0.001);
                    break;
                case "L-proline removal":
                    Assert.assertEquals("L-proline removal", modification.getName());
                    Assert.assertEquals("MOD:01645", modification.getAccession());
                    Assert.assertEquals(Double.parseDouble(nonUtilitiesPtm.getValue()), modification.getMonoIsotopicMassShift(), 0.001);
                    Assert.assertEquals(nonUtilitiesModificationMatch.getModificationSite(), (int) peptideHasModification.getLocation());
                    Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                    Assert.assertEquals(nonUtilitiesPtmScore, peptideHasModification.getProbabilityScore(), 0.001);
                    Assert.assertEquals(nonUtilitiesPtmScore, peptideHasModification.getDeltaScore(), 0.001);
                    break;
                default:
                    Assert.fail();
                    break;
            }
        }

    }

    /**
     * Test the mapping for a peptide with 1 modification, which is present in the db.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_2() throws MappingException, IOException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch("Trioxidation of C", false, 7);
        modificationMatches.add(oxidationMatch);

        //create PSPtmScores
        PSPtmScores ptmScores = new PSPtmScores();

        PtmScoring ptmScoring = new PtmScoring(oxidationMatch.getTheoreticPtm());
        double oxidationScore = 100.0;
        ptmScoring.setProbabilisticScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScoring.setDeltaScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScores.addPtmScoring(oxidationMatch.getTheoreticPtm(), ptmScoring);

        //create new Colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, ptmScores, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            //since the modification is fixed, there should be no score
            Assert.assertNull(peptideHasModification.getDeltaScore());
            Assert.assertNull(peptideHasModification.getProbabilityScore());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNotNull(modification.getId());
            Assert.assertEquals("UNIMOD:345", modification.getAccession());
            Modification dbModification = modificationService.findByName("Trioxidation");
            Assert.assertEquals(dbModification, modification);
        }

    }

    /**
     * Test the mapping for a peptide with 1 UNIMOD modification. The modification is not found in the db, the
     * PtmToPrideMap or the ols service. It should be found in the UNIMOD modifications.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_3() throws MappingException, IOException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch modificationMatch = new ModificationMatch("IMEHex(2)NeuAc(1)", true, 7);
        modificationMatches.add(modificationMatch);

        //create new Colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, null, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
            Assert.assertEquals("UNIMOD:1286", modification.getAccession());
            Assert.assertNull(modification.getUtilitiesName());
        }
    }

    /**
     * Test the mapping for a peptide with a N-terminal notification. The location should be 0.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_4() throws MappingException, IOException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch modificationMatch = new ModificationMatch("Acetylation of protein N-term", true, 1);
        modificationMatches.add(modificationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, null, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            Assert.assertEquals(0, peptideHasModification.getLocation().intValue());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
        }
    }

    /**
     * Test the mapping for a peptide with a C-terminal notification. The location should be 0.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_5() throws MappingException, IOException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch modificationMatch = new ModificationMatch("Amidation of the peptide C-term", true, 7);
        modificationMatches.add(modificationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, null, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            Assert.assertEquals(8, peptideHasModification.getLocation().intValue());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
        }
    }

    /**
     * Test the mapping for a peptide with 1 nonsense modification. The modification is not found in the db, the
     * PtmToPrideMap or the ols service.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_6() throws MappingException, IOException {
        //create ModificationMatches
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch modificationMatch = new ModificationMatch("nonsense modification", true, 7);
        modificationMatches.add(modificationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();
        targetPeptide.setSequence("LENNART");

        utilitiesModificationMapper.map(modificationMatches, null, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
        }
    }
}
