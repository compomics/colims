package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;

import com.compomics.colims.model.Peptide;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
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

    /**
     * Query string for paging method, alter at your peril.
     */
    private final String baseQuery = "SELECT DISTINCT spectrum.id, MAX(%2$s) FROM spectrum "
            + "LEFT JOIN peptide ON peptide.l_spectrum_id = spectrum.id "
            + "LEFT JOIN peptide_has_protein ON peptide_has_protein.l_peptide_id = peptide.id "
            + "LEFT JOIN protein ON peptide_has_protein.l_protein_id = protein.id "
            + "LEFT JOIN protein_accession ON protein_accession.l_protein_id = protein.id "
            + "WHERE (spectrum.id LIKE '%1$s' "
            + "OR peptide.peptide_sequence LIKE '%1$s' "
            + "OR protein_accession.accession LIKE '%1$s') "
            + "GROUP BY spectrum.id ";

    @Override
    public List getPagedSpectra(final AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter) {
        String extraParams = "ORDER BY MAX(%2$s) %3$s, spectrum.id "
                + "LIMIT %4$d "
                + "OFFSET %5$d";

        SQLQuery query = getCurrentSession().createSQLQuery(String.format(baseQuery + extraParams, "%" + filter + "%", orderBy, direction, length, start))
                .addScalar("spectrum.id", LongType.INSTANCE);

        final List idList = query.list();
        List<Spectrum> returnList = new ArrayList<>();

        if (idList.size() > 0) {
            returnList = createCriteria()
                    .add(Restrictions.in("id", idList))
                    .list();

            // sorting here because unable to pass order by list through criteria
            Collections.sort(returnList, (s1, s2) -> Long.compare(idList.indexOf(s1.getId()), idList.indexOf(s2.getId())));
        }

        return returnList;
    }

    @Override
    public int getSpectraCountForRun(final AnalyticalRun analyticalRun, final String orderBy, final String filter) {
        return getCurrentSession().createSQLQuery(String.format(baseQuery, "%" + filter + "%", orderBy))
                .list().size();
    }

    @Override
    public Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun) {
        Long numberOfSpectra;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        numberOfSpectra = ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();

        return numberOfSpectra;
    }

    @Override
    public List<Spectrum> getSpectraForPeptide(final Peptide peptide) {
        return createCriteria(Restrictions.eq("id", peptide)).list();
    }

    @Override
    public Double getMinimumRetentionTime(final AnalyticalRun analyticalRun) {
        Double minimumRetentionTime;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumRetentionTime = (Double) criteria.setProjection(Projections.min("retentionTime")).uniqueResult();

        return minimumRetentionTime;
    }

    @Override
    public Double getMaximumRetentionTime(final AnalyticalRun analyticalRun) {
        Double maximumRetentionTime;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumRetentionTime = (Double) criteria.setProjection(Projections.max("retentionTime")).uniqueResult();

        return maximumRetentionTime;
    }

    @Override
    public Double getMinimumMzRatio(final AnalyticalRun analyticalRun) {
        Double minimumMzRatio;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumMzRatio = (Double) criteria.setProjection(Projections.min("mzRatio")).uniqueResult();

        return minimumMzRatio;
    }

    @Override
    public Double getMaximumMzRatio(final AnalyticalRun analyticalRun) {
        Double maximumMzRatio;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumMzRatio = (Double) criteria.setProjection(Projections.max("mzRatio")).uniqueResult();

        return maximumMzRatio;
    }

    @Override
    public Integer getMinimumCharge(final AnalyticalRun analyticalRun) {
        Integer minimumCharge;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        minimumCharge = (Integer) criteria.setProjection(Projections.min("charge")).uniqueResult();

        return minimumCharge;
    }

    @Override
    public Integer getMaximumCharge(final AnalyticalRun analyticalRun) {
        Integer maximumCharge;

        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        maximumCharge = (Integer) criteria.setProjection(Projections.max("charge")).uniqueResult();

        return maximumCharge;
    }
}
