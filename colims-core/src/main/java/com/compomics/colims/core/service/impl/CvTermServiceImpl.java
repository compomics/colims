package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.model.CvTerm;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.enums.CvTermProperty;
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
    public CvTerm findByAccession(String accession, CvTermProperty cvTermProperty) {
        return cvTermRepository.findByAccession(accession, cvTermProperty);
    }

    @Override
    public List<CvTerm> findByCvTermByProperty(CvTermProperty cvTermProperty) {
        return cvTermRepository.findByCvTermProperty(cvTermProperty);
    }

    @Override
    public <T extends CvTerm> List<T> findByCvTermByProperty(Class<T> clazz, CvTermProperty cvTermProperty) {
        List<T> cvTerms = new ArrayList<>();
        
        for(CvTerm cvTerm : cvTermRepository.findByCvTermProperty(cvTermProperty)){
            if(clazz.isInstance(cvTerm)){
                cvTerms.add((T) cvTerm);
            }
        }
        
        return cvTerms;
    }
    
}
