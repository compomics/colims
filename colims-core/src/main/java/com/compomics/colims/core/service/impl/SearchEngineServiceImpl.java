package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchEngineService;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.repository.SearchEngineRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("searchEngineService")
public class SearchEngineServiceImpl implements SearchEngineService {

    @Autowired
    private SearchEngineRepository searchEngineRepository;

    @Override
    public SearchEngine findByNameAndVersion(String name, String version) {
        return searchEngineRepository.findByNameAndVersion(name, version);
    }

    @Override
    public SearchEngine findById(Long id) {
        return searchEngineRepository.findById(id);
    }

    @Override
    public List<SearchEngine> findAll() {
        return searchEngineRepository.findAll();
    }

    @Override
    public void save(SearchEngine entity) {
        searchEngineRepository.save(entity);
    }

    @Override
    public void update(SearchEngine entity) {
        searchEngineRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(SearchEngine entity) {
        searchEngineRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(SearchEngine entity) {
        searchEngineRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return searchEngineRepository.countAll();
    }

}
