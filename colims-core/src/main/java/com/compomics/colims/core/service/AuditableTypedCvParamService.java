package com.compomics.colims.core.service;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface AuditableTypedCvParamService extends GenericService<AuditableTypedCvParam, Long> {

    /**
     * Find a CV param by accession and cvParamType. Returns null if nothing
     * was found.
     *
     * @param accession the CV param accession
     * @param cvParamType the CV param property
     * @return the found CV param
     */
    AuditableTypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find CV params by CvparamType. Returns null if nothing was found.
     *
     * @param cvParamType the cvParamType
     * @return the found CV params
     */
    List<AuditableTypedCvParam> findByCvParamByType(CvParamType cvParamType);

    /**
     * Find CV params by CvParamType. Returns null if nothing was found.
     *
     * @param <T> the CV param class
     * @param clazz the CvParam sub class
     * @param cvParamType the cvParamType
     * @return the found CV params
     */
    <T extends AuditableTypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvParamType);
}
