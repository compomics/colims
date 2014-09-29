package com.compomics.colims.core.io.utilities_to_colims;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesSearchParametersMapperTest {

    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;

    /**
     * Test the mapping of Utilities SearchParameters to colims
     * SearchParameterSettings.
     *
     */
    @Test
    public void testMapSearchParameters() {
        //create SearchParameters
        SearchParameters searchParameters = new SearchParameters();
        Enzyme enzyme = new Enzyme(1, "testEnzyme", "A", "A", "A", "A");
        searchParameters.setEnzyme(enzyme);

        searchParameters.setnMissedCleavages(2);

        searchParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
        searchParameters.setPrecursorAccuracy(0.5);
        Charge charge = new Charge(Charge.PLUS, 1);
        searchParameters.setMinChargeSearched(charge);
        searchParameters.setMaxChargeSearched(charge);

        searchParameters.setFragmentAccuracyType(SearchParameters.MassAccuracyType.DA);
        searchParameters.setFragmentIonAccuracy(0.5);

        searchParameters.setIonSearched1("a");
        searchParameters.setIonSearched2("b");

        SearchParameterSettings searchParameterSettings = new SearchParameterSettings();

        utilitiesSearchParametersMapper.map(searchParameters, searchParameterSettings);

        Assert.assertNotNull(searchParameterSettings.getEnzyme());
        Assert.assertTrue(enzyme.getName().equalsIgnoreCase(searchParameterSettings.getEnzyme().getName()));

        Assert.assertNotNull(searchParameterSettings.getNumberOfMissedCleavages());
        Assert.assertEquals(Integer.valueOf(2), searchParameterSettings.getNumberOfMissedCleavages());

        Assert.assertNotNull(searchParameterSettings.getFragMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameterSettings.getFragMassToleranceUnit());
        Assert.assertNotNull(searchParameterSettings.getFragMassTolerance());
        Assert.assertEquals(0.5, searchParameterSettings.getFragMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameterSettings.getPrecMassToleranceUnit());
        Assert.assertEquals(MassAccuracyType.DA, searchParameterSettings.getPrecMassToleranceUnit());
        Assert.assertNotNull(searchParameterSettings.getPrecMassTolerance());
        Assert.assertEquals(0.5, searchParameterSettings.getPrecMassTolerance(), 0.01);

        Assert.assertNotNull(searchParameterSettings.getLowerCharge());
        Assert.assertEquals(Integer.valueOf(charge.value), searchParameterSettings.getLowerCharge());
        Assert.assertNotNull(searchParameterSettings.getUpperCharge());
        Assert.assertEquals(Integer.valueOf(charge.value), searchParameterSettings.getUpperCharge());

        Assert.assertNotNull(searchParameterSettings.getFirstSearchedIonType());
        Assert.assertEquals(Integer.valueOf(PeptideFragmentIon.A_ION), searchParameterSettings.getFirstSearchedIonType());
        Assert.assertNotNull(searchParameterSettings.getSecondSearchedIonType());
        Assert.assertEquals(Integer.valueOf(PeptideFragmentIon.B_ION), searchParameterSettings.getSecondSearchedIonType());
    }
}
