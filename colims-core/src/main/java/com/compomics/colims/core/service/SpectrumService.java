package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This interface provides service methods for the Spectrum class.
 *
 * @author Niels Hulstaert
 */
public interface SpectrumService extends GenericService<Spectrum, Long> {

    /**
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) from the SpectrumFile.
     *
     * @param spectrumFile the SpectrumFile
     * @return the peak map
     * @throws java.io.IOException the IOException
     */
    Map<Double, Double> getSpectrumPeaks(SpectrumFile spectrumFile) throws IOException;

    /**
     * Count the spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the number of spectra
     */
    Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum retention time of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum retention time value
     */
    Double getMinimumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum retention time of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum retention time value
     */
    Double getMaximumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum M/Z ratio of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum M/Z ratio value
     */
    Double getMinimumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum M/Z ratio of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum M/Z ratio value
     */
    Double getMaximumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum charge of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum charge value
     */
    Integer getMinimumCharge(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum charge of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum charge value
     */
    Integer getMaximumCharge(final AnalyticalRun analyticalRun);

    /**
     * Get the spectra projections for the given run (Min and max retention time values, min en max M/Z values, min and
     * max charge values).
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the spectra projection values for the given run
     */
    Object[] getSpectraProjections(final AnalyticalRun analyticalRun);

    /**
     * Fetch the spectrum spectrumFiles.
     *
     * @param spectrum the Spectrum instance
     */
    void fetchSpectrumFiles(Spectrum spectrum);

    /**
     * Return a list of spectra according to all these parameters.
     *
     * @param analyticalRun Analytical run with which spectra are associated
     * @param start         Start point in results (SQL OFFSET)
     * @param length        Length of result list (SQL LIMIT)
     * @param orderBy       Column to order by (SQL ORDER BY [column]
     * @param direction     Ordering direction (SQL ORDER BY [dir])
     * @param filter        Filter string (SQL LIKE %[filter]%)
     * @return List of spectra
     */
    List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter);

    /**
     * Count the spectra for a given run with optional filtering.
     *
     * @param analyticalRun Analytical run instance
     * @param orderBy       Ordering parameter
     * @param filter        Filter string (or empty string)
     * @return Row count
     */
    int getSpectraCountForRun(AnalyticalRun analyticalRun, String orderBy, String filter);

    Peptide getRepresentativePeptide(final Spectrum spectrum);
}
