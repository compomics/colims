
package com.compomics.colims.core.service;

import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.enums.CvParamType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
public class CvParamServiceTest {

    @Autowired
    private CvParamService cvParamService;
    @Autowired
    private AuditableTypedCvParamService auditableTypedCvParamService;

    @Test
    public void testFindByCvParamByClass() {
        List<CvParam> searchCvParams = cvParamService.findByCvParamByClass(SearchCvParam.class);

        Assert.assertNotNull(searchCvParams);
        Assert.assertEquals(2, searchCvParams.size());
    }

    @Test
    public void testFindByAccession() {
        //look for an exisiting CV param
        CvParam searchCvParam = cvParamService.findByAccession(SearchCvParam.class, "MS:1001083");

        Assert.assertNotNull(searchCvParam);
        Assert.assertTrue(searchCvParam instanceof SearchCvParam);

        //look for an non exisiting CV param
        searchCvParam = cvParamService.findByAccession(SearchCvParam.class, "MS:1001083");

        Assert.assertNotNull(searchCvParam);
    }

    @Test
    public void testFindByCvParamByType() {
        List<InstrumentCvParam> detectors = auditableTypedCvParamService.findByCvParamByType(InstrumentCvParam.class, CvParamType.DETECTOR);

        Assert.assertNotNull(detectors);
        Assert.assertEquals(2, detectors.size());
    }

}
