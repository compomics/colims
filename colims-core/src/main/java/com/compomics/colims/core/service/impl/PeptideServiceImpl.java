package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.repository.ModificationRepository;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import org.apache.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
            for(PeptideHasModification peptideHasModification : peptideHasModifications){
                peptideHasModification.setModification(modificationRepository.findById(peptideHasModification.getModification().getId()));
            }
            peptide.setPeptideHasModifications(peptideHasModifications);
        }
    }

    @Override
    public List<PeptideDTO> getPeptideDTO(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getPeptideDTOByProteinGroupIdAnalyticalRunId(proteinGroupId, analyticalRunIds);
    }

    @Override
    public List<String> getDistinctPeptideSequence(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getDistinctPeptideSequenceByProteinGroupIdAnalyticalRunId(proteinGroupId, analyticalRunIds);
    }

    @Override
    public List<Peptide> getUniquePeptides(Long proteinGroupId, List<Long> analyticalRunIds) {
        return peptideRepository.getUniquePeptideByProteinGroupIdAnalyticalRunId(proteinGroupId, analyticalRunIds);
    }


    @Override
    public Map<PeptideHasProteinGroup, AnalyticalRun> getPeptideHasProteinGroupByAnalyticalRunId(List<Long> analyticalRunIds) {
        return peptideRepository.getPeptideHasProteinGroupByAnalyticalRunId(analyticalRunIds);
    }
}
