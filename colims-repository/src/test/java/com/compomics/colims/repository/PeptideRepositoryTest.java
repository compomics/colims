package com.compomics.colims.repository;

import com.compomics.colims.model.Protein;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Iain on 09/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PeptideRepositoryTest extends TestCase {

    @Autowired
    private PeptideRepository peptideRepository;

    @Test
    public void testGetPeptidesForProtein() throws Exception {
        Protein protein = new Protein();
        protein.setId(1L);

        List<Long> spectrumIds = new ArrayList<>();
        spectrumIds.add(0L);
        spectrumIds.add(15L);
        spectrumIds.add(112L);
        spectrumIds.add(338L);
        spectrumIds.add(1000L);

        List<Object[]> weirdObjectThings = peptideRepository.getPeptidesForProtein(protein, spectrumIds);

        assertThat(weirdObjectThings.size(), not(spectrumIds.size()));
    }
}