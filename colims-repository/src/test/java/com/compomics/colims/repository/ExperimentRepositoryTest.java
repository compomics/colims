package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import org.junit.Assert;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ExperimentRepositoryTest {

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByProjectIdAndTitle() {
        //try to find an existing experiment
        Experiment foundExperiment = experimentRepository.findByProjectIdAndTitle(1L, "Experiment 1 title");
        
        Assert.assertNotNull(foundExperiment);
        Assert.assertEquals("Experiment 1 title", foundExperiment.getTitle());
        Assert.assertEquals(Long.parseLong("1"), foundExperiment.getProject().getId().longValue());
        
        //try to find an non existing experiment whithin an existing project
        foundExperiment = experimentRepository.findByProjectIdAndTitle(1L, "Unknown experiment 1 title");
        
        Assert.assertNull(foundExperiment);
        
        //try to find an non existing experiment whithin a non existing project
        foundExperiment = experimentRepository.findByProjectIdAndTitle(44L, "Unknown experiment 1 title");
        
        Assert.assertNull(foundExperiment);
    }
        
}
