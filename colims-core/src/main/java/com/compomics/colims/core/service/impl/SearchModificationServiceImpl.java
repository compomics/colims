package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ModificationService;
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

    @Autowired
    private SearchModificationRepository searchModificationRepository;

    @Override
    public SearchModification findById(final Long id) {
        return searchModificationRepository.findById(id);
    }

    @Override
    public List<SearchModification> findAll() {
        return searchModificationRepository.findAll();
    }

    @Override
    public void save(final SearchModification entity) {
        searchModificationRepository.save(entity);
    }

    @Override
    public void update(final SearchModification entity) {
        searchModificationRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final SearchModification entity) {
        searchModificationRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final SearchModification entity) {
        searchModificationRepository.delete(entity);
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
    public SearchModification findByAlternativeAccession(String alternativeAccession) {
        return searchModificationRepository.findByAlternativeAccession(alternativeAccession);
    }

}
