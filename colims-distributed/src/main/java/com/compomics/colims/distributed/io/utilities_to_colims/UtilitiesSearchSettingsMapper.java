package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.SearchEngineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;

/**
 * This class maps the Utilities search settings to a SearchAndValidationSettings instance.
 *
 * @author Niels Hulstaert
 */
@Component("searchSettingsMapper")
public class UtilitiesSearchSettingsMapper {

    /**
     * The Utilities search parameters to Colims search parameters mapper.
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
     * @param fastaDbs                  the FastaDb instances
     * @param utilitiesSearchParameters the Utilities search parameters
     * @param storeIdentificationFile   store the identification or not
     * @return the mapped SearchAndValidationSettings
     * @throws IOException                  thrown in case of an I/O related exception
     * @throws ModificationMappingException in case of a modification mapping problem
     */
    public SearchAndValidationSettings map(SearchEngineType searchEngineType, String version, EnumMap<FastaDbType, FastaDb> fastaDbs, com.compomics.util.experiment.identification.identification_parameters.SearchParameters utilitiesSearchParameters, boolean storeIdentificationFile) throws IOException, ModificationMappingException {
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
        searchAndValidationSettings.setSearchParameters(searchParameters);

        /**
         * SearchEngine
         */
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(searchEngineType, version);
        //set entity associations
        searchAndValidationSettings.setSearchEngine(searchEngine);

        /**
         * FastaDbs
         */
        //set entity associations
        fastaDbs.forEach((k, v) -> {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, v);
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
        });


        return searchAndValidationSettings;
    }

}
