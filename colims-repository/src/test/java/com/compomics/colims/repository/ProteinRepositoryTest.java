package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProteinRepositoryTest {

    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Test
    public void testGetProteinIdsForRunTest() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        List<Long> proteinIds = proteinRepository.getProteinIdsForRun(analyticalRun);

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
