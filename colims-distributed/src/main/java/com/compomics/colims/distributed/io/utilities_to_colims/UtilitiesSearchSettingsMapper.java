package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.ScoreType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.preferences.IdentificationParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.EnumMap;

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
    private final UtilitiesSearchParametersMapper utilitiesSearchParametersMapper;
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;

    @Autowired
    public UtilitiesSearchSettingsMapper(UtilitiesSearchParametersMapper utilitiesSearchParametersMapper, SearchAndValidationSettingsService searchAndValidationSettingsService) {
        this.utilitiesSearchParametersMapper = utilitiesSearchParametersMapper;
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
    }

    /**
     * Map the SearchAndValidationSettings.
     *
     * @param searchEngineType         the search engine type
     * @param version                  the search engine version
     * @param fastaDbs                 the FastaDb instances
     * @param identificationParameters the Utilities identification parameters
     * @param proteinScoreType         the protein target-decoy scoring strategy
     * @param proteinThreshold         the protein score threshold
     * @param storeIdentificationFile  store the identification or not
     * @return the mapped SearchAndValidationSettings
     * @throws IOException thrown in case of an I/O related exception
     */
    public SearchAndValidationSettings map(final SearchEngineType searchEngineType, final String version, final EnumMap<FastaDbType, FastaDb> fastaDbs, final IdentificationParameters identificationParameters, final ScoreType proteinScoreType, final Double proteinThreshold, boolean storeIdentificationFile) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //SearchParameterSettings
        //map the Utilities SearchParameters onto the Colims SearchParameterSettings
        SearchParameters searchParameters = new SearchParameters();
        utilitiesSearchParametersMapper.map(identificationParameters, searchParameters);
        //set the protein score type and threshold value
        searchParameters.setScoreType(proteinScoreType);
        searchParameters.setProteinThreshold(proteinThreshold);

        //look for the given search parameter settings in the database
        searchParameters = searchAndValidationSettingsService.getSearchParameters(searchParameters);
        //set entity associations
        searchAndValidationSettings.setSearchParameters(searchParameters);

        //SearchEngine
        SearchEngine searchEngine = searchAndValidationSettingsService.getSearchEngine(searchEngineType, version);
        //set entity associations
        searchAndValidationSettings.setSearchEngine(searchEngine);

        //FastaDbs
        //set entity associations
        fastaDbs.forEach((k, v) -> {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, v);
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
        });

        return searchAndValidationSettings;
    }

}
