package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesSearchParametersMapperTest {

    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;

    /**
     * Test the mapping of Utilities SearchParameters to Colims SearchParameters.
     */
    @Test
    public void testMapSearchParameters() {
        //create SearchParameters
        com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters = new com.compomics.util.experiment.identification.identification_parameters.SearchParameters();
        Enzyme enzyme = new Enzyme(1, "trypsin", "A", "A", "A", "A");
        utilitiesSearchParameters.setEnzyme(enzyme);

        utilitiesSearchParameters.setnMissedCleavages(2);

        utilitiesSearchParameters.setPrecursorAccuracyType(com.compomics.util.experiment.identification.identification_parameters.SearchParameters.MassAccuracyType.DA);
        utilitiesSearchParameters.setPrecursorAccuracy(0.5);
        Charge charge = new Charge(Charge.PLUS, 1);
        utilitiesSearchParameters.setMinChargeSearched(charge);
        utilitiesSearchParameters.setMaxChargeSearched(charge);

        utilitiesSearchParameters.setFragmentAccuracyType(com.compomics.util.experiment.identification.identification_parameters.SearchParameters.MassAccuracyType.DA);
        utilitiesSearchParameters.setFragmentIonAccuracy(0.5);

        utilitiesSearchParameters.setIonSearched1("a");
        utilitiesSearchParameters.setIonSearched2("b");

        PTM ptm1 = PTMFactory.getInstance().getPTM("Carbamidomethylation of C");
        PTM ptm2 = PTMFactory.getInstance().getPTM("Oxidation of M");
        PTM ptm3 = PTMFactory.getInstance().getPTM("Phosphorylation of S");
        PTM ptm4 = PTMFactory.getInstance().getPTM("Phosphorylation of T");
        PTM ptm5 = PTMFactory.getInstance().getPTM("Phosphorylation of Y");
        PTM ptm6 = PTMFactory.getInstance().getPTM("Acetylation of protein N-term");
        PTM ptm7 = PTMFactory.getInstance().getPTM("Pyrolidone from carbamidomethylated C");
        PTM ptm8 = PTMFactory.getInstance().getPTM("Pyrolidone from E");
        PTM ptm9 = PTMFactory.getInstance().getPTM("Pyrolidone from Q");

        PtmSettings ptmSettings = new PtmSettings();
        ptmSettings.addFixedModification(ptm1);

        ptmSettings.addVariableModification(ptm2);
        ptmSettings.addVariableModification(ptm3);
        ptmSettings.addVariableModification(ptm4);
        ptmSettings.addVariableModification(ptm5);
        ptmSettings.addVariableModification(ptm6);
        ptmSettings.addVariableModification(ptm7);
        ptmSettings.addVariableModification(ptm8);
        ptmSettings.addVariableModification(ptm9);

        utilitiesSearchParameters.setPtmSettings(ptmSettings);

        SearchParameters searchParameters = new SearchParameters();

        utilitiesSearchParametersMapper.map(utilitiesSearchParameters, searchParameters);

        Assert.assertNotNull(searchParameters.getSearchType());
        Assert.assertTrue("MS:1001083".equals(searchParameters.getSearchType().getAccession()));
        Assert.assertEquals(2L, searchParameters.getSearchType().getId().longValue());

        Assert.assertNotNull(searchParameters.getEnzyme());
        Assert.assertTrue(enzyme.getName().equalsIgnoreCase(searchParameters.getEnzyme().getName()));
        Assert.assertEquals(1L, searchParameters.getEnzyme().getId().longValue());

        Assert.assertNotNull(searchParameters.getNumberOfMissedCleavages());
        Assert.assertEquals(2, searchParameters.getNumberOfMissedCleavages().intValue());

        Assert.assertNotNull(searchParameters.getPrecMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameters.getPrecMassToleranceUnit());
        Assert.assertNotNull(searchParameters.getPrecMassTolerance());
        Assert.assertEquals(0.5, searchParameters.getPrecMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameters.getFragMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameters.getFragMassToleranceUnit());
        Assert.assertNotNull(searchParameters.getFragMassTolerance());
        Assert.assertEquals(0.5, searchParameters.getFragMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameters.getLowerCharge());
        Assert.assertEquals(charge.value, searchParameters.getLowerCharge().intValue());
        Assert.assertNotNull(searchParameters.getUpperCharge());
        Assert.assertEquals(charge.value, searchParameters.getUpperCharge().intValue());

        Assert.assertNotNull(searchParameters.getFirstSearchedIonType());
        Assert.assertEquals(PeptideFragmentIon.A_ION, searchParameters.getFirstSearchedIonType().intValue());
        Assert.assertNotNull(searchParameters.getSecondSearchedIonType());
        Assert.assertEquals(PeptideFragmentIon.B_ION, searchParameters.getSecondSearchedIonType().intValue());

        Assert.assertNotNull(searchParameters.getSearchParametersHasModifications());
        Assert.assertEquals(9, searchParameters.getSearchParametersHasModifications().size());
    }
}