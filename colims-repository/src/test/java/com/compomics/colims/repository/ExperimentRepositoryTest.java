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
    public void testCountByProjectIdAndTitle() {
        //try to find an existing experiment
        //should return 0 because the experiment itself is excluded
        Experiment experiment = experimentRepository.findById(1L);
        Long count = experimentRepository.countByProjectIdAndTitle(1L, experiment);

        Assert.assertTrue(count == 0);

        //try to find an non existing experiment within an existing project
        experiment = new Experiment();
        experiment.setTitle("Unknown experiment 1 title");
        count = experimentRepository.countByProjectIdAndTitle(1L, experiment);

        Assert.assertTrue(count == 0);

        //try to find an non existing experiment within a non existing project
        count = experimentRepository.countByProjectIdAndTitle(44L, experiment);

        Assert.assertTrue(count == 0);
    }

}
