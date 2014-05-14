package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Institution;
import com.compomics.colims.repository.InstitutionRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("institutionRepository")
public class InstitutionHibernateRepository extends GenericHibernateRepository<Institution, Long> implements InstitutionRepository {               
    
}
