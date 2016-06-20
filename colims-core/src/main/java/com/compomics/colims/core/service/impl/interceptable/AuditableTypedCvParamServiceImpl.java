package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.repository.AuditableTypedCvParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niels Hulstaert
 */
@Service("auditableTypedCvParamService")
@Transactional
public class AuditableTypedCvParamServiceImpl implements AuditableTypedCvParamService {

    @Autowired
    private AuditableTypedCvParamRepository auditableTypedCvParamRepository;

    @Override
    public AuditableTypedCvParam findById(Long id) {
        return auditableTypedCvParamRepository.findById(id);
    }

    @Override
    public List<AuditableTypedCvParam> findAll() {
        return auditableTypedCvParamRepository.findAll();
    }

    @Override
    public AuditableTypedCvParam findByAccession(final String accession, final CvParamType cvParamType) {
        return auditableTypedCvParamRepository.findByAccession(accession, cvParamType);
    }

    @Override
    public AuditableTypedCvParam findByName(final String name, final CvParamType cvParamType, final boolean ignoreCase) {
        return auditableTypedCvParamRepository.findByName(name, cvParamType, ignoreCase);
    }

    @Override
    public List<AuditableTypedCvParam> findByCvParamByType(CvParamType cvParamType) {
        return auditableTypedCvParamRepository.findByCvParamType(cvParamType);
    }

    @Override
    public <T extends AuditableTypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvTermType) {
        return auditableTypedCvParamRepository.findByCvParamType(cvTermType).stream().filter(clazz::isInstance).map(cvParam -> (T) cvParam).collect(Collectors.toList());
    }

    @Override
    public long countAll() {
        return auditableTypedCvParamRepository.countAll();
    }

    @Override
    public void persist(AuditableTypedCvParam entity) {
        auditableTypedCvParamRepository.persist(entity);
    }

    @Override
    public AuditableTypedCvParam merge(AuditableTypedCvParam entity) {
        return auditableTypedCvParamRepository.merge(entity);
    }

    @Override
    public void remove(AuditableTypedCvParam entity) {
        //get a reference to the entity
        AuditableTypedCvParam reference = auditableTypedCvParamRepository.getMappedSuperclassReference(entity.getClass(), entity.getId());
        auditableTypedCvParamRepository.remove(reference);
    }
}
