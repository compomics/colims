package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Material;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SampleBinaryFile;
import com.compomics.colims.repository.SampleRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
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

    @Override
    public List<SampleBinaryFile> fetchBinaryFiles(Long sampleId) {
        Criteria criteria = getCurrentSession().createCriteria(SampleBinaryFile.class);

        criteria.add(Restrictions.eq("sample.id", sampleId));

        return criteria.list();
    }

    @Override
    public List<Material> fetchMaterials(Long sampleId) {
        Criteria criteria = getCurrentSession().createCriteria(Material.class);

        criteria.createAlias("samples", "sample");
        criteria.add(Restrictions.eq("sample.id", sampleId));

        return criteria.list();
    }

    @Override
    public Sample findByIdWithFetchedRuns(Long sampleId) {
        Query query = getCurrentSession().getNamedQuery("Sample.findByIdWithFetchedRuns");

        query.setLong("sampleId", sampleId);

        return (Sample) query.uniqueResult();
    }

    @Override
    public Object[] getParentIds(Long sampleId) {
        Query query = getCurrentSession().getNamedQuery("Sample.getParentIds");

        query.setLong("sampleId", sampleId);

        return (Object[]) query.uniqueResult();
    }
}
