package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import org.apache.log4j.Logger;
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
    public long countAll() {
        return peptideRepository.countAll();
    }

    @Override
    public void persist(Peptide entity) {
        peptideRepository.persist(entity);
    }

    @Override
    public Peptide merge(Peptide entity) {
        return peptideRepository.merge(entity);
    }

    @Override
    public void remove(Peptide entity) {
        peptideRepository.remove(entity);
    }

    @Override
    public Peptide fetchPeptideHasModifications(final Peptide peptide) {
        try {
            peptide.getPeptideHasModifications().size();
            return peptide;
        } catch (LazyInitializationException e) {
            //merge the peptide
            Peptide merge = peptideRepository.merge(peptide);
            merge.getPeptideHasModifications().size();
            return merge;
        }
    }

    @Override
    public List<PeptideDTO> getPeptideDTOByProteinGroupId(Long proteinGroupId) {
        return peptideRepository.getPeptideDTOByProteinGroupId(proteinGroupId);
    }
}
