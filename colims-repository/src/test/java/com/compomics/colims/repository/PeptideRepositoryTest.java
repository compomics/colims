package com.compomics.colims.repository;

import com.compomics.colims.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
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
    ProteinGroupRepository proteinGroupRepository;
    @Autowired
    SpectrumRepository spectrumRepository;

    @Test
    public void testGetPeptidesForProteinGroup() throws Exception {
        ProteinGroup proteinGroup = proteinGroupRepository.findById(1L);

        List<Long> spectrumIds = new ArrayList<>();
        spectrumIds.add(1L);

        List<Peptide> peptides = peptideRepository.getPeptidesForProteinGroup(proteinGroup, spectrumIds);

        assertThat(peptides.size(), greaterThan(0));
    }

    @Test
    public void testGetPeptidesFromSequence() {
        String sequence = "LENNART";
        List<Long> ids = new ArrayList<>();
        ids.add(1L);

        List peptides = peptideRepository.getPeptidesFromSequence(sequence, ids);

        assertThat(peptides.size(), greaterThan(0));
    }

    @Test
    public void testGetModificationsForMultiplePeptides() {
        List<Peptide> peptides = new ArrayList<>();
        peptides.add(peptideRepository.findById(1L));
        peptides.add(peptideRepository.findById(2L));

        List<PeptideHasModification> peptideHasModifications = peptideRepository.getModificationsForMultiplePeptides(peptides);

        assertThat(peptideHasModifications.size(), greaterThan(0));
    }

    @Test
    public void testGetPeptidesForSpectrum() {
        Spectrum spectrum = spectrumRepository.findById(1L);

        List<Peptide> peptides = peptideRepository.getPeptidesForSpectrum(spectrum);

        assertThat(peptides.size(), greaterThan(0));
    }
}