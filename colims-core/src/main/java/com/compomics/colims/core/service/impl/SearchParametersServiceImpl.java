package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchParametersService;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("searchParametersService")
@Transactional
public class SearchParametersServiceImpl implements SearchParametersService {

    @Autowired
    private SearchParametersRepository searchParametersRepository;

    @Override
    public void fetchSearchModifications(SearchParameters searchParameters) {
        try {
            searchParameters.getSearchParametersHasModifications().size();
        } catch (LazyInitializationException e) {
            //attach the protein to the new session
            searchParametersRepository.saveOrUpdate(searchParameters);
            if (!Hibernate.isInitialized(searchParameters.getSearchParametersHasModifications())) {
                Hibernate.initialize(searchParameters.getSearchParametersHasModifications());
            }
        }
    }

    @Override
    public SearchParameters findById(Long aLong) {
        return searchParametersRepository.findById(aLong);
    }

    @Override
    public List<SearchParameters> findAll() {
        return searchParametersRepository.findAll();
    }

    @Override
    public void save(SearchParameters entity) {
        searchParametersRepository.save(entity);
    }

    @Override
    public void update(SearchParameters entity) {
        searchParametersRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(SearchParameters entity) {
        searchParametersRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(SearchParameters entity) {
        searchParametersRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return searchParametersRepository.countAll();
    }
}
