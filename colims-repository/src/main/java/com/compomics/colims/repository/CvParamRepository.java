package com.compomics.colims.repository;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface CvParamRepository extends GenericRepository<AuditableTypedCvParam, Long> {
    /**
     * Find a CV term by accession and cvTermType. Returns null if nothing
     * was found.
     *
     * @param accession the CV term accession
     * @param cvTermType the CV term property
     * @return the found CV term
     */
    AuditableTypedCvParam findByAccession(String accession, CvParamType cvTermType);

    /**
     * Find CV terms by CV term property. Returns null if nothing was found.
     *
     * @param cvTermType the cvTermType
     * @return the found CV terms
     */
    List<AuditableTypedCvParam> findByCvTermType(CvParamType cvTermType);
}
