package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("modificationRepository")
public class ModificationHibernateRepository extends GenericHibernateRepository<Modification, Long> implements ModificationRepository {

    @Override
    public Modification findByName(String name) {
        Query namedQuery = getCurrentSession().getNamedQuery("Modification.findByName");
        namedQuery.setParameter("name", name);
        List<Modification> resultList = namedQuery.list();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
