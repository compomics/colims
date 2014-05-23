
package com.compomics.colims.core.io;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface DataImporter {
    
    SearchAndValidationSettings importSearchSettings(DataImport dataImport);
    
    QuantificationSettings importQuantSettings();
    
    List<AnalyticalRun> importInputAndResults(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings);
    
}
