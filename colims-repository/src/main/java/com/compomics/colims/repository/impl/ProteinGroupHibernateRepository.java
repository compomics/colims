package com.compomics.colims.repository.impl;

import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinGroupRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinGroupRepository")
public class ProteinGroupHibernateRepository extends GenericHibernateRepository<ProteinGroup, Long> implements ProteinGroupRepository {

}
