package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchParametersService;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.repository.SearchParametersRepository;
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
            //fetch the search modifications
            List<SearchParametersHasModification> searchParametersHasModifications = searchParametersRepository.fetchSearchModifications(searchParameters.getId());
            searchParameters.setSearchParametersHasModifications(searchParametersHasModifications);
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
    public long countAll() {
        return searchParametersRepository.countAll();
    }

    @Override
    public void persist(SearchParameters entity) {
        searchParametersRepository.persist(entity);
    }

    @Override
    public SearchParameters merge(SearchParameters entity) {
        return searchParametersRepository.merge(entity);
    }

    @Override
    public void remove(SearchParameters entity) {
        searchParametersRepository.remove(entity);
    }
}
