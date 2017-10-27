package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import com.compomics.colims.repository.SearchEngineRepository;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("searchAndValidationSettingsService")
@Transactional
public class SearchAndValidationSettingsServiceImpl implements SearchAndValidationSettingsService {

    private final SearchAndValidationSettingsRepository searchAndValidationSettingsRepository;
    private final SearchEngineRepository searchEngineRepository;
    private final SearchParametersRepository searchParametersRepository;

    @Autowired
    public SearchAndValidationSettingsServiceImpl(SearchAndValidationSettingsRepository searchAndValidationSettingsRepository, SearchEngineRepository searchEngineRepository, SearchParametersRepository searchParametersRepository) {
        this.searchAndValidationSettingsRepository = searchAndValidationSettingsRepository;
        this.searchEngineRepository = searchEngineRepository;
        this.searchParametersRepository = searchParametersRepository;
    }

    @Override
    public SearchAndValidationSettings findById(final Long id) {
        return searchAndValidationSettingsRepository.findById(id);
    }

    @Override
    public List<SearchAndValidationSettings> findAll() {
        return searchAndValidationSettingsRepository.findAll();
    }

    @Override
    public long countAll() {
        return searchAndValidationSettingsRepository.countAll();
    }

    @Override
    public void persist(SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.persist(entity);
    }

    @Override
    public SearchAndValidationSettings merge(SearchAndValidationSettings entity) {
        return searchAndValidationSettingsRepository.merge(entity);
    }

    @Override
    public void remove(SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.remove(entity);
    }

    @Override
    public SearchEngine getSearchEngine(SearchEngineType searchEngineType, String version) {
        SearchEngine searchEngine = searchEngineRepository.findByTypeAndVersion(searchEngineType, version);

        if (searchEngine == null) {
            //check if the search engine can be found by type
            searchEngine = searchEngineRepository.findByType(searchEngineType);

            if (searchEngine != null) {
                //copy the found SearchEngine fields and the given version onto a new instance
                searchEngine = new SearchEngine(searchEngine, version);
            } else {
                //create a new instance with the type and version
                searchEngine = new SearchEngine(searchEngineType, version);
            }

            searchEngineRepository.persist(searchEngine);
        }

        return searchEngine;
    }

    @Override
    public SearchParameters getSearchParameters(SearchParameters searchParameters) {
        //find SearchParameters by example
        List<SearchParameters> searchParameterses = searchParametersRepository.findByExample(searchParameters);
        if (!searchParameterses.isEmpty()) {
            return searchParameterses.get(0);
        } else {
            //persist the given instance
            searchParametersRepository.saveOrUpdate(searchParameters);
            return searchParameters;
        }
    }

    @Override
    public void fetchSearchSettingsHasFastaDb(SearchAndValidationSettings searchAndValidationSettings) {
        try {
            searchAndValidationSettings.getSearchSettingsHasFastaDbs().size();
        } catch (LazyInitializationException e) {
            // merge the searchAndValidationSettings
            SearchAndValidationSettings merge = searchAndValidationSettingsRepository.merge(searchAndValidationSettings);
            merge.getSearchSettingsHasFastaDbs().size();
            searchAndValidationSettings.setSearchSettingsHasFastaDbs(merge.getSearchSettingsHasFastaDbs());
        }
    }

    @Override
    public SearchAndValidationSettings getByAnalyticalRun(AnalyticalRun analyticalRun) {
        return searchAndValidationSettingsRepository.findbyAnalyticalRunId(analyticalRun.getId());
    }

}
