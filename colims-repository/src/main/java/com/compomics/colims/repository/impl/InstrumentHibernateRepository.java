package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("instrumentRepository")
public class InstrumentHibernateRepository extends GenericHibernateRepository<Instrument, Long> implements InstrumentRepository {

    @Override
    public Instrument findByName(String name) {
        Query namedQuery = getCurrentSession().getNamedQuery("Instrument.findByName");
        namedQuery.setParameter("name", name);
        List<Instrument> resultList = namedQuery.list();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
