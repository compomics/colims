package com.compomics.colims.distributed.io;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesPtmSettingsMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSearchParametersMapper;
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
     * The Utilities search parameters to Colims search parameters mapper.
     */
    @Autowired
    private UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;
    /**
     * The Utilities PtmSettings to Colims search modification parameters mapper.
     */
    @Autowired
    private UtilitiesPtmSettingsMapper utilitiesPtmSettingsMapper;
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
    public SearchAndValidationSettings map(SearchEngineType searchEngineType, String version, FastaDb fastaDb, com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters, List<File> identificationFiles, boolean storeIdentificationFile) throws IOException, ModificationMappingException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        /**
         * SearchParameterSettings
         */
        //map the Utilities SearchParameters onto the Colims SearchParameterSettings
        SearchParameters searchParameters = new SearchParameters();
        utilitiesSearchParametersMapper.map(utilitiesSearchParameters, searchParameters);

        //look for the given search parameter settings in the database
        searchParameters = searchAndValidationSettingsService.getSearchParameters(searchParameters);
        //set entity associations
        searchAndValidationSettings.setSearchParameterSettings(searchParameters);

        /**
         * SearchEngine
         */
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(searchEngineType, version);
        //set entity associations
        searchAndValidationSettings.setSearchEngine(searchEngine);

        /**
         * FastaDb
         */
        //set entity associations
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
            if (storeIdentificationFile) {
                identificationFileEntity.setBinaryFileType(binaryFileType);
                byte[] content = IOUtils.readAndZip(identificationFile);
                identificationFileEntity.setContent(content);
            }

            //set entity associations
            identificationFileEntity.setSearchAndValidationSettings(searchAndValidationSettings);
            searchAndValidationSettings.getIdentificationFiles().add(identificationFileEntity);
        }

        return searchAndValidationSettings;
    }

}
