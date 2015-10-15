package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.model.ProteinGroupForRun;
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

    private static final String PAGED_QUERY = "SELECT DISTINCT protein_group.id, MAX(%3$s) FROM protein_group"
            + " LEFT JOIN protein_group_has_protein pg_has_p ON pg_has_p.l_protein_group_id = protein_group.id"
            + " LEFT JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = protein_group.id"
            + " LEFT JOIN protein ON protein.id = pg_has_p.l_protein_id"
            + " LEFT JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id"
            + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE (protein.protein_sequence LIKE '%2$s'"
            + " OR pg_has_p.protein_accession LIKE '%2$s')"
            + " AND sp.l_analytical_run_id = %1$d"
            + " GROUP BY protein_group.id";

    private static final String PAGED_QUERY_NO_FILTER = "SELECT DISTINCT protein_group.id, MAX(%3$s) FROM protein_group"
            + " LEFT JOIN protein_group_has_protein pg_has_p ON pg_has_p.l_protein_group_id = protein_group.id"
            + " LEFT JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = protein_group.id"
            + " LEFT JOIN protein ON protein.id = pg_has_p.l_protein_id"
            + " LEFT JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id"
            + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE sp.l_analytical_run_id = %1$d"
            + " GROUP BY protein_group.id";

    @Override
    public List<ProteinGroupForRun> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, SortDirection sortDirection, String filter) {
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
        criteria.setResultTransformer(Transformers.aliasToBean(ProteinGroupForRun.class));

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
        criteria.setFetchMode("peptideHasProteinGroups", FetchMode.JOIN);
        criteria.add(Restrictions.eq("id", id));

        return (ProteinGroup) criteria.uniqueResult();
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
