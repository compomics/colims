package com.compomics.colims.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class ProteinRepositoryTest {

    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private ProteinGroupRepository proteinGroupRepository;

    /**
     * Test with protein group IDs.
     */
    @Test
    public void testGetConstraintLessProteinIdsForRunsTest() {
        List<Long> proteinGroupIds = new ArrayList<>();
        proteinGroupIds.add(1L);

        List<Long> proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);

        //3 ProteinGroup entries, 1 only linked to run 1, so one should return 1.
        Assert.assertEquals(1, proteinIds.size());

        proteinGroupIds.add(3L);
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        //Only 1 proteins is only linked to protein group 1 and 3.
        Assert.assertEquals(1, proteinIds.size());

        proteinGroupIds.remove(1);
        proteinGroupIds.add(2L);
        //Only 3 proteins are only linked to protein group 1 and 2.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(3, proteinIds.size());

        proteinGroupIds.clear();
        proteinGroupIds.add(3L);
        //Protein group 3 does not have proteins linked uniquely to it.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(0, proteinIds.size());

        proteinGroupIds.add(1L);
        proteinGroupIds.add(2L);
        //All proteins are linked to the 3 protein groups.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(4, proteinIds.size());
    }

    /**
     * Test with analytical run IDs.
     */
    @Test
    public void testGetConstraintLessProteinIdsForRunsTest2() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<Long> proteinGroupIds = proteinGroupRepository.getConstraintLessProteinGroupIdsForRuns(runIds);
        List<Long> proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);

        //3 proteins linked only to run 1.
        Assert.assertEquals(3, proteinIds.size());

        runIds.add(3L);
        proteinGroupIds = proteinGroupRepository.getConstraintLessProteinGroupIdsForRuns(runIds);
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        //3 proteins linked only to run 1 and 3.
        Assert.assertEquals(3, proteinIds.size());

        runIds.remove(1);
        runIds.add(2L);
        proteinGroupIds = proteinGroupRepository.getConstraintLessProteinGroupIdsForRuns(runIds);
        //3 proteins are only linked to protein group 1 and 2.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(3, proteinIds.size());

        runIds.clear();
        runIds.add(3L);
        proteinGroupIds = proteinGroupRepository.getConstraintLessProteinGroupIdsForRuns(runIds);
        //Run 3 does not have proteins linked uniquely to it.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(0, proteinIds.size());

        runIds.add(1L);
        runIds.add(2L);
        proteinGroupIds = proteinGroupRepository.getConstraintLessProteinGroupIdsForRuns(runIds);
        //All proteins are linked to one of the 3 runs.
        proteinIds = proteinRepository.getConstraintLessProteinIdsForProteinGroups(proteinGroupIds);
        Assert.assertEquals(4, proteinIds.size());
    }

//    @Test
//    public void testHibernateSearchFindBySequence() {
//        //(re)build the lucene indexes
//        proteinGroupRepository.rebuildIndex();
//        
//        Protein foundProtein = proteinGroupRepository.hibernateSearchFindBySequence("MGDERPHYYGKHGTPQKYDPTFKG");
//        Assert.assertNotNull(foundProtein);
//        Assert.assertEquals("MGDERPHYYGKHGTPQKYDPTFKG", foundProtein.getSequence());
//    }
}
