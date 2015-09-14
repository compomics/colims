package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.ProteinGroupRepository;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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

    public static final String BASE_QUERY = "SELECT DISTINCT protein_group.id, MAX(%3$s) FROM protein_group"
            + " LEFT JOIN protein_group_has_protein pg_has_p ON pg_has_p.l_protein_group_id = protein_group.id"
            + " LEFT JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = protein_group.id"
            + " LEFT JOIN protein ON protein.id = pg_has_p.l_protein_id"
            + " LEFT JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id"
            + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE (protein.protein_sequence LIKE '%2$s'"
            + " OR pg_has_p.protein_accession LIKE '%2$s')"
            + " AND sp.l_analytical_run_id = %1$d"
            + " GROUP BY protein_group.id";

    @Override
    public List<ProteinGroup> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        List<ProteinGroup> proteins = new ArrayList<>();

        String extraParams = " ORDER BY MAX(%3$s) %4$s, protein_group.id";

        if (length > 0) {
            extraParams += " LIMIT %5$d  OFFSET %6$d";
        }

        final List idList = getCurrentSession()
                .createSQLQuery(String.format(BASE_QUERY + extraParams, analyticalRun.getId(), "%" + filter + "%", orderBy, direction, length, start))
                .addScalar("protein_group.id", LongType.INSTANCE)
                .list();

        if (idList.size() > 0) {
            proteins = createCriteria().add(Restrictions.in("id", idList)).list();

            Collections.sort(proteins, (s1, s2) -> Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId())));
        }

        return proteins;
    }

    @Override
    public int getProteinGroupCountForRun(AnalyticalRun analyticalRun, String filter) {
        return getCurrentSession().createSQLQuery(String.format(BASE_QUERY, analyticalRun.getId(), "%" + filter + "%", "protein_group.id"))
                .list().size();
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
