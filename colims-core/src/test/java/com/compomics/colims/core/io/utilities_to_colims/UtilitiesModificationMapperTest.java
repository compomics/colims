package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.bean.PtmFactoryWrapper;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.preferences.ModificationProfile;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PtmToPrideMap;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesModificationMapperTest {

    @Autowired
    private PtmCvTermMapper ptmCvTermMapper;
    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;
    @Autowired
    private ModificationService modificationService;
    @Autowired
    private PtmFactoryWrapper ptmFactoryWrapper;
    private SearchParameters searchParameters;
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
    public void loadSearchParameters() throws FileNotFoundException, IOException, XmlPullParserException {
        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
        ptmFactoryWrapper.getPtmFactory().clearFactory();
        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);

        //get PTMs from PTMFactory
        oxidation = ptmFactoryWrapper.getPtmFactory().getPTM("oxidation of m");
        phosphorylation = ptmFactoryWrapper.getPtmFactory().getPTM("phosphorylation of y");
        nonUtilitiesPtmName = "L-proline removal";
        nonUtilitiesPtm = new CvTerm("PSI-MOD", "MOD:01645", "L-proline removal", "-97.052764");

        searchParameters = new SearchParameters();

        ModificationProfile modificationProfile = new ModificationProfile();
        modificationProfile.addFixedModification(oxidation);
        modificationProfile.addFixedModification(phosphorylation);

        searchParameters.setModificationProfile(modificationProfile);

        try {
            //reset PtmToPrideMap for consistent testing
            ptmCvTermMapper.setPtmToPrideMap(new PtmToPrideMap());
            //update mapper with the SearchParameters
            ptmCvTermMapper.updatePtmToPrideMap(searchParameters);
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            Assert.fail();
        }

        //add the non utilities PTM to the ptmCvTermMapper
        ptmCvTermMapper.addCvTerm(nonUtilitiesPtmName, nonUtilitiesPtm);
    }

    /**
     * Test the mapping for a peptide with 3 modifications, none of them are
     * present in the db.
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

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

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
            Assert.assertNotNull(peptideHasModification.getAlphaScore());
            Assert.assertNotNull(peptideHasModification.getDeltaScore());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
            if (modification.getName().equals("monohydroxylated residue")) {
                Assert.assertEquals("monohydroxylated residue", modification.getName());
                Assert.assertEquals(oxidation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(oxidationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
                Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                Assert.assertEquals(oxidationScore, peptideHasModification.getDeltaScore(), 0.001);
                Assert.assertEquals(oxidationScore, peptideHasModification.getDeltaScore(), 0.001);
            } else if (modification.getName().equals("phosphorylated residue")) {
                Assert.assertEquals("phosphorylated residue", modification.getName());
                Assert.assertEquals(phosphorylation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(phosphorylationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
                Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                Assert.assertEquals(phosphorylationScore, peptideHasModification.getAlphaScore(), 0.001);
                Assert.assertEquals(phosphorylationScore, peptideHasModification.getDeltaScore(), 0.001);
            } else if (modification.getName().equals("L-proline removal")) {
                Assert.assertEquals("L-proline removal", modification.getName());
                Assert.assertEquals(Double.parseDouble(nonUtilitiesPtm.getValue()), modification.getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(nonUtilitiesModificationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
                Assert.assertEquals(ModificationType.VARIABLE, peptideHasModification.getModificationType());
                Assert.assertEquals(nonUtilitiesPtmScore, peptideHasModification.getAlphaScore(), 0.001);
                Assert.assertEquals(nonUtilitiesPtmScore, peptideHasModification.getDeltaScore(), 0.001);
            } else {
                Assert.fail();
            }
        }

    }

    /**
     * Test the mapping for a peptide with 1 modification, which is present in
     * the db.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_2() throws MappingException, IOException {
        //create ModificationMatches       
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch("methionine oxidation with neutral loss of 64 Da", false, 7);
        modificationMatches.add(oxidationMatch);

        //create PSPtmScores
        PSPtmScores ptmScores = new PSPtmScores();

        PtmScoring ptmScoring = new PtmScoring(oxidationMatch.getTheoreticPtm());
        double oxidationScore = 100.0;
        ptmScoring.setProbabilisticScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScoring.setDeltaScore(oxidationMatch.getModificationSite(), oxidationScore);
        ptmScores.addPtmScoring(oxidationMatch.getTheoreticPtm(), ptmScoring);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

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
            Assert.assertNull(peptideHasModification.getAlphaScore());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNotNull(modification.getId());
            Modification dbModification = modificationService.findByName("methionine oxidation with neutral loss of 64 Da");
            Assert.assertEquals(dbModification, modification);
        }

    }

    /**
     * Test the mapping for a peptide with 1 nonsense modification. The
     * modification is not found in the db, the PtmToPrideMap or the ols
     * service.
     *
     * @throws MappingException
     * @throws IOException
     */
    @Test
    public void testMapModification_3() throws MappingException, IOException {
        //create ModificationMatches       
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch("nonsense modification", true, 7);
        modificationMatches.add(oxidationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

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