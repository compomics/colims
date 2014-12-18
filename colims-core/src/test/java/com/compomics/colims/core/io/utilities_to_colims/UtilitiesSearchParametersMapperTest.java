package com.compomics.colims.core.io.utilities_to_colims;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.massspectrometry.Charge;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesSearchParametersMapperTest {

    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;

    /**
     * Test the mapping of Utilities SearchParameters to Colims SearchParameters.
     */
    @Test
    public void testMapSearchParameters() {
        //create SearchParameters
        com.compomics.util.experiment.identification.SearchParameters utilitiesSearchParameters = new com.compomics.util.experiment.identification.SearchParameters();
        Enzyme enzyme = new Enzyme(1, "trypsin", "A", "A", "A", "A");
        utilitiesSearchParameters.setEnzyme(enzyme);

        utilitiesSearchParameters.setnMissedCleavages(2);

        utilitiesSearchParameters.setPrecursorAccuracyType(com.compomics.util.experiment.identification.SearchParameters.MassAccuracyType.DA);
        utilitiesSearchParameters.setPrecursorAccuracy(0.5);
        Charge charge = new Charge(Charge.PLUS, 1);
        utilitiesSearchParameters.setMinChargeSearched(charge);
        utilitiesSearchParameters.setMaxChargeSearched(charge);

        utilitiesSearchParameters.setFragmentAccuracyType(com.compomics.util.experiment.identification.SearchParameters.MassAccuracyType.DA);
        utilitiesSearchParameters.setFragmentIonAccuracy(0.5);

        utilitiesSearchParameters.setIonSearched1("a");
        utilitiesSearchParameters.setIonSearched2("b");

        SearchParameters searchParameters = new SearchParameters();

        utilitiesSearchParametersMapper.map(utilitiesSearchParameters, searchParameters);

        Assert.assertNotNull(searchParameters.getSearchType());
        Assert.assertTrue("MS:1001083".equals(searchParameters.getSearchType().getAccession()));
        Assert.assertEquals(2L, searchParameters.getSearchType().getId().longValue());

        Assert.assertNotNull(searchParameters.getEnzyme());
        Assert.assertTrue(enzyme.getName().equalsIgnoreCase(searchParameters.getEnzyme().getName()));
        Assert.assertEquals(1L, searchParameters.getEnzyme().getId().longValue());

        Assert.assertNotNull(searchParameters.getNumberOfMissedCleavages());
        Assert.assertEquals(Integer.valueOf(2), searchParameters.getNumberOfMissedCleavages());

        Assert.assertNotNull(searchParameters.getPrecMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameters.getPrecMassToleranceUnit());
        Assert.assertNotNull(searchParameters.getPrecMassTolerance());
        Assert.assertEquals(0.5, searchParameters.getPrecMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameters.getFragMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameters.getFragMassToleranceUnit());
        Assert.assertNotNull(searchParameters.getFragMassTolerance());
        Assert.assertEquals(0.5, searchParameters.getFragMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameters.getLowerCharge());
        Assert.assertEquals(Integer.valueOf(charge.value), searchParameters.getLowerCharge());
        Assert.assertNotNull(searchParameters.getUpperCharge());
        Assert.assertEquals(Integer.valueOf(charge.value), searchParameters.getUpperCharge());

        Assert.assertNotNull(searchParameters.getFirstSearchedIonType());
        Assert.assertEquals(Integer.valueOf(PeptideFragmentIon.A_ION), searchParameters.getFirstSearchedIonType());
        Assert.assertNotNull(searchParameters.getSecondSearchedIonType());
        Assert.assertEquals(Integer.valueOf(PeptideFragmentIon.B_ION), searchParameters.getSecondSearchedIonType());
    }
}
