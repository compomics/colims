package com.compomics.colims.repository;

import com.compomics.colims.model.SearchParamCvParam;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.enums.MassAccuracyType;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SearchParameterSettingsRepositoryTest {

    @Autowired
    private SearchParameterSettingsRepository searchParameterSettingsRepository;

    @Test
    public void testFindByExample() {
        SearchParameterSettings searchParameterSettings = new SearchParameterSettings();

        SearchParamCvParam enzyme = new SearchParamCvParam(CvParamType.ENZYME, "PSI-MS", "PSI-MS", "MS:1001251", "Trypsin");
        searchParameterSettings.setEnzyme(enzyme);
        searchParameterSettings.setNumberOfMissedCleavages(2);
        searchParameterSettings.setLowerCharge(2);
        searchParameterSettings.setUpperCharge(4);
        searchParameterSettings.setPrecMassToleranceUnit(MassAccuracyType.PPM);
        searchParameterSettings.setPrecMassTolerance(10.0);
        searchParameterSettings.setFragMassToleranceUnit(MassAccuracyType.DA);
        searchParameterSettings.setFragMassTolerance(0.02);
        searchParameterSettings.setFirstSearchedIonType(1);
        searchParameterSettings.setSecondSearchedIonType(4);

        List<SearchParameterSettings> searchParameterSettingses = searchParameterSettingsRepository.findByExample(searchParameterSettings);

        Assert.assertNotNull(searchParameterSettingses);
        Assert.assertFalse(searchParameterSettingses.isEmpty());
        Assert.assertEquals(1, searchParameterSettingses.size());
    }

}
