package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import com.compomics.colims.repository.SearchEngineRepository;
import com.compomics.colims.repository.SearchParametersRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Niels Hulstaert
 */
@Service("searchAndValidationSettingsService")
@Transactional
public class SearchAndValidationSettingsServiceImpl implements SearchAndValidationSettingsService {

    @Autowired
    private SearchAndValidationSettingsRepository searchAndValidationSettingsRepository;
    @Autowired
    private SearchEngineRepository searchEngineRepository;
    @Autowired
    private SearchParametersRepository searchParametersRepository;
    @Autowired
    private OlsService olsService;

    @Override
    public SearchAndValidationSettings findById(final Long id) {
        return searchAndValidationSettingsRepository.findById(id);
    }

    @Override
    public List<SearchAndValidationSettings> findAll() {
        return searchAndValidationSettingsRepository.findAll();
    }

    @Override
    public void save(final SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.save(entity);
    }

    @Override
    public void update(final SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final SearchAndValidationSettings entity) {
        searchAndValidationSettingsRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return searchAndValidationSettingsRepository.countAll();
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

            searchEngineRepository.save(searchEngine);
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
            //save the given instance
            searchParametersRepository.save(searchParameters);
            return searchParameters;
        }
    }

}
