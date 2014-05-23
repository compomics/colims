
package com.compomics.colims.core.io;

import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.SearchEngineService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.identification.SearchParameters;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("searchSettingsMapper")
public class SearchSettingsMapper {
    
    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;
    @Autowired
    private SearchEngineService searchEngineService;
    
    public void map(SearchEngineType searchEngineType, String version, FastaDb fastaDb, SearchParameters searchParameters, File identificationFile){
        
    }
    
    private SearchEngine getSearchEngine()

}
