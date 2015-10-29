package com.compomics.colims.repository;


import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.cv.TypedCvParam;
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
public class TypedCvParamRepositoryTest {

    @Autowired
    private TypedCvParamRepository cvParamRepository;

    @Test
    public void testFindByAccession() {
        //look for unknown search enzyme
        TypedCvParam cvParam = cvParamRepository.findByAccession("unknown enzyme", CvParamType.SEARCH_PARAM_ENZYME);
        Assert.assertNull(cvParam);

        //look for a known search enzyme
        cvParam = cvParamRepository.findByAccession("MS:1001251", CvParamType.SEARCH_PARAM_ENZYME);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is an SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);
    }

    @Test
    public void testFindByName() {
        //look for unknown analyzer
        TypedCvParam cvParam = cvParamRepository.findByName("unknown enzyme", CvParamType.SEARCH_PARAM_ENZYME, false);
        Assert.assertNull(cvParam);

        //look for known enzyme, equal casing
        cvParam = cvParamRepository.findByName("Trypsin", CvParamType.SEARCH_PARAM_ENZYME, false);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);

        //look for known enzyme, different casing, ignoreCase false
        cvParam = cvParamRepository.findByName("TRYPSIN", CvParamType.SEARCH_PARAM_ENZYME, false);
        Assert.assertNull(cvParam);

        //look for known enzyme, different casing, ignoreCase true
        cvParam = cvParamRepository.findByName("TRYPSIN", CvParamType.SEARCH_PARAM_ENZYME, true);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);
    }

    @Test
    public void testFindByCvParamType() {
        List<TypedCvParam> cvParams = cvParamRepository.findByCvParamType(CvParamType.SEARCH_PARAM_ENZYME);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(1, cvParams.size());

        cvParams = cvParamRepository.findByCvParamType(CvParamType.SEARCH_TYPE);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(1, cvParams.size());
    }

}
