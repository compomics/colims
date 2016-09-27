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
    private TypedCvParamRepository typedCvParamRepository;

    @Test
    public void testFindByAccession() {
        //look for unknown search type
        TypedCvParam cvParam = typedCvParamRepository.findByAccession("unknown search type", CvParamType.SEARCH_TYPE);
        Assert.assertNull(cvParam);

        //look for a known search type
        cvParam = typedCvParamRepository.findByAccession("MS:1001083", CvParamType.SEARCH_TYPE);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is an SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);
    }

    @Test
    public void testFindByName() {
        //look for unknown analyzer
        TypedCvParam cvParam = typedCvParamRepository.findByName("unknown search type", CvParamType.SEARCH_TYPE, false);
        Assert.assertNull(cvParam);

        //look for known search type, equal casing
        cvParam = typedCvParamRepository.findByName("ms-ms search", CvParamType.SEARCH_TYPE, false);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);

        //look for known search type, different casing, ignoreCase false
        cvParam = typedCvParamRepository.findByName("MS-MS SEARCH", CvParamType.SEARCH_TYPE, false);
        Assert.assertNull(cvParam);

        //look for known search type, different casing, ignoreCase true
        cvParam = typedCvParamRepository.findByName("MS-MS SEARCH", CvParamType.SEARCH_TYPE, true);
        Assert.assertNotNull(cvParam);
        //check if the CvParam is a SearchCvParam
        Assert.assertTrue(cvParam instanceof SearchCvParam);
    }

    @Test
    public void testFindByCvParamType() {
        List<TypedCvParam> cvParams = typedCvParamRepository.findByCvParamType(CvParamType.SEARCH_TYPE);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(1, cvParams.size());

        cvParams = typedCvParamRepository.findByCvParamType(CvParamType.SEARCH_TYPE);
        Assert.assertNotNull(cvParams);
        Assert.assertEquals(1, cvParams.size());
    }

}
