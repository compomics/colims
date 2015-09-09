package com.compomics.colims.repository;

import com.compomics.colims.model.*;
import org.junit.Ignore;
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
    @Autowired
    ProteinRepository proteinRepository;

    @Ignore
    @Test
    public void testGetPeptidesForProtein() throws Exception {
        Protein protein = proteinRepository.findById(4L);

        List<Long> spectrumIds = new ArrayList<>();
        spectrumIds.add(1L);

        List<Peptide> peptides = peptideRepository.getPeptidesForProtein(protein, spectrumIds);

        assertThat(peptides.size(), not(0));
    }

    @Ignore
    @Test
    public void testGetPeptidesFromSequence() {
        String sequence = "ABCDEFGH";
        List<Long> ids = new ArrayList<>();
        ids.add(1L);

        List peptides = peptideRepository.getPeptidesFromSequence(sequence, ids);

        assertThat(peptides.size(), not(0));
    }

    @Test
    public void testGetModificationsForMultiplePeptides() {
        List<Peptide> peptides = new ArrayList<>();
        peptides.add(peptideRepository.findById(1L));
        peptides.add(peptideRepository.findById(2L));

        List<PeptideHasModification> peptideHasModifications = peptideRepository.getModificationsForMultiplePeptides(peptides);

        assertThat(peptideHasModifications.size(), not(0));
    }

    @Test
    public void testGetProteinAccessionsForPeptide() {
        Peptide peptide = peptideRepository.findById(1L);

        List<String> proteinAccessions = peptideRepository.getProteinAccessionsForPeptide(peptide);

        assertThat(proteinAccessions.size(), not(0));
    }
}