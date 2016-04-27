package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.OlsService;
import com.compomics.util.experiment.biology.*;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesPtmSettingsMapperTest {

    @Autowired
    private OlsService newOlsService;
    @Autowired
    private UtilitiesPtmSettingsMapper utilitiesPtmSettingsMapper;
    private PtmSettings ptmSettings;

    /**
     * Load the search parameters with the modifications.
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    @Before
    public void loadSearchParameters() throws IOException, XmlPullParserException {
        PTMFactory ptmFactory = PTMFactory.getInstance();

        PTM ptm1 = ptmFactory.getPTM("Carbamidomethylation of C");
        PTM ptm2 = ptmFactory.getPTM("Oxidation of M");
        PTM ptm3 = ptmFactory.getPTM("Phosphorylation of S");
        PTM ptm4 = ptmFactory.getPTM("Phosphorylation of T");
        PTM ptm5 = ptmFactory.getPTM("Phosphorylation of Y");
        PTM ptm6 = ptmFactory.getPTM("Acetylation of protein N-term");
        PTM ptm7 = ptmFactory.getPTM("Pyrolidone from carbamidomethylated C");
        PTM ptm8 = ptmFactory.getPTM("Pyrolidone from E");
        PTM ptm9 = ptmFactory.getPTM("Pyrolidone from Q");

        ptmSettings = new PtmSettings();
        ptmSettings.addFixedModification(ptm1);

        ptmSettings.addVariableModification(ptm2);
        ptmSettings.addVariableModification(ptm3);
        ptmSettings.addVariableModification(ptm4);
        ptmSettings.addVariableModification(ptm5);
        ptmSettings.addVariableModification(ptm6);
        ptmSettings.addVariableModification(ptm7);
        ptmSettings.addVariableModification(ptm8);
        ptmSettings.addVariableModification(ptm9);
    }

    /**
     * Clear the modifications cache.
     */
    @After
    public void clearCache() {
        newOlsService.getModificationsCache().clear();
    }

    /**
     * Test the modification profile mapping for a set of known (to
     * PeptideShaker) PTMs. 2 Search modifications are already stored in the
     * database, so their ID's shouldn't be null.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile1() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        utilitiesPtmSettingsMapper.map(ptmSettings, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(9, searchParameters.getSearchParametersHasModifications().size());
        searchParameters.getSearchParametersHasModifications().stream().forEach(searchParametersHasModification -> {
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            //check for the 2 search modifications in the db
            if (searchParametersHasModification.getSearchModification().getAccession().equals("MOD:00425") || searchParametersHasModification.getSearchModification().getAccession().equals("MOD:00696")) {
                Assert.assertNotNull(searchParametersHasModification.getSearchModification().getId());
            } else {
                Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
            }
        });

    }

    /**
     * Test the modification profile mapping for an unknown (to PeptideShaker)
     * PTM but found through the OLS.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile2() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        AtomChain atomChain = new AtomChain(true);
        atomChain.append(new AtomImpl(Atom.C, 0), 8);
        atomChain.append(new AtomImpl(Atom.C, 1), 4);
        atomChain.append(new AtomImpl(Atom.H, 0), 20);
        atomChain.append(new AtomImpl(Atom.N, 0), 1);
        atomChain.append(new AtomImpl(Atom.N, 1), 1);
        atomChain.append(new AtomImpl(Atom.O, 0), 2);
        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern("K");
        PTM unknownPtm = new PTM(PTM.MODAA, "nonexisting", "non", atomChain, null, aminoAcidPattern);
        PtmSettings ptmSettings_2 = new PtmSettings();
        ptmSettings_2.addVariableModification(unknownPtm);

        utilitiesPtmSettingsMapper.map(ptmSettings_2, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(1, searchParameters.getSearchParametersHasModifications().size());
        searchParameters.getSearchParametersHasModifications().stream().forEach(searchParametersHasModification -> {
            Assert.assertEquals("K", searchParametersHasModification.getResidues());
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
        });

    }

    /**
     * Test the modification profile mapping for an unknown (to PeptideShaker)
     * PTM and not found through the OLS.
     *
     * @throws com.compomics.colims.core.io.MappingException
     */
    @Test
    public void testMapModificationProfile3() throws MappingException {
        com.compomics.colims.model.SearchParameters searchParameters = new com.compomics.colims.model.SearchParameters();

        AtomChain atomChain = new AtomChain(true);
        atomChain.append(new AtomImpl(Atom.C, 0), 8);
        atomChain.append(new AtomImpl(Atom.C, 1), 4);
        atomChain.append(new AtomImpl(Atom.H, 0), 20);
        atomChain.append(new AtomImpl(Atom.N, 0), 1);
        atomChain.append(new AtomImpl(Atom.N, 1), 1);
        atomChain.append(new AtomImpl(Atom.O, 0), 2);
        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern("AKL");
        PTM unknownPtm = new PTM(PTM.MODAA, "nonexisting", "non", atomChain, null, aminoAcidPattern);
        PtmSettings ptmSettings_3 = new PtmSettings();
        ptmSettings_3.addVariableModification(unknownPtm);

        utilitiesPtmSettingsMapper.map(ptmSettings_3, searchParameters);

        Assert.assertFalse(searchParameters.getSearchParametersHasModifications().isEmpty());
        Assert.assertEquals(1, searchParameters.getSearchParametersHasModifications().size());
        searchParameters.getSearchParametersHasModifications().stream().forEach(searchParametersHasModification -> {
            Assert.assertEquals("AKL", searchParametersHasModification.getResidues());
            Assert.assertNotNull(searchParametersHasModification.getSearchModification());
            Assert.assertNotNull(searchParametersHasModification.getSearchParameters());
            Assert.assertNotNull(searchParametersHasModification.getModificationType());
            Assert.assertNull(searchParametersHasModification.getSearchModification().getId());
            //check the mass
            Assert.assertNull(searchParametersHasModification.getSearchModification().getMonoIsotopicMassShift());
            Assert.assertNull(searchParametersHasModification.getSearchModification().getAverageMassShift());
        });
    }

}
