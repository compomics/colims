package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SearchModificationService;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.repository.SearchModificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("searchModificationService")
@Transactional
public class SearchModificationServiceImpl implements SearchModificationService {

    private final SearchModificationRepository searchModificationRepository;

    @Autowired
    public SearchModificationServiceImpl(SearchModificationRepository searchModificationRepository) {
        this.searchModificationRepository = searchModificationRepository;
    }

    @Override
    public SearchModification findById(final Long id) {
        return searchModificationRepository.findById(id);
    }

    @Override
    public List<SearchModification> findAll() {
        return searchModificationRepository.findAll();
    }

    @Override
    public SearchModification findByName(final String name) {
        return searchModificationRepository.findByName(name);
    }

    @Override
    public SearchModification findByAccession(final String accession) {
        return searchModificationRepository.findByAccession(accession);
    }

    @Override
    public long countAll() {
        return searchModificationRepository.countAll();
    }

    @Override
    public void persist(SearchModification entity) {
        searchModificationRepository.persist(entity);
    }

    @Override
    public SearchModification merge(SearchModification entity) {
        return searchModificationRepository.merge(entity);
    }

    @Override
    public void remove(SearchModification entity) {
        searchModificationRepository.remove(entity);
    }

}
