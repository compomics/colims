package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.SpectrumRepository;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("spectrumRepository")
@Transactional
public class SpectrumHibernateRepository extends GenericHibernateRepository<Spectrum, Long> implements SpectrumRepository {

    @Override
    public List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        Criteria criteria = createCriteria()
            .add(Restrictions.eq("analyticalRun", analyticalRun))
            .setFirstResult(start)
            .setMaxResults(length);

        if (direction.equals("asc")) {
            criteria.addOrder(Order.asc(orderBy));
        } else {
            criteria.addOrder(Order.desc(orderBy));
        }

        if (filter != null) {
            String searchTerm = "%" + filter + "%";

            criteria.createAlias("peptides", "peptide")
                .createAlias("peptide.peptideHasProteins", "peptideHasProtein")
                .createAlias("peptideHasProtein.protein", "protein")
                .createAlias("protein.proteinAccessions", "accession")
                .add(Restrictions.disjunction()
                    .add(Restrictions.like("accession", searchTerm))
                    .add(Restrictions.like("title", searchTerm))
                    .add(Restrictions.like("peptide.sequence", searchTerm))
                    .add(Restrictions.like("accession.accession", searchTerm))
                );
        }

        return criteria.list();
    }

    @Override
    public Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun) {
        Long numberOfSpectra = 0L;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        numberOfSpectra = ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();

        return numberOfSpectra;
    }

    @Override
    public Double getMinimumRetentionTime(final AnalyticalRun analyticalRun) {
        Double minimumRetentionTime = 0.0;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumRetentionTime = (Double) criteria.setProjection(Projections.min("retentionTime")).uniqueResult();

        return minimumRetentionTime;
    }

    @Override
    public Double getMaximumRetentionTime(final AnalyticalRun analyticalRun) {
        Double maximumRetentionTime = 0.0;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumRetentionTime = (Double) criteria.setProjection(Projections.max("retentionTime")).uniqueResult();

        return maximumRetentionTime;
    }

    @Override
    public Double getMinimumMzRatio(final AnalyticalRun analyticalRun) {
        Double minimumMzRatio = 0.0;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumMzRatio = (Double) criteria.setProjection(Projections.min("mzRatio")).uniqueResult();

        return minimumMzRatio;
    }

    @Override
    public Double getMaximumMzRatio(final AnalyticalRun analyticalRun) {
        Double maximumMzRatio = 0.0;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumMzRatio = (Double) criteria.setProjection(Projections.max("mzRatio")).uniqueResult();

        return maximumMzRatio;
    }

    @Override
    public Integer getMinimumCharge(final AnalyticalRun analyticalRun) {
        Integer minimumCharge = 1;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumCharge = (Integer) criteria.setProjection(Projections.min("charge")).uniqueResult();

        return minimumCharge;
    }

    @Override
    public Integer getMaximumCharge(final AnalyticalRun analyticalRun) {
        Integer maximumCharge = 1;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumCharge = (Integer) criteria.setProjection(Projections.max("charge")).uniqueResult();

        return maximumCharge;
    }
}
