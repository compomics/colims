package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        Criteria criteria = getCurrentSession().createCriteria(Peptide.class, "peptide");

        //joins
        criteria.createAlias("peptideHasProteinGroups", "peptideHasProteinGroup");

        //restrictions
        criteria.add(Restrictions.eq("peptideHasProteinGroup.proteinGroup.id", proteinGroupId));

        //projection
//        ProjectionList projections = Projections.projectionList();
//        projections.add(Projections.property("peptideHasProteinGroup.peptideProbability").as("peptideProbability"));
//        projections.add(Projections.property("peptideHasProteinGroup.peptidePostErrorProbability").as("peptidePostErrorProbability"));
//        criteria.setProjection(projections);

        //eager fetch collections
        criteria.setFetchMode("peptideHasProteinGroups", FetchMode.JOIN);
        criteria.setFetchMode("peptideHasModifications", FetchMode.JOIN);
        criteria.setFetchMode("peptideHasModifications.modification", FetchMode.JOIN);

        //transform results into ProteinGroupForRun instances
//        criteria.setResultTransformer(Transformers.aliasToBean(PeptideDTO.class));

        List list = criteria.list();

        return list;
    }
}