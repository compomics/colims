package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.PeptideRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Niels Hulstaert
 */
@Service("peptideService")
@Transactional
public class PeptideServiceImpl implements PeptideService {

    private static final Logger LOGGER = Logger.getLogger(PeptideServiceImpl.class);
    @Autowired
    PeptideRepository peptideRepository;

    @Override
    public Peptide findById(Long id) {
        return peptideRepository.findById(id);
    }

    @Override
    public List<Peptide> findAll() {
        return peptideRepository.findAll();
    }

    @Override
    public void save(Peptide entity) {
        peptideRepository.save(entity);
    }

    @Override
    public void update(Peptide entity) {
        peptideRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Peptide entity) {
        peptideRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(Peptide entity) {
        peptideRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return peptideRepository.countAll();
    }

    @Override
    public List<Peptide> findBySpectrumId(long spectrumId) {
        return peptideRepository.findPeptideBySpectrumId(spectrumId);
    }

    @Override
    public void fetchPeptideHasModificiations(Peptide peptide) {
        try {
            //attach the peptide to the new session
            peptideRepository.saveOrUpdate(peptide);
            if (!Hibernate.isInitialized(peptide.getPeptideHasModifications())) {
                Hibernate.initialize(peptide.getPeptideHasModifications());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }
}
