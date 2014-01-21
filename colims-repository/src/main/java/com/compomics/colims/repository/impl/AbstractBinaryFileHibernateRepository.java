package com.compomics.colims.repository.impl;

import com.compomics.colims.model.BinaryFile;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.AbstractBinaryFileRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("abstractBinaryFileRepository")
public class AbstractBinaryFileHibernateRepository extends GenericHibernateRepository<BinaryFile, Long> implements AbstractBinaryFileRepository {
    
}
