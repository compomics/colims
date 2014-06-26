package com.compomics.colims.core.service;

import com.compomics.colims.core.io.MappedDataImport;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import java.util.Date;

/**
 *
 * @author Niels Hulstaert
 */
public interface DataStorageService {
    
    /**
     * Store the AnalyticalRuns, SearchAndValidationSettings and QuantificationSettings.
     * 
     * @param mappedDataImport AnalyticalRuns, SearchAndValidationSettings and QuantificationSettings
     * @param sample the sample the runs will be added to
     * @param instrument the instrument the runs were executed on
     * @param userName the user name
     * @param startDate the start date of the runs
     */
    void storeMappedData(MappedDataImport mappedDataImport, Sample sample, Instrument instrument, String userName, Date startDate);
    
}
