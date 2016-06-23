package com.compomics.colims.repository;

import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.ProtocolCvParam;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import org.junit.Assert;
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
public class AuditableTypedCvParamRepositoryTest {

    @Autowired
    private AuditableTypedCvParamRepository cvParamRepository;

    @Test
    public void testFindByAccession() {
        //look for unknown analyzer
        AuditableTypedCvParam cvParam = cvParamRepository.findByAccession("unknown analyzer", CvParamType.ANALYZER);
        Assert.assertNull(cvParam);

        //look for known analyzer
        cvParam = cvParamRepository.findByAccession("MS:1000140", CvParamType.ANALYZER);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is an InstrumentCvParam
        Assert.assertTrue(cvParam instanceof InstrumentCvParam);

        //look for known enzyme
        cvParam = cvParamRepository.findByAccession("protocol_cv_acc_2", CvParamType.ENZYME);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a ProtocolCvParam
        Assert.assertTrue(cvParam instanceof ProtocolCvParam);
    }

    @Test
    public void testFindByName() {
        //look for unknown analyzer
        AuditableTypedCvParam cvParam = cvParamRepository.findByName("unknown analyzer", CvParamType.ANALYZER, false);
        Assert.assertNull(cvParam);

        //look for known analyzer
        cvParam = cvParamRepository.findByName("4700 Proteomics Analyzer", CvParamType.ANALYZER, false);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is an InstrumentCvParam
        Assert.assertTrue(cvParam instanceof InstrumentCvParam);

        //look for known enzyme, equal casing
        cvParam = cvParamRepository.findByName("trypsin", CvParamType.ENZYME, false);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a ProtocolCvParam
        Assert.assertTrue(cvParam instanceof ProtocolCvParam);

        //look for known enzyme, different casing, ignoreCase false
        cvParam = cvParamRepository.findByName("Trypsin", CvParamType.ENZYME, false);
        Assert.assertNull(cvParam);

        //look for known enzyme, different casing, ignoreCase true
        cvParam = cvParamRepository.findByName("Trypsin", CvParamType.ENZYME, true);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a ProtocolCvParam
        Assert.assertTrue(cvParam instanceof ProtocolCvParam);
    }

    @Test
    public void testFindByCvParamType() {
        List<AuditableTypedCvParam> cvParams = cvParamRepository.findByCvParamType(CvParamType.ANALYZER);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(2, cvParams.size());

        cvParams = cvParamRepository.findByCvParamType(CvParamType.ENZYME);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(2, cvParams.size());
    }

    @Test
    public void testGetMappedSuperclassReference() {
        AuditableTypedCvParam cvParam = cvParamRepository.getMappedSuperclassReference(InstrumentCvParam.class, 1L);

        Assert.assertNotNull(cvParam);
        //check the subclass
        Assert.assertTrue(cvParam instanceof InstrumentCvParam);
    }

}
