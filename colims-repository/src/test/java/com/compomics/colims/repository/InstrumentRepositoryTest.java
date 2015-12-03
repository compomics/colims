package com.compomics.colims.repository;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InstrumentRepositoryTest {

    @Autowired
    private InstrumentRepository instrumentRepository;
    private Instrument instrument;

    @Before
    public void loadInstrument() {
        instrument = instrumentRepository.findAll().get(0);
    }

    @Test
    public void testCountInstrumentByName() {
        Long count = instrumentRepository.countByName("instrument_1");

        Assert.assertEquals(1L, count.longValue());
    }

    @Test
    public void testFindAllOrderedByName() {
        List<Instrument> instruments = instrumentRepository.findAllOrderedByName();

        Assert.assertNotNull(instruments);
        Assert.assertTrue(!instruments.isEmpty());

        //check if the list is sorted by instrument name
        boolean sorted = true;
        for (int i = 1; i < instruments.size(); i++) {
            if (instruments.get(i - 1).getName().compareTo(instruments.get(i).getName()) > 0) {
                sorted = false;
            }
        }
        Assert.assertTrue(sorted);
    }

    @Test
    public void testInstrumentCvTerms() {
        //type cv term
        Assert.assertNotNull(instrument.getType());
        Assert.assertEquals(CvParamType.TYPE, instrument.getType().getCvParamType());

        //source cv term
        Assert.assertNotNull(instrument.getSource());
        Assert.assertEquals(CvParamType.SOURCE, instrument.getSource().getCvParamType());

        //detector cv term
        Assert.assertNotNull(instrument.getDetector());
        Assert.assertEquals(CvParamType.DETECTOR, instrument.getDetector().getCvParamType());

        //analyzer cv terms
        Assert.assertEquals(2, instrument.getAnalyzers().size());
        AuditableTypedCvParam analyzer = instrument.getAnalyzers().get(0);
        Assert.assertEquals(CvParamType.ANALYZER, analyzer.getCvParamType());
        analyzer = instrument.getAnalyzers().get(1);
        Assert.assertEquals(CvParamType.ANALYZER, analyzer.getCvParamType());
    }

}
