package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.PeptideRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("peptideRepository")
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {
    
}
