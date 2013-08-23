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

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class AnalyzerRepositoryTest {

    @Autowired
    private AnalyzerRepository analyzerRepository;        
        
    @Test
    public void testFindByAccession() {
        //look for unknown analyzer
        InstrumentCvTerm analyzer = analyzerRepository.findByAccession("unknown analyzer");
        Assert.assertNull(analyzer);
        
        //look for known analyzer
        analyzer = analyzerRepository.findByAccession("instr_cv_acc_4");
        Assert.assertNotNull(analyzer);        
    }        
      
}
