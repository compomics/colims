package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.model.ProteinGroupForRun;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<ProteinGroupForRun> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroup.class, "proteinGroup");

        //joins
        criteria.createAlias("proteinGroup.peptideHasProteinGroups", "peptideHasProteinGroup");
        criteria.createAlias("peptideHasProteinGroup.peptide", "peptide");
        criteria.createAlias("peptide.spectrum", "spectrum");
        criteria.createAlias("proteinGroup.proteinGroupHasProteins", "proteinGroupHasProtein");
        criteria.createAlias("proteinGroupHasProtein.protein", "protein");

        //restrictions
        criteria.add(Restrictions.eq("spectrum.analyticalRun.id", analyticalRun.getId()));
//        criteria.add(Restrictions.eq("proteinGroupHasProtein.isMainGroupProtein", true));

        //projections
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("id").as("proteinGroupId"));
        projectionList.add(Projections.count("spectrum.id").as("spectrumCount"));
        projectionList.add(Projections.countDistinct("peptide.sequence").as("distinctPeptideCount"));
        projectionList.add(Projections.property("proteinGroupHasProtein.proteinAccession").as("mainAccession"));
//        projectionList.add(Projections.("proteinGroupHasProtein.proteinAccession").as("mainAccession"));
//        projectionList.add(Projections.count("protein.").as("mainAccession"));
        criteria.setProjection(projectionList);

        //paging
        criteria.setFirstResult(start);
        criteria.setMaxResults(length);

        //transform results into ProteinGroupForRun instances
//        criteria.setResultTransformer(Transformers.aliasToBean(ProteinGroupForRun.class));

        //order
        criteria.addOrder(Order.asc("distinctPeptideCount"));

        List list = criteria.list();

        return list;
    }

    public List<ProteinGroup> getPagedProteinGroupsForRunOld(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        List<ProteinGroup> proteins = new ArrayList<>();

        String extraParams = " ORDER BY MAX(%3$s) %4$s, protein_group.id";

        if (length > 0) {
            extraParams += " LIMIT %5$d  OFFSET %6$d";
        }

        final List idList = getCurrentSession()
                .createSQLQuery(String.format(PAGED_QUERY + extraParams, analyticalRun.getId(), "%" + filter + "%", orderBy, direction, length, start))
                .addScalar("protein_group.id", LongType.INSTANCE)
                .list();

        if (idList.size() > 0) {
            proteins = createCriteria().add(Restrictions.in("id", idList)).list();

            Collections.sort(proteins, (s1, s2) -> Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId())));
        }

        return proteins;
    }

    @Override
    public long getProteinGroupCountForRun(AnalyticalRun analyticalRun, String filter) {
        if (!filter.isEmpty()) {
            Query namedQuery = getCurrentSession().getNamedQuery("ProteinGroup.getProteinGroupCountForRun");
            namedQuery.setLong("analyticalRunId", analyticalRun.getId());
            namedQuery.setString("filter", "%" + filter + "%");
            return (long) namedQuery.uniqueResult();
        } else {
            Query namedQuery = getCurrentSession().getNamedQuery("ProteinGroup.getProteinGroupCountForRunNoFilter");
            namedQuery.setLong("analyticalRunId", analyticalRun.getId());
            return (long) namedQuery.uniqueResult();
        }
    }

    @Override
    public String getMainProteinSequence(ProteinGroup proteinGroup) {
        return getCurrentSession()
                .createCriteria(ProteinGroupHasProtein.class, "proteinGroupHasProtein")
                .createAlias("proteinGroupHasProtein.protein", "protein")
                .add(Restrictions.eq("proteinGroup", proteinGroup))
                .add(Restrictions.eq("isMainGroupProtein", true))
                .setProjection(Projections.property("protein.sequence"))
                .uniqueResult().toString();
    }
}
