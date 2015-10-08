package com.compomics.colims.repository;

import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.enums.MassAccuracyType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SearchParametersRepositoryTest {

    @Autowired
    private SearchParametersRepository searchParametersRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Test
    public void testFindByExample() {
        SearchParameters searchParameters = new SearchParameters();

        SearchCvParam enzyme = new SearchCvParam(CvParamType.ENZYME, "PSI-MS", "PSI-MS", "MS:1001251", "Trypsin");
        searchParameters.setEnzyme(enzyme);
        searchParameters.setNumberOfMissedCleavages(2);
        searchParameters.setThreshold(50.0);
        searchParameters.setLowerCharge(2);
        searchParameters.setUpperCharge(4);
        searchParameters.setPrecMassToleranceUnit(MassAccuracyType.PPM);
        searchParameters.setPrecMassTolerance(10.0);
        searchParameters.setFragMassToleranceUnit(MassAccuracyType.DA);
        searchParameters.setFragMassTolerance(0.02);
        searchParameters.setFirstSearchedIonType(1);
        searchParameters.setSecondSearchedIonType(4);

        List<SearchParameters> searchSettingses = searchParametersRepository.findByExample(searchParameters);

        Assert.assertNotNull(searchSettingses);
        Assert.assertFalse(searchSettingses.isEmpty());
        Assert.assertEquals(1, searchSettingses.size());
    }

    @Test
    public void testGetSearchParametersIdsForRunTest() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<Long> searchParametersIds = searchParametersRepository.getConstraintLessSearchParameterIdsForRuns(runIds);

        Assert.assertEquals(1, searchParametersIds.size());
    }

}
