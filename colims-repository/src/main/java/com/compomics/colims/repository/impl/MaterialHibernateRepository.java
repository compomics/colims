package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Material;
import com.compomics.colims.repository.MaterialRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("materialRepository")
public class MaterialHibernateRepository extends GenericHibernateRepository<Material, Long> implements MaterialRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Material> findAllOrderedByName() {
        return createCriteria().addOrder(Order.asc("name")).list();
    }

}
