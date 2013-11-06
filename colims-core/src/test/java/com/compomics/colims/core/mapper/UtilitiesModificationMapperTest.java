package com.compomics.colims.core.mapper;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.impl.UtilitiesModificationMapper;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationScoreType;
import com.compomics.colims.model.enums.ModificationTypeEnum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.preferences.ModificationProfile;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.io.FileNotFoundException;
import org.junit.Before;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesModificationMapperTest {

    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;
    @Autowired
    private ModificationService modificationService;
    private PTMFactory pTMFactory = PTMFactory.getInstance();
    private SearchParameters searchParameters;
    private PTM oxidation;
    private PTM phosphorylation;

    @Before
    public void loadSearchParameters() throws FileNotFoundException, IOException {
        //get PTMs from PTMFactory
        oxidation = pTMFactory.getPTM("oxidation of m");
        phosphorylation = pTMFactory.getPTM("phosphorylation of y");

        searchParameters = new SearchParameters();

        ModificationProfile modificationProfile = new ModificationProfile();
        modificationProfile.addFixedModification(oxidation);
        modificationProfile.addFixedModification(phosphorylation);

        searchParameters.setModificationProfile(modificationProfile);

        try {
            //update mapper with the SearchParameters
            utilitiesModificationMapper.update(searchParameters);
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            Assert.fail();
        }
    }

    /*
     * Test the mapping for a peptide with 2 modifications, none of them are present in the db.
     */
    @Test
    public void testMapModification_1() throws MappingException {
        //create ModificationMatches       
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch(oxidation.getName(), true, 7);
        modificationMatches.add(oxidationMatch);
        ModificationMatch phosphorylationMatch = new ModificationMatch(phosphorylation.getName(), true, 1);
        modificationMatches.add(phosphorylationMatch);

        //create PSPtmScores
        PSPtmScores ptmScores = new PSPtmScores();

        PtmScoring ptmScoring = new PtmScoring(oxidation.getName());
        ArrayList<Integer> locations = new ArrayList();
        locations.add(oxidationMatch.getModificationSite());
        double oxidationScore = 100.0;
        ptmScoring.addDeltaScore(locations, oxidationScore);
        ptmScores.addPtmScoring(oxidation.getName(), ptmScoring);

        ptmScoring = new PtmScoring(phosphorylation.getName());
        locations = new ArrayList();
        locations.add(phosphorylationMatch.getModificationSite());
        double phosphorylationScore = 200.0;
        ptmScoring.addDeltaScore(locations, phosphorylationScore);
        ptmScores.addPtmScoring(phosphorylation.getName(), ptmScoring);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

        utilitiesModificationMapper.map(modificationMatches, ptmScores, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(2, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getModificationType());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());
            Assert.assertNotNull(peptideHasModification.getModificationScoreType());
            Assert.assertNotNull(peptideHasModification.getScore());
            
            Assert.assertEquals(ModificationScoreType.DELTA, peptideHasModification.getModificationScoreType());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
            if (modification.getName().equals(oxidation.getName())) {
                Assert.assertEquals(oxidation.getName(), modification.getName());
                Assert.assertEquals(oxidation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(oxidationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
                Assert.assertEquals(ModificationTypeEnum.VARIABLE, peptideHasModification.getModificationType());                
                Assert.assertEquals(oxidationScore, peptideHasModification.getScore(), 0.001);
            } else if (modification.getName().equals(phosphorylation.getName())) {
                Assert.assertEquals(phosphorylation.getName(), modification.getName());
                Assert.assertEquals(phosphorylation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(phosphorylationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
                Assert.assertEquals(ModificationTypeEnum.FIXED, peptideHasModification.getModificationType());
                Assert.assertEquals(phosphorylationScore, peptideHasModification.getScore(), 0.001);
            }
        }

    }

    /*
     * Test the mapping for a peptide with 1 modification, which is present in the db.
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
        ArrayList<Integer> locations = new ArrayList();
        locations.add(oxidationMatch.getModificationSite());
        double oxidationScore = 100.0;
        ptmScoring.addDeltaScore(locations, oxidationScore);
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
            Assert.assertNull(peptideHasModification.getModificationScoreType());
            Assert.assertNull(peptideHasModification.getScore());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNotNull(modification.getId());
            Modification dbModification = modificationService.findByName("methionine oxidation with neutral loss of 64 Da");
            Assert.assertEquals(dbModification, modification);
        }

    }

    /*
     * Test the mapping for a peptide with 1 nonsense modification. The mapper should throw a MappingException.
     */
    @Test(expected = MappingException.class)
    public void testMapModification_3() throws MappingException, IOException {
        //create ModificationMatches       
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        ModificationMatch oxidationMatch = new ModificationMatch("nonsense modification", true, 7);
        modificationMatches.add(oxidationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

        utilitiesModificationMapper.map(modificationMatches, null, targetPeptide);
    }
}
