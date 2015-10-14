package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.hibernate.model.ProteinGroupForRun;
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
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProteinGroupRepositoryTest {

    @Autowired
    private ProteinGroupRepository proteinGroupRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Test
    public void testGetPagedProteinsForRunTest() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        List<ProteinGroupForRun> proteinGroups = proteinGroupRepository.getPagedProteinGroupsForRun(analyticalRun, 0, 20, "protein.id", "asc", "");

        assertThat(proteinGroups.size(), not(0));
//        assertThat(proteinGroups.get(0).getId(), is(1L));

        proteinGroups = proteinGroupRepository.getPagedProteinGroupsForRun(analyticalRun, 0, 20, "protein.id", "asc", "NOTAPROTEIN");

        assertThat(proteinGroups.size(), is(0));
    }

    @Test
    public void testGetProteinGroupCountForRun() {
        AnalyticalRun analyticalRun = analyticalRunRepository.findById(1L);

        assertThat(proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, ""), not(0L));
        assertThat(proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, "ACC_11"), not(0L));
        assertThat(proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, "LENNART"), not(0L));

        assertThat(proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, "AC_11"), is(0L));
        assertThat(proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, "NOTAPROTEIN"), is(0L));
    }

    @Test
    public void testGetMainProteinSequence() {
        ProteinGroup proteinGroup = proteinGroupRepository.findById(1L);

        assertThat(proteinGroupRepository.getMainProteinSequence(proteinGroup), is("AAAAAAAAAAAAAAAAAAAAAAABLENNARTMAAAAAAAAAAAAA"));
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
