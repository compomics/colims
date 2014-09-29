package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.AuditableTypedCvParamRepository;
import java.util.ArrayList;

/**
 *
 * @author Niels Hulstaert
 */
@Service("auditableTypedCvParamService")
@Transactional
public class AuditableTypedCvParamServiceImpl implements AuditableTypedCvParamService {

    @Autowired
    private AuditableTypedCvParamRepository cvParamRepository;

    @Override
    public AuditableTypedCvParam findById(Long id) {
        return cvParamRepository.findById(id);
    }

    @Override
    public List<AuditableTypedCvParam> findAll() {
        return cvParamRepository.findAll();
    }

    @Override
    public void save(AuditableTypedCvParam entity) {
        cvParamRepository.save(entity);
    }

    @Override
    public void update(AuditableTypedCvParam entity) {
        cvParamRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(AuditableTypedCvParam entity) {
        cvParamRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(AuditableTypedCvParam entity) {
        cvParamRepository.delete(entity);
    }

    @Override
    public AuditableTypedCvParam findByAccession(String accession, CvParamType cvParamType) {
        return cvParamRepository.findByAccession(accession, cvParamType);
    }

    @Override
    public List<AuditableTypedCvParam> findByCvParamByType(CvParamType cvParamType) {
        return cvParamRepository.findByCvParamType(cvParamType);
    }

    @Override
    public <T extends AuditableTypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvTermType) {
        List<T> cvTerms = new ArrayList<>();

        for(AuditableTypedCvParam cvTerm : cvParamRepository.findByCvParamType(cvTermType)){
            if(clazz.isInstance(cvTerm)){
                cvTerms.add((T) cvTerm);
            }
        }

        return cvTerms;
    }

    @Override
    public long countAll() {
        return cvParamRepository.countAll();
    }

}
