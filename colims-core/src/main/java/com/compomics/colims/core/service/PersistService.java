package com.compomics.colims.core.service;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;

import java.util.Date;

/**
 * This interface provides service methods for storing one or more analytical runs.
 *
 * @author Niels Hulstaert
 */
public interface PersistService {

    /**
     * Persist the AnalyticalRuns, SearchAndValidationSettings and QuantificationSettings.
     *
     * @param mappedData the mapped data (runs and protein groups)
     * @param sample     the sample the runs will be added to
     * @param instrument the instrument the runs were executed on
     * @param userName   the user name
     * @param startDate  the start date of the runs
     */
    void persist(MappedData mappedData, Sample sample, Instrument instrument, String userName, Date startDate);

}
