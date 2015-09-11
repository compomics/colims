package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinRepository")
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {

    public static final String PROTEIN_IDS_QUERY = new StringBuilder()
            .append("SELECT ")
            .append("DISTINCT protein.id ")
            .append("from protein ")
            .append("LEFT JOIN protein_group_has_protein ON protein_group_has_protein.l_protein_id = protein.id ")
            .append("AND protein_group_has_protein.id NOT IN ")
            .append("( ")
            .append("   SELECT ")
            .append("   DISTINCT pg_has_p.id ")
            .append("   FROM protein_group_has_protein pg_has_p ")
            .append("   JOIN peptide_has_protein_group p_has_pg ON p_has_pg.l_protein_group_id = pg_has_p.l_protein_group_id ")
            .append("   JOIN peptide pep ON pep.id = p_has_pg.l_peptide_id ")
            .append("   JOIN spectrum sp ON sp.id = pep.l_spectrum_id ")
            .append("   WHERE sp.l_analytical_run_id IN (:ids) ")
            .append(") ")
            .append("WHERE protein_group_has_protein.l_protein_id IS NULL ")
            .append("; ")
            .toString();

    @Override
    public Protein findBySequence(String sequence) {
        Criteria criteria = createCriteria(Restrictions.eq("sequence", sequence));
        criteria.setCacheable(true);
        return (Protein) criteria.uniqueResult();
    }

    @Override
    public List<Long> getConstraintLessProteinIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(PROTEIN_IDS_QUERY);
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("protein.id", LongType.INSTANCE);

        return sqlQuery.list();
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
