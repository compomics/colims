package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.CvParamService;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.repository.CvParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Niels Hulstaert
 */
@Service("cvParamService")
@Transactional
public class CvParamServiceImpl implements CvParamService {

    private final CvParamRepository cvParamRepository;

    @Autowired
    public CvParamServiceImpl(CvParamRepository cvParamRepository) {
        this.cvParamRepository = cvParamRepository;
    }

    @Override
    public CvParam findById(Long id) {
        return cvParamRepository.findById(id);
    }

    @Override
    public List<CvParam> findAll() {
        return cvParamRepository.findAll();
    }

    @Override
    public CvParam findByAccession(final Class clazz, final String accession) {
        List<CvParam> foundCvParams = cvParamRepository.findByAccession(accession);
        Optional<CvParam> foundCvParam = foundCvParams.stream().filter(clazz::isInstance).findFirst();
        return foundCvParam.orElse(null);
    }

    @Override
    public CvParam findByName(final Class clazz, final String name, final boolean ignoreCase) {
        List<CvParam> foundCvParams = cvParamRepository.findByName(name, ignoreCase);
        Optional<CvParam> foundCvParam = foundCvParams.stream().filter(clazz::isInstance).findFirst();
        return foundCvParam.orElse(null);
    }

    @Override
    public long countAll() {
        return cvParamRepository.countAll();
    }

    @Override
    public void persist(CvParam entity) {
        cvParamRepository.persist(entity);
    }

    @Override
    public CvParam merge(CvParam entity) {
        return cvParamRepository.merge(entity);
    }

    @Override
    public void remove(CvParam entity) {
        //get a reference to the entity
        CvParam reference = cvParamRepository.getMappedSuperclassReference(entity.getClass(), entity.getId());
        cvParamRepository.remove(reference);
    }

    @Override
    public List<CvParam> findByCvParamByClass(Class clazz) {
        return cvParamRepository.findAll().stream().filter(clazz::isInstance).collect(Collectors.toList());
    }
}
