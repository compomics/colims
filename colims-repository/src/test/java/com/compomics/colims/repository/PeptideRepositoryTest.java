package com.compomics.colims.repository;

import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
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
import static org.junit.Assert.*;

/**
 * Created by Iain on 14/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PeptideRepositoryTest {

    @Autowired
    PeptideRepository peptideRepository;

    @Test
    public void testGetPeptidesForProtein() throws Exception {
        Protein protein = new Protein();
        protein.setId(1l);
        protein.setSequence("BREADBREADBREADBREADBREAD");

        List<Long> spectrumIds = new ArrayList<>();
        spectrumIds.add(1L);

        List<PeptideHasProtein> peptides = peptideRepository.getPeptidesForProtein(protein, spectrumIds);

        assertThat(peptides.size(), not(0));
    }
}