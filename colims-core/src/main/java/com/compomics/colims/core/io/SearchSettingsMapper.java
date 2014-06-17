package com.compomics.colims.core.io;

import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.util.IOUtils;
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
@Component("searchSettingsMapper")
public class SearchSettingsMapper {

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
        searchParameterSettings = searchAndValidationSettingsService.getSearchParamterSettings(searchParameterSettings);
        //set entity relations
        searchAndValidationSettings.setSearchParameterSettings(searchParameterSettings);
//        searchParamterSettings.getSearchAndValidationSettingses().add(searchAndValidationSettings);

        /**
         * SearchEngine
         */
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(searchEngineType, version);
        //set entity relations
        searchAndValidationSettings.setSearchEngine(searchEngine);
//        searchEngine.getSearchAndValidationSettingses().add(searchAndValidationSettings);

        /**
         * FastaDb
         */
        //set entity relations
        searchAndValidationSettings.setFastaDb(fastaDb);
//        fastaDb.getSearchAndValidationSettingses().add(searchAndValidationSettings);

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
            if (storeIdentificationFile) {
                identificationFileEntity.setBinaryFileType(binaryFileType);
                byte[] content = IOUtils.readAndZip(identificationFile);
                identificationFileEntity.setContent(content);
            }

            //set entity relations
            identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
            searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);
        }

        return searchAndValidationSettings;
    }

}
