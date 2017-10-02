package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.ModificationRepository;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.SpectrumRepository;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import com.compomics.colims.repository.hibernate.PeptideMzTabDTO;
import org.slf4j.Logger;
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

    private final PeptideRepository peptideRepository;
    private final ModificationRepository modificationRepository;

    @Autowired
    public PeptideServiceImpl(PeptideRepository peptideRepository, ModificationRepository modificationRepository) {
        this.peptideRepository = peptideRepository;
        this.modificationRepository = modificationRepository;
    }

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
    public void fetchPeptideHasModifications(final Peptide peptide) {
        try {
            peptide.getPeptideHasModifications().size();
        } catch (LazyInitializationException e) {
            //fetch the PeptideHasModification instance
            List<PeptideHasModification> peptideHasModifications = peptideRepository.fetchPeptideHasModifications(peptide.getId());
            for (PeptideHasModification peptideHasModification : peptideHasModifications) {
                peptideHasModification.setModification(modificationRepository.findById(peptideHasModification.getModification().getId()));
            }
            peptide.setPeptideHasModifications(peptideHasModifications);
        }
    }

    @Override
    public List<PeptideDTO> getPeptideDTOs(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getPeptideDTOs(proteinGroupId, analyticalRunIds);
    }

    @Override
    public List<String> getDistinctPeptideSequences(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getDistinctPeptideSequences(proteinGroupId, analyticalRunIds);
    }

    @Override
    public List<Peptide> getUniquePeptides(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getUniquePeptides(proteinGroupId, analyticalRunIds);
    }


    @Override
    public List<PeptideMzTabDTO> getPeptideMzTabDTOs(List<Long> analyticalRunIds) {
        return peptideRepository.getPeptideMzTabDTOs(analyticalRunIds);
    }
}
