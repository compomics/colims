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
@Transactional
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ProteinHibernateRepository.class);

    @Override
    public Protein findBySequence(String sequence) {
        Criteria criteria = createCriteria(Restrictions.eq("sequence", sequence));
        criteria.setCacheable(true);
        return (Protein) criteria.uniqueResult();
    }

    @Override
    public List<Protein> getProteinsForRun(AnalyticalRun analyticalRun) {
        List<Protein> proteins = new ArrayList<>();

        String queryString = "SELECT DISTINCT pro.id FROM protein pro"
            + " JOIN peptide_has_protein php ON php.l_protein_id = pro.id"
            + " JOIN peptide pep ON pep.id = php.l_peptide_id"
            + " JOIN spectrum sp ON sp.id = pep.l_spectrum_id"
            + " WHERE sp.l_analytical_run_id = " + analyticalRun.getId();

        final List idList = getCurrentSession().createSQLQuery(queryString).addScalar("pro.id", LongType.INSTANCE).list();

        if (idList.size() > 0) {
            proteins = createCriteria().add(Restrictions.in("id", idList)).list();

            Collections.sort(proteins, (s1, s2) -> Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId())));
        }

        return proteins;
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
