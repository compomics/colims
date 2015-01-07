package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kenneth Verheggen
 */
@Repository("analyticalRunRepository")
public class AnalyticalRunHibernateRepository extends GenericHibernateRepository<AnalyticalRun, Long> implements AnalyticalRunRepository {
    // TODO: everything or possibly nothing
}
