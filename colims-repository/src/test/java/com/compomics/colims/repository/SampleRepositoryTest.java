package com.compomics.colims.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class SampleRepositoryTest {

    @Autowired
    private SampleRepository sampleRepository;

    @Test
    public void testGetParentIds() {
        //look for existing sample
        Object[] parentIds = sampleRepository.getParentIds(1L);

        Assert.assertEquals(1L, parentIds[0]);
        Assert.assertEquals(1L, parentIds[1]);

        //look for non-existing sample
        parentIds = sampleRepository.getParentIds(10L);
        Assert.assertNull(parentIds);
    }

}
