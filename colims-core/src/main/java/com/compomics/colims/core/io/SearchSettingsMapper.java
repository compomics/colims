package com.compomics.colims.core.io;

import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.BinaryFileType;
import com.compomics.colims.model.enums.SearchEngineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class maps the Utilities search settings to a SearchAndValidationSettings instance.
 *
 * @author Niels Hulstaert
 */
@Component("searchSettingsMapper")
public class SearchSettingsMapper {

    /**
     * The Utilities to Search parameters mapper.
     */
    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;
    /**
     *
     */
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    /**
     * Map the SearchAndValidationSettings.
     *
     * @param searchEngineType          the search engine type
     * @param version                   the search engine version
     * @param fastaDb                   the FastaDb instance
     * @param utilitiesSearchParameters the Utilities search parameters
     * @param identificationFiles       the list of identification files
     * @param storeIdentificationFile   store the identification or not
     * @return the mapped SearchAndValidationSettings
     * @throws java.io.IOException thrown in case of an I/O related exception
     */
    public SearchAndValidationSettings map(SearchEngineType searchEngineType, String version, FastaDb fastaDb, com.compomics.util.experiment.identification.SearchParameters utilitiesSearchParameters, List<File> identificationFiles, boolean storeIdentificationFile) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        /**
         * SearchParameterSettings
         */
        //map the Utilities SearchParameters onto the Colims SearchParameterSettings
        SearchParameters searchParameters = new SearchParameters();
        utilitiesSearchParametersMapper.map(utilitiesSearchParameters, searchParameters);

        //look for the given search parameter settings in the database
        searchParameters = searchAndValidationSettingsService.getSearchParameters(searchParameters);
        //set entity relations
        searchAndValidationSettings.setSearchParameterSettings(searchParameters);
//        searchParameterSettings.getSearchAndValidationSettingses().add(searchAndValidationSettings);

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
