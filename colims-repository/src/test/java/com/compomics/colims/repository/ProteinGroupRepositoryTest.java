package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProteinRepositoryTest {

    @Autowired
    private ProteinGroupRepository proteinRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Test
    public void testFindBySequence() {
        Protein foundProtein = proteinRepository.findBySequence("MGDERPHYYGKHGTPQKYDPTFKG");
        Assert.assertNotNull(foundProtein);
        Assert.assertEquals("MGDERPHYYGKHGTPQKYDPTFKG", foundProtein.getSequence());
    }

    @Test
    public void testGetPagedProteinsForRunTest() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        List<Protein> proteins = proteinRepository.getPagedProteinGroupsForRun(analyticalRun, 0, 20, "protein.id", "asc", "");

        assertThat(proteins.size(), not(0));
        assertThat(proteins.get(0).getId(), is(1L));

        proteins = proteinRepository.getPagedProteinGroupsForRun(analyticalRun, 0, 20, "protein.id", "asc", "NOTAPROTEIN");

        assertThat(proteins.size(), is(0));
    }

    @Test
    public void testGetProteinCountForRun() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        assertThat(proteinRepository.getProteinCountForRun(analyticalRun, ""), not(0));
        assertThat(proteinRepository.getProteinCountForRun(analyticalRun, "NOTAPROTEIN"), is(0));
    }

//    @Test
//    public void testHibernateSearchFindBySequence() {
//        //(re)build the lucene indexes
//        proteinRepository.rebuildIndex();
//        
//        Protein foundProtein = proteinRepository.hibernateSearchFindBySequence("MGDERPHYYGKHGTPQKYDPTFKG");
//        Assert.assertNotNull(foundProtein);
//        Assert.assertEquals("MGDERPHYYGKHGTPQKYDPTFKG", foundProtein.getSequence());
//    }
}
