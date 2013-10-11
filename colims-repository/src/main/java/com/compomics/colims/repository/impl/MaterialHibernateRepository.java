package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Material;
import com.compomics.colims.repository.MaterialRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("materialRepository")
public class MaterialHibernateRepository extends GenericHibernateRepository<Material, Long> implements MaterialRepository {
    
    @Override
    public Material findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }    
    
}
