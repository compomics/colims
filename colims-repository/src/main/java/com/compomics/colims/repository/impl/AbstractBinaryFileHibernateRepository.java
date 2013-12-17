package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AbstractBinaryFile;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.AbstractBinaryFileRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("abstractBinaryFileRepository")
public class AbstractBinaryFileHibernateRepository extends GenericHibernateRepository<AbstractBinaryFile, Long> implements AbstractBinaryFileRepository {
    
}
