package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface provides repository methods for the Peptide class.
 *
 * @author Iain
 */
@Repository("peptideRepository")
@Transactional
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {

    @Override
    public List<Peptide> getPeptidesForSpectrum(Spectrum spectrum) {
        return createCriteria()
                .add(Restrictions.eq("spectrum", spectrum))
                .list();
    }

    @Override
    public List<PeptideDTO> getPeptideDTOByProteinGroupId(Long proteinGroupId) {
        Query query = getCurrentSession().getNamedQuery("Peptide.getPeptideDTOByProteinGroupId");
        query.setLong("proteinGroupId", proteinGroupId);

        List list = query.list();

        List<PeptideDTO> peptideDTOs = new ArrayList<>(list.size());
        for (Object object : list) {
            Object[] objectArray = (Object[]) object;
            PeptideDTO peptideDTO = new PeptideDTO();
            peptideDTO.setPeptide((Peptide) objectArray[0]);
            peptideDTO.setPeptideProbability((Double) objectArray[1]);
            peptideDTO.setPeptidePostErrorProbability((Double) objectArray[2]);

            //get the number of protein groups associated with the given peptide
            //and set it in the PeptideDTO instance
            Criteria criteria = getCurrentSession().createCriteria(Peptide.class, "peptide");
            criteria.createAlias("peptide.peptideHasProteinGroups", "peptideHasProteinGroup");
            criteria.add(Restrictions.eq("id", peptideDTO.getPeptide().getId()));
            criteria.setProjection(Projections.countDistinct("peptideHasProteinGroup.proteinGroup.id").as("proteinGroupCount"));
            long proteinGroupCount = (long) criteria.uniqueResult();
            peptideDTO.setProteinGroupCount(proteinGroupCount);

            peptideDTOs.add(peptideDTO);
        }

        return peptideDTOs;
    }
}