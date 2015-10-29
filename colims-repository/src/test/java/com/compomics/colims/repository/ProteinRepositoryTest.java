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

    @Test
    public void testGetConstraintLessProteinIdsForRunsTest() {
        List<Long> runIds = new ArrayList<>();
        runIds.add(1L);

        List<Long> proteinIds = proteinRepository.getConstraintLessProteinIdsForRuns(runIds);

        //5 ProteinGroupHasProtein entries, 4 linked to 1 run.
        //The last 4 are linked to 4 proteins but one of the proteins is also linked to the other ProteinGroupHasProtein entry
        //so only 3 protein IDs should be returned.
        Assert.assertEquals(3, proteinIds.size());
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
