package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.model.AuditableTypedCvTerm;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.repository.CvTermRepository;
import java.util.ArrayList;

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
    public AuditableTypedCvTerm findById(Long id) {
        return cvTermRepository.findById(id);
    }

    @Override
    public List<AuditableTypedCvTerm> findAll() {
        return cvTermRepository.findAll();
    }

    @Override
    public void save(AuditableTypedCvTerm entity) {
        cvTermRepository.save(entity);
    }

    @Override
    public void update(AuditableTypedCvTerm entity) {
        cvTermRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(AuditableTypedCvTerm entity) {
        cvTermRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(AuditableTypedCvTerm entity) {
        cvTermRepository.delete(entity);
    }        

    @Override
    public AuditableTypedCvTerm findByAccession(String accession, CvTermType cvTermType) {
        return cvTermRepository.findByAccession(accession, cvTermType);
    }

    @Override
    public List<AuditableTypedCvTerm> findByCvTermByType(CvTermType cvTermType) {
        return cvTermRepository.findByCvTermType(cvTermType);
    }

    @Override
    public <T extends AuditableTypedCvTerm> List<T> findByCvTermByType(Class<T> clazz, CvTermType cvTermType) {
        List<T> cvTerms = new ArrayList<>();
        
        for(AuditableTypedCvTerm cvTerm : cvTermRepository.findByCvTermType(cvTermType)){
            if(clazz.isInstance(cvTerm)){
                cvTerms.add((T) cvTerm);
            }
        }
        
        return cvTerms;
    }

    @Override
    public long countAll() {
        return cvTermRepository.countAll();
    }
    
}
