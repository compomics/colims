package com.compomics.colims.core.service;

import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermType;
import java.util.List;

/**
 *
 * @author niels
 */
public interface CvTermService extends GenericService<CvTerm, Long> {

    /**
     * Find a CV term by accession and cvTermType. Returns null if nothing
     * was found.
     *
     * @param accession the CV term accession
     * @param cvTermType the CV term property
     * @return the found CV term
     */
    CvTerm findByAccession(String accession, CvTermType cvTermType);

    /**
     * Find CV terms by CvTermType. Returns null if nothing was found.
     *
     * @param cvTermType the cvTermType
     * @return the found CV terms
     */
    List<CvTerm> findByCvTermByType(CvTermType cvTermType);
    
    /**
     * Find CV terms by CvTermType. Returns null if nothing was found.
     *
     * @param clazz the CvTerm sub class
     * @param cvTermType the cvTermType
     * @return the found CV terms
     */
    <T extends CvTerm> List<T> findByCvTermByType(Class<T> clazz, CvTermType cvTermType);
}
