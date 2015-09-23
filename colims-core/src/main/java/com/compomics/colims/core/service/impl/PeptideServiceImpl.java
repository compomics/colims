package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.PeptideRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("peptideService")
@Transactional
public class PeptideServiceImpl implements PeptideService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PeptideServiceImpl.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Override
    public Peptide findById(final Long id) {
        return peptideRepository.findById(id);
    }

    @Override
    public List<Peptide> findAll() {
        return peptideRepository.findAll();
    }

    @Override
    public void save(final Peptide entity) {
        peptideRepository.save(entity);
    }

    @Override
    public void update(final Peptide entity) {
        peptideRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Peptide entity) {
        peptideRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Peptide entity) {
        peptideRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return peptideRepository.countAll();
    }

    @Override
    public void fetchPeptideHasModifications(final Peptide peptide) {
        try {
            peptide.getPeptideHasModifications().size();
        } catch (LazyInitializationException e) {
            //attach the peptide to the new session
            peptideRepository.saveOrUpdate(peptide);
            if (!Hibernate.isInitialized(peptide.getPeptideHasModifications())) {
                Hibernate.initialize(peptide.getPeptideHasModifications());
            }
        }
    }

    @Override
    public List<Peptide> getPeptidesForProteinGroup(ProteinGroup proteinGroup, List<Long> spectrumIds) {
        return peptideRepository.getPeptidesForProteinGroup(proteinGroup, spectrumIds);
    }

    @Override
    public List getPeptidesFromSequence(String sequence, List<Long> spectrumIds) {
        return peptideRepository.getPeptidesFromSequence(sequence, spectrumIds);
    }

    @Override
    public List<PeptideHasModification> getModificationsForMultiplePeptides(List<Peptide> peptides) {
        return peptideRepository.getModificationsForMultiplePeptides(peptides);
    }

    @Override
    public List<Peptide> getPeptidesForSpectrum(Spectrum spectrum) {
        return peptideRepository.getPeptidesForSpectrum(spectrum);
    }
}
