package com.compomics.colims.repository;


import com.compomics.colims.model.CvTerm;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.ProtocolCvTerm;
import com.compomics.colims.model.enums.CvTermProperty;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CvTermRepositoryTest {

    @Autowired
    private CvTermRepository cvTermRepository;        
        
    @Test
    public void testFindByAccession() {
        //look for unknown analyzer
        CvTerm cvTerm = cvTermRepository.findByAccession("unknown analyzer", CvTermProperty.ANALYZER);
        Assert.assertNull(cvTerm);        
        
        //look for known analyzer
        cvTerm = cvTermRepository.findByAccession("instr_cv_acc_4", CvTermProperty.ANALYZER);
        Assert.assertNotNull(cvTerm);        
        //check if the CvTerm is an InstrumentCvTerm
        Assert.assertTrue(cvTerm instanceof InstrumentCvTerm);
        
        //look for known enzyme
        cvTerm = cvTermRepository.findByAccession("protocol_cv_acc_2", CvTermProperty.ENZYME);
        Assert.assertNotNull(cvTerm);        
        //check if the CvTerm is a ProtocolCvTerm
        Assert.assertTrue(cvTerm instanceof ProtocolCvTerm);
    }  
    
    @Test
    public void testFindByCvTermProperty() {        
        List<CvTerm> cvTerms = cvTermRepository.findByCvTermProperty(CvTermProperty.ANALYZER);
        Assert.assertNotNull(cvTerms);  
        Assert.assertEquals(2, cvTerms.size());
        
        cvTerms = cvTermRepository.findByCvTermProperty(CvTermProperty.ENZYME);
        Assert.assertNotNull(cvTerms);  
        Assert.assertEquals(2, cvTerms.size());
    }
      
}
