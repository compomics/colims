package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.repository.ProtocolRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("protocolRepository")
public class ProtocolHibernateRepository extends GenericHibernateRepository<Protocol, Long> implements ProtocolRepository {
    
    @Override
    public Protocol findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }    
    
}
