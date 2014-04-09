package com.compomics.colims.repository.impl;

import com.compomics.colims.model.BinaryFile;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.BinaryFileRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("abstractBinaryFileRepository")
public class BinaryFileHibernateRepository extends GenericHibernateRepository<BinaryFile, Long> implements BinaryFileRepository {
    
}
