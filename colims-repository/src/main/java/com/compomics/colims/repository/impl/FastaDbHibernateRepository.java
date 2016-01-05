package com.compomics.colims.repository.impl;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.repository.FastaDbRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.hibernate.type.EnumType;
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
