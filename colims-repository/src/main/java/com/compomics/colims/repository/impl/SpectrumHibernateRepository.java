package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.repository.SpectrumRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private static final String BASE_QUERY = "SELECT DISTINCT spectrum.id, MAX(%3$s) FROM spectrum"
            + " LEFT JOIN peptide ON peptide.l_spectrum_id = spectrum.id"
            + " LEFT JOIN peptide_has_protein_group ON peptide_has_protein_group.l_peptide_id = peptide.id"
            + " LEFT JOIN protein_group ON protein_group.id = peptide_has_protein_group.l_protein_group_id = protein_group.id"
            + " LEFT JOIN protein_group_has_protein ON protein_group_has_protein.l_protein_group_id = protein_group.id"
            + " WHERE (spectrum.id LIKE '%2$s'"
            + " OR peptide.peptide_sequence LIKE '%2$s'"
            + " OR protein_group_has_protein.protein_accession LIKE '%2$s')"
            + " AND spectrum.l_analytical_run_id = %1$d"
            + " GROUP BY spectrum.id ";

    @Override
    public List getPagedSpectra(final AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter) {
        List<Spectrum> spectra = new ArrayList<>();

        String extraParams = "ORDER BY MAX(%3$s) %4$s, spectrum.id "
                + "LIMIT %5$d "
                + "OFFSET %6$d";

        final List idList = getCurrentSession()
                .createSQLQuery(String.format(BASE_QUERY + extraParams, analyticalRun.getId(), "%" + filter + "%", orderBy, direction, length, start))
                .addScalar("spectrum.id", LongType.INSTANCE)
                .list();

        if (idList.size() > 0) {
            spectra = createCriteria().add(Restrictions.in("id", idList)).list();

            // sorting here because unable to pass order by list through criteria
            //noinspection ComparatorCombinators
            spectra.sort(Comparator.comparingLong(idList::indexOf));
        }

        return spectra;
    }

    @Override
    public int getSpectraCountForRun(final AnalyticalRun analyticalRun, final String orderBy, final String filter) {
        return getCurrentSession().createSQLQuery(String.format(BASE_QUERY, analyticalRun.getId(), "%" + filter + "%", orderBy))
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
    public Double getMinimumRetentionTime(final List<Long> analyticalRunIds) {
        Double minimumRetentionTime;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        minimumRetentionTime = (Double) criteria.setProjection(Projections.min("retentionTime")).uniqueResult();

        return minimumRetentionTime;
    }

    @Override
    public Double getMaximumRetentionTime(final List<Long> analyticalRunIds) {
        Double maximumRetentionTime;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        maximumRetentionTime = (Double) criteria.setProjection(Projections.max("retentionTime")).uniqueResult();

        return maximumRetentionTime;
    }

    @Override
    public Double getMinimumMzRatio(final List<Long> analyticalRunIds) {
        Double minimumMzRatio;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        minimumMzRatio = (Double) criteria.setProjection(Projections.min("mzRatio")).uniqueResult();

        return minimumMzRatio;
    }

    @Override
    public Double getMaximumMzRatio(final List<Long> analyticalRunIds) {
        Double maximumMzRatio;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        maximumMzRatio = (Double) criteria.setProjection(Projections.max("mzRatio")).uniqueResult();

        return maximumMzRatio;
    }

    @Override
    public Integer getMinimumCharge(final List<Long> analyticalRunIds) {
        Integer minimumCharge;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        minimumCharge = (Integer) criteria.setProjection(Projections.min("charge")).uniqueResult();

        return minimumCharge;
    }

    @Override
    public Integer getMaximumCharge(final List<Long> analyticalRunIds) {
        Integer maximumCharge;

        Criteria criteria = createCriteria().add(Restrictions.in("analyticalRun.id", analyticalRunIds));
        maximumCharge = (Integer) criteria.setProjection(Projections.max("charge")).uniqueResult();

        return maximumCharge;
    }

    @Override
    public Peptide getRepresentativePeptide(final Spectrum spectrum) {
        return (Peptide) getCurrentSession().createCriteria(Peptide.class)
                .add(Restrictions.eq("spectrum", spectrum))
                .uniqueResult();
    }

    @Override
    public Object[] getSpectraProjections(List<Long> analyticalRunIds) {
        Criteria criteria = getCurrentSession().createCriteria(Spectrum.class);

        criteria.add(Restrictions.in("analyticalRun.id", analyticalRunIds));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min("retentionTime"));
        projectionList.add(Projections.max("retentionTime"));
        projectionList.add(Projections.min("mzRatio"));
        projectionList.add(Projections.max("mzRatio"));
        projectionList.add(Projections.min("charge"));
        projectionList.add(Projections.max("charge"));
        criteria.setProjection(projectionList);

        return (Object[]) criteria.uniqueResult();
    }

    @Override
    public List<SpectrumFile> fetchSpectrumFiles(Long spectrumId) {
        Criteria criteria = getCurrentSession().createCriteria(SpectrumFile.class);

        criteria.add(Restrictions.eq("spectrum.id", spectrumId));

        return criteria.list();
    }

    @Override
    public void saveOrUpdate(Spectrum spectrum) {
        getCurrentSession().saveOrUpdate(spectrum);
    }
}
