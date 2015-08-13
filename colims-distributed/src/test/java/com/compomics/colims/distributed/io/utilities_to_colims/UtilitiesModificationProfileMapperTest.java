package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;
import com.compomics.util.pride.PtmToPrideMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesModificationProfileMapperTest {

    @Autowired
    private PtmCvTermMapper ptmCvTermMapper;
    @Autowired
    private OlsService olsService;
    @Autowired
    private UtilitiesModificationProfileMapper utilitiesModificationProfileMapper;
    private ModificationProfile modificationProfile;

    /**
     * Load the search parameters with the modifications.
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    @Before
    public void loadSearchParameters() throws IOException, XmlPullParserException {
        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
        PTMFactory.getInstance().clearFactory();
        PTMFactory.getInstance().importModifications(utilitiesMods.getFile(), false);

        PTM ptm1 = PTMFactory.getInstance().getPTM("carbamidomethyl c");
        PTM ptm2 = PTMFactory.getInstance().getPTM("oxidation of m");
        PTM ptm3 = PTMFactory.getInstance().getPTM("phosphorylation of s");
        PTM ptm4 = PTMFactory.getInstance().getPTM("phosphorylation of t");
        PTM ptm5 = PTMFactory.getInstance().getPTM("phosphorylation of y");
        PTM ptm6 = PTMFactory.getInstance().getPTM("acetylation of protein n-term");
        PTM ptm7 = PTMFactory.getInstance().getPTM("pyro-cmc");
        PTM ptm8 = PTMFactory.getInstance().getPTM("pyro-glu from n-term e");
        PTM ptm9 = PTMFactory.getInstance().getPTM("pyro-glu from n-term q");

        modificationProfile = new ModificationProfile();
        modificationProfile.addFixedModification(ptm1);

        modificationProfile.addVariableModification(ptm2);
        modificationProfile.addVariableModification(ptm3);
        modificationProfile.addVariableModification(ptm4);
        modificationProfile.addVariableModification(ptm5);
        modificationProfile.addVariableModification(ptm6);
        modificationProfile.addVariableModification(ptm7);
        modificationProfile.addVariableModification(ptm8);
        modificationProfile.addVariableModification(ptm9);

        SearchParameters utilitiesSearchParameters = new SearchParameters();

        try {
            //reset PtmToPrideMap for consistent testing
            ptmCvTermMapper.setPtmToPrideMap(new PtmToPrideMap());
            //update mapper with the SearchParameters
            ptmCvTermMapper.updatePtmToPrideMap(utilitiesSearchParameters);
        } catch (FileNotFoundException | ClassNotFoundException ex) {
            Assert.fail();
        }
    }

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache(){
        olsService.getModificationsCache().clear();
    }

    /**
     * Test the modification profile mapping for a set of known (to PeptideShaker) PTMs. 2 Search modifications are
     * already stored in the database, so their ID's shouldn't be null.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile1() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        utilitiesModificationProfileMapper.map(modificationProfile, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(9, searchParameters.getSearchParametersHasModifications().size());
        for (SearchParametersHasModification searchParametersHasModification : searchParameters.getSearchParametersHasModifications()) {
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            //check for the 2 search modifications in the db
            if (searchParametersHasModification.getSearchModification().getAccession().equals("MOD:00425") || searchParametersHasModification.getSearchModification().getAccession().equals("MOD:00696")) {
                Assert.assertNotNull(searchParametersHasModification.getSearchModification().getId());
            } else {
                Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
            }
        }

    }

    /**
     * Test the modification profile mapping for an unknown (to PeptideShaker) PTM but found through the OLS.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile2() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern("AKL");
        PTM unknownPtm = new PTM(0, "L-proline removal", "proline", 52.1, aminoAcidPattern);
        ModificationProfile modificationProfile_2 = new ModificationProfile();
        modificationProfile_2.addVariableModification(unknownPtm);

        utilitiesModificationProfileMapper.map(modificationProfile_2, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(1, searchParameters.getSearchParametersHasModifications().size());
        for (SearchParametersHasModification searchParametersHasModification : searchParameters.getSearchParametersHasModifications()) {
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
        }

    }

    /**
     * Test the modification profile mapping for an unknown (to PeptideShaker) PTM and not found through the OLS.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile3() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern("AKL");
        PTM unknownPtm = new PTM(0, "nonexisting", "non", 52.1, aminoAcidPattern);
        ModificationProfile modificationProfile_3 = new ModificationProfile();
        modificationProfile_3.addVariableModification(unknownPtm);

        utilitiesModificationProfileMapper.map(modificationProfile_3, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(1, searchParameters.getSearchParametersHasModifications().size());
        for (SearchParametersHasModification searchParametersHasModification : searchParameters.getSearchParametersHasModifications()) {
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
            //check the mass
            Assert.assertEquals(unknownPtm.getMass(), searchParametersHasModification.getSearchModification().getMonoIsotopicMassShift(), 0.01);
            Assert.assertEquals(unknownPtm.getMass(), searchParametersHasModification.getSearchModification().getAverageMassShift(), 0.01);
        }
    }

}
