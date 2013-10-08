package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.model.CvTerm;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.repository.CvTermRepository;
import java.util.ArrayList;
import org.hibernate.LockOptions;

/**
 *
 * @author Niels Hulstaert
 */
@Service("cvTermService")
@Transactional
public class CvTermServiceImpl implements CvTermService {
    
    @Autowired
    private CvTermRepository cvTermRepository;

    @Override
    public CvTerm findById(Long id) {
        return cvTermRepository.findById(id);
    }

    @Override
    public List<CvTerm> findAll() {
        return cvTermRepository.findAll();
    }

    @Override
    public void save(CvTerm entity) {
        cvTermRepository.save(entity);
    }

    @Override
    public void update(CvTerm entity) {
        //attach the CV term to the new session
        cvTermRepository.lock(entity, LockOptions.NONE);
        cvTermRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(CvTerm entity) {
        cvTermRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(CvTerm entity) {
        cvTermRepository.delete(entity);
    }        

    @Override
    public CvTerm findByAccession(String accession, CvTermType cvTermType) {
        return cvTermRepository.findByAccession(accession, cvTermType);
    }

    @Override
    public List<CvTerm> findByCvTermByType(CvTermType cvTermType) {
        return cvTermRepository.findByCvTermType(cvTermType);
    }

    @Override
    public <T extends CvTerm> List<T> findByCvTermByType(Class<T> clazz, CvTermType cvTermType) {
        List<T> cvTerms = new ArrayList<>();
        
        for(CvTerm cvTerm : cvTermRepository.findByCvTermType(cvTermType)){
            if(clazz.isInstance(cvTerm)){
                cvTerms.add((T) cvTerm);
            }
        }
        
        return cvTerms;
    }
    
}
