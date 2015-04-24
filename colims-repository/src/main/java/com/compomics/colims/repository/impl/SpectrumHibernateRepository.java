package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.SpectrumRepository;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("spectrumRepository")
@Transactional
public class SpectrumHibernateRepository extends GenericHibernateRepository<Spectrum, Long> implements SpectrumRepository {

    @Override
    public List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        String sql = "SELECT DISTINCT spectrum.id, MAX(%2$s) FROM spectrum " +
            "LEFT JOIN peptide ON peptide.l_spectrum_id = spectrum.id " +
            "LEFT JOIN peptide_has_protein ON peptide_has_protein.l_peptide_id = peptide.id " +
            "LEFT JOIN protein ON peptide_has_protein.l_protein_id = protein.id " +
            "LEFT JOIN protein_accession ON protein_accession.l_protein_id = protein.id " +
            "WHERE (spectrum.id LIKE '%1$s' " +
            "OR spectrum.title LIKE '%1$s' " +
            "OR protein_accession.accession LIKE '%1$s') " +
            "GROUP BY spectrum.id " +
            "ORDER BY MAX(%2$s) %3$s, spectrum.id " +
            "LIMIT %4$d " +
            "OFFSET %5$d";

        // does this guarantee ordering?
        SQLQuery query = getCurrentSession().createSQLQuery(String.format(sql, "%" + filter + "%", orderBy, direction, length, start))
            .addScalar("spectrum.id", LongType.INSTANCE);

        final List idList = query.list();

        List<Spectrum> returnList = createCriteria()
            .add(Restrictions.in("id", idList))
            .list();

        // sorting here because unable to pass order by list through criteria
        Collections.sort(returnList, new Comparator<Spectrum>() {
            public int compare(Spectrum s1, Spectrum s2) {
                return Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId()));
            }
        });

        return returnList;
    }

    public int getSpectraCount(AnalyticalRun analyticalRun) {
        return createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun)).list().size();
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
