package com.compomics.colims.repository;


import com.compomics.colims.model.cv.AuditableTypedCvParam;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.ProtocolCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CvParamRepositoryTest {

    @Autowired
    private AuditableTypedCvParamRepository cvParamRepository;

    @Test
    public void testFindByAccession() {
        //look for unknown analyzer
        AuditableTypedCvParam cvTerm = cvParamRepository.findByAccession("unknown analyzer", CvParamType.ANALYZER);
        Assert.assertNull(cvTerm);

        //look for known analyzer
        cvTerm = cvParamRepository.findByAccession("MS:1000140", CvParamType.ANALYZER);
        Assert.assertNotNull(cvTerm);
        //check if the CvTerm is an InstrumentCvTerm
        Assert.assertTrue(cvTerm instanceof InstrumentCvParam);

        //look for known enzyme
        cvTerm = cvParamRepository.findByAccession("protocol_cv_acc_2", CvParamType.ENZYME);
        Assert.assertNotNull(cvTerm);
        //check if the CvTerm is a ProtocolCvTerm
        Assert.assertTrue(cvTerm instanceof ProtocolCvParam);
    }

    @Test
    public void testFindBycvTermType() {
        List<AuditableTypedCvParam> cvTerms = cvParamRepository.findByCvParamType(CvParamType.ANALYZER);
        Assert.assertNotNull(cvTerms);
        Assert.assertEquals(2, cvTerms.size());

        cvTerms = cvParamRepository.findByCvParamType(CvParamType.ENZYME);
        Assert.assertNotNull(cvTerms);
        Assert.assertEquals(2, cvTerms.size());
    }

}
