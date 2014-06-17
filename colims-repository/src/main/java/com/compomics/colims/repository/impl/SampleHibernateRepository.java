package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.SampleRepository;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("sampleRepository")
public class SampleHibernateRepository extends GenericHibernateRepository<Sample, Long> implements SampleRepository {
    
    @Override
    public Protocol getMostUsedProtocol() {
        Criteria criteria = createCriteria();

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.rowCount(), "sampleCountByProtocol");
        projectionList.add(Projections.groupProperty("protocol"));

        //get results
        List criteriaResults = criteria.setProjection(projectionList).addOrder(Order.desc("sampleCountByProtocol")).list();

        Protocol protocol = null;
        if (!criteriaResults.isEmpty()) {
            protocol = (Protocol) ((Object[]) criteriaResults.get(0))[1];
        }

        return protocol;
    }
}
