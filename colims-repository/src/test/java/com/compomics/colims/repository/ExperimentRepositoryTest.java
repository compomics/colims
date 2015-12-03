package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;
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
public class ExperimentRepositoryTest {

    @Autowired
    private ExperimentRepository experimentRepository;

    @Test
    public void testFindByProjectIdAndTitle() {
        //try to find an existing experiment
        Long count = experimentRepository.countByProjectIdAndTitle(1L, "Experiment 1 title");

        Assert.assertFalse(count.longValue() == 0);

        //try to find an non existing experiment within an existing project
        count = experimentRepository.countByProjectIdAndTitle(1L, "Unknown experiment 1 title");

        Assert.assertTrue(count.longValue() == 0);

        //try to find an non existing experiment within a non existing project
        count = experimentRepository.countByProjectIdAndTitle(44L, "Unknown experiment 1 title");

        Assert.assertTrue(count.longValue() == 0);
    }

}
