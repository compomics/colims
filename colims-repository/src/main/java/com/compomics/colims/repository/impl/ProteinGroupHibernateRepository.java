package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.model.ProteinGroupDTO;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
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
    public List<ProteinGroupDTO> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, SortDirection sortDirection, String filter) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("peptide.spectrum", "spectrum");
        criteria.createAlias("proteinGroup.proteinGroupHasProteins", "proteinGroupHasProtein");
        criteria.createAlias("proteinGroupHasProtein.protein", "protein");
        criteria.createAlias("protein.proteinAccessions", "proteinAccession");

        //restrictions
        criteria.add(Restrictions.eq("spectrum.analyticalRun.id", analyticalRun.getId()));
        criteria.add(Restrictions.eq("proteinGroupHasProtein.isMainGroupProtein", true));

        //projections
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("id").as("id"));
        projectionList.add(Projections.count("spectrum.id").as("spectrumCount"));
        projectionList.add(Projections.countDistinct("peptide.sequence").as("distinctPeptideCount"));
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
    public long getProteinGroupCountForRun(AnalyticalRun analyticalRun, String filter) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("peptide.spectrum", "spectrum");

        //restrictions
        criteria.add(Restrictions.eq("spectrum.analyticalRun.id", analyticalRun.getId()));

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
    public String getMainProteinSequence(ProteinGroup proteinGroup) {
        return (String) getCurrentSession()
                .createCriteria(ProteinGroupHasProtein.class, "proteinGroupHasProtein")
                .createAlias("proteinGroupHasProtein.protein", "protein")
                .add(Restrictions.eq("proteinGroup", proteinGroup))
                .add(Restrictions.eq("isMainGroupProtein", true))
                .setProjection(Projections.property("protein.sequence"))
                .uniqueResult();
    }

    @Override
    public ProteinGroup findByIdAndFetchAssociations(Long id) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class);

        //eager fetch collections
        criteria.setFetchMode("peptideHasProteinGroups", FetchMode.JOIN);

        //restrictions
        criteria.add(Restrictions.eq("id", id));

        ProteinGroup proteinGroup = (ProteinGroup) criteria.uniqueResult();

//        intialize peptide modifications
//        for(PeptideHasProteinGroup peptideHasProteinGroup : proteinGroup.p){
//        }

        return proteinGroup;
    }

    @Override
    public List<PeptideHasProteinGroup> getPeptideHasProteinGroups(Long id) {
        Criteria criteria = getCurrentSession().createCriteria(PeptideHasProteinGroup.class);

        //eager fetch collections
        criteria.setFetchMode("peptideHasProteinGroup.peptide", FetchMode.JOIN);
        criteria.setFetchMode("peptide.peptideHasModifications", FetchMode.JOIN);

        //restrictions
        criteria.add(Restrictions.eq("proteinGroup.id", id));

        List<PeptideHasProteinGroup> peptideHasProteinGroups = criteria.list();

        return peptideHasProteinGroups;
    }

    @Override
    public List<String> getAccessionsForProteinGroup(ProteinGroup proteinGroup) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroupHasProtein.class);

        //restriction
        criteria.add(Restrictions.eq("proteinGroup.id", proteinGroup.getId()));

        //projection
        criteria.setProjection(Projections.distinct(Projections.property("proteinAccession")));

        return criteria.list();
    }
}
