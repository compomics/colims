package com.compomics.colims.repository;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface AuditableTypedCvParamRepository extends GenericRepository<AuditableTypedCvParam, Long> {

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
     * Find CV params by CV param property. Returns null if nothing was found.
     *
     * @param cvParamType the CvParamType
     * @return the found CV params
     */
    List<AuditableTypedCvParam> findByCvParamType(CvParamType cvParamType);
}
