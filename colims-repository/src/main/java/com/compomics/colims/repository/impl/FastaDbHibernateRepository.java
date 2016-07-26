package com.compomics.colims.repository.impl;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.repository.FastaDbRepository;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("fastaDbRepository")
public class FastaDbHibernateRepository extends GenericHibernateRepository<FastaDb, Long> implements FastaDbRepository {

    @Override
    public List<FastaDb> findByFastaDbType(List<FastaDbType> fastaDbTypes) {
        Query query = getCurrentSession().getNamedQuery("FastaDb.findByFastaDbType");

        query.setParameterList("fastaDbTypeOrdinals", fastaDbTypes);

        return query.list();
    }

}
