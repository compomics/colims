package com.compomics.colims.repository;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class InstrumentCvTermRepositoryTest {

    @Autowired
    private InstrumentCvTermRepository instrumentCvTermRepository;        
        
    @Test
    public void testFindByAccession() {
        //look for unknown analyzer
        InstrumentCvTerm analyzer = instrumentCvTermRepository.findByAccession("unknown analyzer", InstrumentCvProperty.ANALYZER);
        Assert.assertNull(analyzer);
        
        //look for known analyzer
        analyzer = instrumentCvTermRepository.findByAccession("instr_cv_acc_4", InstrumentCvProperty.ANALYZER);
        Assert.assertNotNull(analyzer);        
    }  
    
    @Test
    public void testFindByInstrumentCvProperty() {        
        List<InstrumentCvTerm> analyzers = instrumentCvTermRepository.findByInstrumentCvProperty(InstrumentCvProperty.ANALYZER);
        Assert.assertNotNull(analyzers);  
        Assert.assertEquals(2, analyzers.size());
    }
      
}
