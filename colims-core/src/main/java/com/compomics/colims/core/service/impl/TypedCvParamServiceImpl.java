package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.model.cv.TypedCvParam;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.TypedCvParamRepository;
import java.util.ArrayList;

/**
 *
 * @author Niels Hulstaert
 */
@Service("typedCvParamService")
@Transactional
public class TypedCvParamServiceImpl implements TypedCvParamService {

    @Autowired
    private TypedCvParamRepository cvParamRepository;

    @Override
    public TypedCvParam findById(Long id) {
        return cvParamRepository.findById(id);
    }

    @Override
    public List<TypedCvParam> findAll() {
        return cvParamRepository.findAll();
    }

    @Override
    public void save(TypedCvParam entity) {
        cvParamRepository.save(entity);
    }

    @Override
    public void update(TypedCvParam entity) {
        cvParamRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(TypedCvParam entity) {
        cvParamRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(TypedCvParam entity) {
        cvParamRepository.delete(entity);
    }

    @Override
    public TypedCvParam findByAccession(String accession, CvParamType cvParamType) {
        return cvParamRepository.findByAccession(accession, cvParamType);
    }

    @Override
    public List<TypedCvParam> findByCvParamByType(CvParamType cvParamType) {
        return cvParamRepository.findByCvParamType(cvParamType);
    }

    @Override
    public <T extends TypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvParamType) {
        List<T> cvTerms = new ArrayList<>();

        for(TypedCvParam cvTerm : cvParamRepository.findByCvParamType(cvParamType)){
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
