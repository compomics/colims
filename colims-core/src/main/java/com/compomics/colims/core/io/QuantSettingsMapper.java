package com.compomics.colims.core.io;

import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.model.enums.BinaryFileType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.identification.SearchParameters;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("quantSettingsMapper")
public class QuantSettingsMapper {

    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    /**
     * Map the SearchAndValidationSettings.
     *
     * @param searchEngineType
     * @param version
     * @param fastaDb
     * @param searchParameters
     * @param identificationFiles
     * @param storeIdentificationFile
     * @return the mapped SearchAndValidationSettings
     * @throws java.io.IOException
     */
    public SearchAndValidationSettings map(SearchEngineType searchEngineType, String version, FastaDb fastaDb, SearchParameters searchParameters, List<File> identificationFiles, boolean storeIdentificationFile) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        /**
         * SearchParamterSettings
         */
        //map the utitilities SearchParamaters onto the colims SearchParameterSettings
        SearchParameterSettings searchParameterSettings = new SearchParameterSettings();
        utilitiesSearchParametersMapper.map(searchParameters, searchParameterSettings);

        //look for the given search parameter settings in the database
        SearchParameterSettings searchParamterSettings = searchAndValidationSettingsService.getSearchParamterSettings(searchParameterSettings);
        searchAndValidationSettings.setSearchParameterSettings(searchParameterSettings);

        /**
         * SearchEngine
         */
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(searchEngineType, version);
        searchAndValidationSettings.setSearchEngine(searchEngine);

        /**
         * FastaDb
         */
        searchAndValidationSettings.setFastaDb(fastaDb);

        /**
         * IdentificationFile(s)
         */
        BinaryFileType binaryFileType = null;
        if (storeIdentificationFile) {            
            switch (searchEngineType) {
                case PEPTIDESHAKER:
                    binaryFileType = BinaryFileType.TEXT;
                    break;
                case MAX_QUANT:
                    binaryFileType = BinaryFileType.ZIP;
                    break;
                default:
                    break;
            }
        }

        for (File identificationFile : identificationFiles) {
            IdentificationFile identificationFileEntity = new IdentificationFile(identificationFile.getName(), identificationFile.getCanonicalPath());
            if(binaryFileType != null){
                identificationFileEntity.setBinaryFileType(binaryFileType);
            }
            identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
            searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);
        }

        return searchAndValidationSettings;
    }

}
