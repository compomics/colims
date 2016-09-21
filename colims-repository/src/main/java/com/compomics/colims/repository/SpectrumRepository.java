package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;

import java.util.List;

/**
 * This interface provides repository methods for the Spectrum class.
 *
 * @author Niels Hulstaert
 */
public interface SpectrumRepository extends GenericRepository<Spectrum, Long> {

    /**
     * Return a list of spectra according to all these parameters.
     *
     * @param analyticalRun the analytical run with which spectra are associated
     * @param start the start point in results (SQL OFFSET)
     * @param length the length of result list (SQL LIMIT)
     * @param orderBy the column to order by (SQL ORDER BY [column]
     * @param direction the ordering direction (SQL ORDER BY [dir])
     * @param filter the filter string (SQL LIKE %[filter]%)
     * @return List of spectra
     */
    List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter);

    /**
     * Count the spectra for a given run with optional filtering.
     *
     * @param analyticalRun the analytical run instance
     * @param orderBy the ordering parameter
     * @param filter the filter string (or empty string)
     * @return Row count
     */
    int getSpectraCountForRun(AnalyticalRun analyticalRun, String orderBy, String filter);

    /**
     * Count the spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the number of spectra
     */
    Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum retention time of spectra associated to the given
     * analytical runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the minimum retention time value
     */
    Double getMinimumRetentionTime(final List<Long> analyticalRunIds);

    /**
     * Get the maximum retention time of spectra associated to the given
     * analytical runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the maximum retention time value
     */
    Double getMaximumRetentionTime(final List<Long> analyticalRunIds);

    /**
     * Get the minimum M/Z ratio of spectra associated to the given analytical
     * runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the minimum M/Z ratio value
     */
    Double getMinimumMzRatio(final List<Long> analyticalRunIds);

    /**
     * Get the maximum M/Z ratio of spectra associated to the given analytical
     * runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the maximum M/Z ratio value
     */
    Double getMaximumMzRatio(final List<Long> analyticalRunIds);

    /**
     * Get the minimum charge of spectra associated to the given analytical runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the minimum charge value
     */
    Integer getMinimumCharge(final List<Long> analyticalRunIds);

    /**
     * Get the maximum charge of spectra associated to the given analytical runs.
     *
     * @param analyticalRunIds the list of run IDs
     * @return the maximum charge value
     */
    Integer getMaximumCharge(final List<Long> analyticalRunIds);

    Peptide getRepresentativePeptide(final Spectrum spectrum);

    /**
     * Get the spectra projections for the given runs (Min and max retention
     * time values, min en max M/Z values, min and max charge values).
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the spectra projection values for the given run
     */
    Object[] getSpectraProjections(final List<Long> analyticalRunIds);

    /**
     * Fetch the spectrum files for the given spectrum.
     *
     * @param spectrumId the spectrum id
     * @return the associated spectrum files
     */
    List<SpectrumFile> fetchSpectrumFiles(Long spectrumId);
    
    /**
     * Cascade save or update the given spectrum. We don't use the JPA merge method because of consistency with
     * saveOrUpdate the protein groups in the PersistService.
     *
     * @param spectrum the spectrum instance to save or update
     */
    void saveOrUpdate(final Spectrum spectrum);

}
