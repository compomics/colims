package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.compomics.colims.repository.hibernate.SortDirection;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinGroupRepository")
public class ProteinGroupHibernateRepository extends GenericHibernateRepository<ProteinGroup, Long> implements ProteinGroupRepository {

    @Override
    public List<ProteinGroupDTO> getPagedProteinGroupsForRun(List<Long> analyticalRunIds, int start, int length, String orderBy, SortDirection sortDirection, String filter) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("peptide.spectrum", "spectrum");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("proteinGroup.proteinGroupHasProteins", "proteinGroupHasProtein");
        criteria.createAlias("proteinGroupHasProtein.protein", "protein");

        //restrictions
        criteria.add(Restrictions.in("spectrum.analyticalRun.id", analyticalRunIds));
        criteria.add(Restrictions.eq("proteinGroupHasProtein.isMainGroupProtein", true));

        //projections
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("id").as("id"));
        projectionList.add(Projections.count("spectrum.id").as("spectrumCount"));
        projectionList.add(Projections.countDistinct("peptide.sequence").as("distinctPeptideSequenceCount"));
        projectionList.add(Projections.property("proteinGroup.proteinProbability").as("proteinProbability"));
        projectionList.add(Projections.property("proteinGroup.proteinPostErrorProbability").as("proteinPostErrorProbability"));
        projectionList.add(Projections.property("proteinGroupHasProtein.proteinAccession").as("mainAccession"));
        projectionList.add(Projections.property("protein.sequence").as("mainSequence"));
        criteria.setProjection(projectionList);

        //paging
        criteria.setFirstResult(start);
        criteria.setMaxResults(length);

        //transform results into ProteinGroupForRun instances
        criteria.setResultTransformer(Transformers.aliasToBean(ProteinGroupDTO.class));

        //order
        if (!orderBy.isEmpty()) {
            if (sortDirection.equals(SortDirection.ASCENDING)) {
                criteria.addOrder(Order.asc(orderBy));
            } else {
                criteria.addOrder(Order.desc(orderBy));
            }
        }

        //filter
        if (!filter.isEmpty()) {
            //filter restrictions
            filter = "%" + filter + "%";
            criteria.add(Restrictions.or(Restrictions.ilike("protein.sequence", filter), Restrictions.ilike("proteinGroupHasProtein.proteinAccession", filter)));
        }

        return criteria.list();
    }

    @Override
    public long getProteinGroupCountForRun(List<Long> analyticalRunIds, String filter) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("peptide.spectrum", "spectrum");

        //restrictions
        criteria.add(Restrictions.in("spectrum.analyticalRun.id", analyticalRunIds));

        //projections
        criteria.setProjection(Projections.countDistinct("id"));

        //filter
        if (!filter.isEmpty()) {
            //joins
            criteria.createAlias("proteinGroup.proteinGroupHasProteins", "proteinGroupHasProtein");
            criteria.createAlias("proteinGroupHasProtein.protein", "protein");

            //filter restrictions
            filter = "%" + filter + "%";
            criteria.add(Restrictions.or(Restrictions.ilike("protein.sequence", filter), Restrictions.ilike("proteinGroupHasProtein.proteinAccession", filter)));
        }

        return (long) criteria.uniqueResult();
    }

    @Override
    public Object[] getProteinGroupsProjections(AnalyticalRun analyticalRun) {
        return null;
    }

    @Override
    public void saveOrUpdate(ProteinGroup proteinGroup) {
        getCurrentSession().saveOrUpdate(proteinGroup);
    }

    @Override
    public List<ProteinGroupDTO> getProteinGroupsForRun(List<Long> analyticalRunIds) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("peptide.spectrum", "spectrum");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("proteinGroup.proteinGroupHasProteins", "proteinGroupHasProtein");
        criteria.createAlias("proteinGroupHasProtein.protein", "protein");

        //restrictions
        criteria.add(Restrictions.in("spectrum.analyticalRun.id", analyticalRunIds));
        criteria.add(Restrictions.eq("proteinGroupHasProtein.isMainGroupProtein", true));

        //projections
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("id").as("id"));
        projectionList.add(Projections.count("spectrum.id").as("spectrumCount"));
        projectionList.add(Projections.countDistinct("peptide.sequence").as("distinctPeptideSequenceCount"));
        projectionList.add(Projections.property("proteinGroup.proteinProbability").as("proteinProbability"));
        projectionList.add(Projections.property("proteinGroup.proteinPostErrorProbability").as("proteinPostErrorProbability"));
        projectionList.add(Projections.property("proteinGroupHasProtein.proteinAccession").as("mainAccession"));
        projectionList.add(Projections.property("protein.sequence").as("mainSequence"));
        criteria.setProjection(projectionList);

        //transform results into ProteinGroupForRun instances
        criteria.setResultTransformer(Transformers.aliasToBean(ProteinGroupDTO.class));

        return criteria.list();
    }
}
