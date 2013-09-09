package com.compomics.colims.core.service;

import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.enums.CvTermProperty;
import java.util.List;

/**
 *
 * @author niels
 */
public interface CvTermService extends GenericService<CvTerm, Long> {

    /**
     * Find a CV term by accession and CvTermProperty. Returns null if nothing
     * was found.
     *
     * @param accession the CV term accession
     * @param cvTermProperty the CV term property
     * @return the found CV term
     */
    CvTerm findByAccession(String accession, CvTermProperty cvTermProperty);

    /**
     * Find CV terms by CV term property. Returns null if nothing was found.
     *
     * @param cvTermProperty the CvTermProperty
     * @return the found CV terms
     */
    List<CvTerm> findByCvTermByProperty(CvTermProperty cvTermProperty);
    
    /**
     * Find CV terms by CV term property. Returns null if nothing was found.
     *
     * @param clazz the CvTerm sub class
     * @param cvTermProperty the CvTermProperty
     * @return the found CV terms
     */
    <T extends CvTerm> List<T> findByCvTermByProperty(Class<T> clazz, CvTermProperty cvTermProperty);
}
