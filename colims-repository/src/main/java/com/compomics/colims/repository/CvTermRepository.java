package com.compomics.colims.repository;

import com.compomics.colims.model.TypedCvTerm;
import com.compomics.colims.model.enums.CvTermType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface CvTermRepository extends GenericRepository<TypedCvTerm, Long> {
    /**
     * Find a CV term by accession and cvTermType. Returns null if nothing
     * was found.
     *
     * @param accession the CV term accession
     * @param cvTermType the CV term property
     * @return the found CV term
     */
    TypedCvTerm findByAccession(String accession, CvTermType cvTermType);

    /**
     * Find CV terms by CV term property. Returns null if nothing was found.
     *
     * @param cvTermType the cvTermType
     * @return the found CV terms
     */
    List<TypedCvTerm> findByCvTermType(CvTermType cvTermType);
}
