package com.compomics.colims.repository;

import com.compomics.colims.model.CvTerm;
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
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.enums.CvTermProperty;
import org.junit.Before;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
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

        //instrument type term
        InstrumentType instrumentType = instrument.getInstrumentType();
        Assert.assertNotNull(instrumentType);
        Assert.assertEquals(instrumentType.getName(), "instr_type_1_name");
        Assert.assertEquals(instrumentType.getDescription(), "instrument type 1 description");
    }

    @Test
    public void testInstrumentCvTerms() {
        //source cv term
        Assert.assertNotNull(instrument.getSource());
        Assert.assertEquals(CvTermProperty.SOURCE, instrument.getSource().getCvTermProperty());

        //detector cv term
        Assert.assertNotNull(instrument.getDetector());
        Assert.assertEquals(CvTermProperty.DETECTOR, instrument.getDetector().getCvTermProperty());

        //analyzer cv terms
        Assert.assertEquals(2, instrument.getAnalyzers().size());
        CvTerm analyzer = instrument.getAnalyzers().get(0);
        Assert.assertEquals(CvTermProperty.ANALYZER, analyzer.getCvTermProperty());
        analyzer = instrument.getAnalyzers().get(1);
        Assert.assertEquals(CvTermProperty.ANALYZER, analyzer.getCvTermProperty());
    }

    @Test
    public void testCustomUpdateEventListener() {
        //get creation and modification date
        Date creationDate = instrument.getCreationdate();
        Date modificationDate = instrument.getModificationdate();

        //update the instrument and save it
        instrument.setName("instrument_1_new_name");
        instrumentRepository.update(instrument);

        //check if the creation date is the same and the modification has been updated
        Assert.assertEquals(creationDate, instrument.getCreationdate());
        //@ToDo check why the date doesn't change, possible because the transaction has not been flushed yet.
        Assert.assertFalse(modificationDate.equals(instrument.getModificationdate()));        
    }
}
