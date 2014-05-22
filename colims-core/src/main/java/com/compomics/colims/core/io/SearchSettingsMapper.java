
package com.compomics.colims.core.io;

import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
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
    
    

}
