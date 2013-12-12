package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Sample;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.SampleRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("sampleRepository")
public class SampleHibernateRepository extends GenericHibernateRepository<Sample, Long> implements SampleRepository {

}
