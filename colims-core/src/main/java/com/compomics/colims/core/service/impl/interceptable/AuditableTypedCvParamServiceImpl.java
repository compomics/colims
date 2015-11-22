package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.AuditableTypedCvParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
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
    public AuditableTypedCvParam findByAccession(final String accession, final CvParamType cvParamType) {
        return cvParamRepository.findByAccession(accession, cvParamType);
    }

    @Override
    public AuditableTypedCvParam findByName(final String name, final CvParamType cvParamType, final boolean ignoreCase) {
        return cvParamRepository.findByName(name, cvParamType, ignoreCase);
    }

    @Override
    public List<AuditableTypedCvParam> findByCvParamByType(CvParamType cvParamType) {
        return cvParamRepository.findByCvParamType(cvParamType);
    }

    @Override
    public <T extends AuditableTypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvTermType) {
        List<T> cvTerms = new ArrayList<>();

        for (AuditableTypedCvParam cvTerm : cvParamRepository.findByCvParamType(cvTermType)) {
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
    public void persist(AuditableTypedCvParam entity) {
        cvParamRepository.persist(entity);
    }

    @Override
    public AuditableTypedCvParam merge(AuditableTypedCvParam entity) {
        return cvParamRepository.merge(entity);
    }

    @Override
    public void remove(AuditableTypedCvParam entity) {
        cvParamRepository.remove(entity);
    }
}
