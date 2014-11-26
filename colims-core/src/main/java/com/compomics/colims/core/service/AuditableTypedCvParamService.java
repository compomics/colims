package com.compomics.colims.core.service;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;

import java.util.List;

/**
 * This interface provides service methods for the AuditableTypedCvParam class.
 *
 * @author Niels Hulstaert
 */
public interface AuditableTypedCvParamService extends GenericService<AuditableTypedCvParam, Long> {

    /**
     * Find a CV param by accession and CvParamType. Returns null if nothing was found.
     *
     * @param accession   the CV param accession
     * @param cvParamType the CV param property
     * @return the found CV param
     */
    AuditableTypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find a CV param by name (ignoring the casing or not) and CvParamType. Returns null if nothing was found.
     *
     * @param name        the CV param accession
     * @param cvParamType the CV param property
     * @param ignoreCase  whether or not to ignore the name casing during the comparison
     * @return the found CV param
     */
    AuditableTypedCvParam findByName(String name, CvParamType cvParamType, boolean ignoreCase);

    /**
     * Find CV params by example (a CvParamType instance). Returns null if nothing was found.
     *
     * @param cvParamType the CvParamType instance
     * @return the found CV params
     */
    List<AuditableTypedCvParam> findByCvParamByType(CvParamType cvParamType);

    /**
     * Find CV params by class and example (a CvParamType instance). Returns null if nothing was found.
     *
     * @param <T>         the CV param class
     * @param clazz       the CvParam sub class
     * @param cvParamType the CvParamType instance
     * @return the found CV params
     */
    <T extends AuditableTypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvParamType);
}
