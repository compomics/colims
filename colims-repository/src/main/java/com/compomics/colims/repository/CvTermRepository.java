package com.compomics.colims.repository;

import com.compomics.colims.model.AuditableTypedCvTerm;
import com.compomics.colims.model.enums.CvTermType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface CvTermRepository extends GenericRepository<AuditableTypedCvTerm, Long> {
    /**
     * Find a CV term by accession and cvTermType. Returns null if nothing
     * was found.
     *
     * @param accession the CV term accession
     * @param cvTermType the CV term property
     * @return the found CV term
     */
    AuditableTypedCvTerm findByAccession(String accession, CvTermType cvTermType);

    /**
     * Find CV terms by CV term property. Returns null if nothing was found.
     *
     * @param cvTermType the cvTermType
     * @return the found CV terms
     */
    List<AuditableTypedCvTerm> findByCvTermType(CvTermType cvTermType);
}
