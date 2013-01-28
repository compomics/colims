package com.compomics.colims.repository;

import com.compomics.colims.model.Instrument;
import java.util.Date;
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
public class InstrumentRepositoryTest {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Test
    public void testFindInstrumentByName() {
        Instrument foundInstrument = instrumentRepository.findByName("instrument_1");

        Assert.assertNotNull(foundInstrument);
    }

    @Test
    public void testCustomUpdateEventListener() {
        
        Instrument instrument = instrumentRepository.findAll().get(0);

        //get creation and modification date
        Date creationDate = instrument.getCreationdate();
        Date modificationDate = instrument.getModificationdate();

        //update the instrument and save it
        instrument.setName("instrument_1_new_name");
        instrumentRepository.update(instrument);

        //check if the creation date is the same and the modification has been updated
        Assert.assertEquals(creationDate, instrument.getCreationdate());
        Assert.assertFalse(modificationDate.equals(instrument.getModificationdate()));        
    }
}
