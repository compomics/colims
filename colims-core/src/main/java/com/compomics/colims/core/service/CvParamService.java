package com.compomics.colims.core.service;

import com.compomics.colims.model.cv.CvParam;
import java.util.List;

/**
 * This interface provides service methods for the TypedCvParam class.
 *
 * @author Niels Hulstaert
 */
public interface CvParamService extends GenericService<CvParam, Long> {

    /**
     * Find a CV param by accession. Returns null if nothing was found.
     *
     * @param clazz the CvParam subclass
     * @param accession the CV param accession
     * @return the found CV param
     */
    CvParam findByAccession(Class clazz, String accession);

    /**
     * Find a CV param by name (ignoring the casing or not). Returns null if
     * nothing was found.
     *
     * @param clazz the CvParam subclass
     * @param name the CV param name
     * @param ignoreCase whether or not to ignore the name casing during the
     * comparison
     * @return the found CV param
     */
    CvParam findByName(Class clazz, String name, boolean ignoreCase);

    /**
     * Find CV params by class. Returns null if nothing was found.
     *
     * @param clazz the CvParam sub class
     * @return the found CV params
     */
    List<CvParam> findByCvParamByClass(Class clazz);

}
