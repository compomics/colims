package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinRepository")
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {

    public static final String BASE_QUERY = "SELECT DISTINCT protein.id, MAX(%3$s) FROM protein"
        + " LEFT JOIN protein_group_has_protein pg_has_p ON pg_has_p.l_protein_id = protein.id"
        + " LEFT JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = pg_has_p.l_protein_group_id"  // hmm
        + " LEFT JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id"
        + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
        + " LEFT JOIN protein_accession ON protein.id = protein_accession.l_protein_id"
        + " WHERE (protein.protein_sequence LIKE '%2$s'"
        + " OR protein_accession.accession LIKE '%2$s')"
        + " AND sp.l_analytical_run_id = %1$d"
        + " GROUP BY protein.id";

    @Override
    public Protein findBySequence(String sequence) {
        Criteria criteria = createCriteria(Restrictions.eq("sequence", sequence));
        criteria.setCacheable(true);
        return (Protein) criteria.uniqueResult();
    }

    @Override
    public List<Protein> getPagedProteinsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter) {
        List<Protein> proteins = new ArrayList<>();

        String extraParams = " ORDER BY MAX(%3$s) %4$s, protein.id";

        if (length > 0) {
            extraParams += " LIMIT %5$d  OFFSET %6$d";
        }

        final List idList = getCurrentSession()
            .createSQLQuery(String.format(BASE_QUERY + extraParams, analyticalRun.getId(), "%" + filter + "%", orderBy, direction, length, start))
            .addScalar("protein.id", LongType.INSTANCE)
            .list();

        if (idList.size() > 0) {
            proteins = createCriteria().add(Restrictions.in("id", idList)).list();

            Collections.sort(proteins, (s1, s2) -> Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId())));
        }

        return proteins;
    }

    @Override
    public int getProteinCountForRun(AnalyticalRun analyticalRun, String filter) {
        return getCurrentSession().createSQLQuery(String.format(BASE_QUERY, analyticalRun.getId(), "%" + filter + "%", "protein.id"))
            .list().size();
    }

//    @Override
//    public Protein hibernateSearchFindBySequence(String sequence) {
//        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());
//
//        final QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
//                .buildQueryBuilder().forEntity(Protein.class).get();
//
//        org.apache.lucene.search.Query luceneQuery
//                = queryBuilder.keyword().onField("sequence").matching(sequence).createQuery();
//
//        org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, Protein.class);
//        
//        return (Protein) fullTextQuery.uniqueResult();
//    }
//
//    @Override
//    public void rebuildIndex() {
//        FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());
//        try {
//            fullTextSession.createIndexer(Protein.class).startAndWait();
//        } catch (InterruptedException ex) {
//            LOGGER.error(ex);
//        }
//    }

}
