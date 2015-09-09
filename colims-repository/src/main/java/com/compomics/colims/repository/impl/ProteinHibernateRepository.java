package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinRepository")
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {

    public static final String BASE_QUERY = "SELECT DISTINCT protein.id FROM protein"
            + " LEFT JOIN protein_group_has_protein pg_has_p ON pg_has_p.l_protein_id = protein.id"
            + " LEFT JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = pg_has_p.l_protein_group_id"
            + " LEFT JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id"
            + " LEFT JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE sp.l_analytical_run_id = %1$d";

    @Override
    public Protein findBySequence(String sequence) {
        Criteria criteria = createCriteria(Restrictions.eq("sequence", sequence));
        criteria.setCacheable(true);
        return (Protein) criteria.uniqueResult();
    }

    @Override
    public List<Long> getProteinIdsForRun(AnalyticalRun analyticalRun) {
        List<Long> proteinIds = getCurrentSession()
                .createSQLQuery(String.format(BASE_QUERY, analyticalRun.getId()))
                .addScalar("protein.id", LongType.INSTANCE)
                .list();

        return proteinIds;
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
