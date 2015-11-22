package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.TypedCvParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
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
    public TypedCvParam findByAccession(String accession, CvParamType cvParamType) {
        return cvParamRepository.findByAccession(accession, cvParamType);
    }

    @Override
    public TypedCvParam findByName(final String name, final CvParamType cvParamType, final boolean ignoreCase) {
        return cvParamRepository.findByName(name, cvParamType, ignoreCase);
    }

    @Override
    public List<TypedCvParam> findByCvParamByType(final CvParamType cvParamType) {
        return cvParamRepository.findByCvParamType(cvParamType);
    }

    @Override
    public <T extends TypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvParamType) {
        List<T> cvTerms = new ArrayList<>();

        for (TypedCvParam cvTerm : cvParamRepository.findByCvParamType(cvParamType)) {
            if (clazz.isInstance(cvTerm)) {
                cvTerms.add((T) cvTerm);
            }
        }

        return cvTerms;
    }

    @Override
    public long countAll() {
        return cvParamRepository.countAll();
    }

    @Override
    public void persist(TypedCvParam entity) {
        cvParamRepository.persist(entity);
    }

    @Override
    public TypedCvParam merge(TypedCvParam entity) {
        return cvParamRepository.merge(entity);
    }

    @Override
    public void remove(TypedCvParam entity) {
        cvParamRepository.remove(entity);
    }
}
