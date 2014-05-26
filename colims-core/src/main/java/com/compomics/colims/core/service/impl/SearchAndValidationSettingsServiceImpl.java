package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import com.compomics.colims.repository.SearchEngineRepository;
import com.compomics.colims.repository.SearchParameterSettingsRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Service("searchAndValidationSettingsService")
@Transactional
public class SearchAndValidationSettingsServiceImpl implements SearchAndValidationSettingsService {

    private static final Logger LOGGER = Logger.getLogger(SearchAndValidationSettingsServiceImpl.class);

    @Autowired
    private SearchAndValidationSettingsRepository searchAndValidationSettingsRepository;
    @Autowired
    private SearchEngineRepository searchEngineRepository;
    @Autowired
    private SearchParameterSettingsRepository searchParameterSettingsRepository;

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
        SearchEngine searchEngine = searchEngineRepository.findByNameAndVersion(searchEngineType, version);

        if (searchEngine == null) {
            searchEngine = new SearchEngine(searchEngineType, version);
            searchEngineRepository.save(searchEngine);
        }

        return searchEngine;
    }

    @Override
    public SearchParameterSettings getSearchParamterSettings(SearchParameterSettings searchParameterSettings) {
        //find SearchParameterSettings by example
        List<SearchParameterSettings> searchParameterSettingses = searchParameterSettingsRepository.findByExample(searchParameterSettings);
        if (!searchParameterSettingses.isEmpty()) {
            return searchParameterSettingses.get(0);
        } else {
            //save the given instance
            searchParameterSettingsRepository.save(searchParameterSettings);
            return searchParameterSettings;
        }
    }

}
