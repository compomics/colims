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
import com.compomics.colims.core.mapper.impl.UtilitiesPeptideMapper;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.preferences.ModificationProfile;
import java.io.FileNotFoundException;
import java.util.List;
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

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

        utilitiesModificationMapper.map(modificationMatches, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(2, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

            Modification modification = peptideHasModification.getModification();
            Assert.assertNull(modification.getId());
            if (modification.getName().equals(oxidation.getName())) {
                Assert.assertEquals(oxidation.getName(), modification.getName());
                Assert.assertEquals(oxidation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(oxidationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
            } else if (modification.getName().equals(phosphorylation.getName())) {
                Assert.assertEquals(phosphorylation.getName(), modification.getName());
                Assert.assertEquals(phosphorylation.getMass(), peptideHasModification.getModification().getMonoIsotopicMassShift(), 0.001);
                Assert.assertEquals(phosphorylationMatch.getModificationSite() - 1, (int) peptideHasModification.getLocation());
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
        ModificationMatch oxidationMatch = new ModificationMatch("methionine oxidation with neutral loss of 64 Da", true, 7);
        modificationMatches.add(oxidationMatch);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide targetPeptide = new Peptide();

        utilitiesModificationMapper.map(modificationMatches, targetPeptide);

        //check modification mapping
        Assert.assertFalse(targetPeptide.getPeptideHasModifications().isEmpty());
        Assert.assertEquals(1, targetPeptide.getPeptideHasModifications().size());
        //the modifications are not present in the db, so the IDs should be null
        for (PeptideHasModification peptideHasModification : targetPeptide.getPeptideHasModifications()) {
            //check for null values
            Assert.assertNotNull(peptideHasModification.getModification());
            Assert.assertNotNull(peptideHasModification.getLocation());
            Assert.assertNotNull(peptideHasModification.getPeptide());

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

        utilitiesModificationMapper.map(modificationMatches, targetPeptide);
    }
}
