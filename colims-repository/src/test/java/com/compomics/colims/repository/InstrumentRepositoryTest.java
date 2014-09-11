package com.compomics.colims.repository;

import com.compomics.colims.model.TypedCvTerm;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.enums.CvTermType;
import java.util.List;
import org.junit.Before;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
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
    public void testFindInstrumentByName() {
        Instrument foundInstrument = instrumentRepository.findByName("instrument_1");

        Assert.assertNotNull(foundInstrument);
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
        Assert.assertEquals(CvTermType.TYPE, instrument.getType().getcvTermType());

        //source cv term
        Assert.assertNotNull(instrument.getSource());
        Assert.assertEquals(CvTermType.SOURCE, instrument.getSource().getcvTermType());

        //detector cv term
        Assert.assertNotNull(instrument.getDetector());
        Assert.assertEquals(CvTermType.DETECTOR, instrument.getDetector().getcvTermType());

        //analyzer cv terms
        Assert.assertEquals(2, instrument.getAnalyzers().size());
        TypedCvTerm analyzer = instrument.getAnalyzers().get(0);
        Assert.assertEquals(CvTermType.ANALYZER, analyzer.getcvTermType());
        analyzer = instrument.getAnalyzers().get(1);
        Assert.assertEquals(CvTermType.ANALYZER, analyzer.getcvTermType());
    }

    @Test
    public void testCustomUpdateEventListener() {
        //get creation and modification date
        Date creationDate = instrument.getCreationDate();
        Date modificationDate = instrument.getModificationDate();

        //update the instrument and save it
        instrument.setName("instrument_1_new_name");
        instrumentRepository.update(instrument);

        //check if the creation date is the same and the modification has been updated
        Assert.assertEquals(creationDate, instrument.getCreationDate());
        //@ToDo check why the date doesn't change, possible because the transaction has not been flushed yet.
        Assert.assertFalse(modificationDate.equals(instrument.getModificationDate()));
    }
}
