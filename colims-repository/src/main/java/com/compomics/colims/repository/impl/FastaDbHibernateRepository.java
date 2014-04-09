package com.compomics.colims.repository.impl;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.repository.FastaDbRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("fastaDbRepository")
public class FastaDbHibernateRepository extends GenericHibernateRepository<FastaDb, Long> implements FastaDbRepository {
    
}
