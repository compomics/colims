package com.compomics.colims.repository;

import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;

import java.util.List;

/**
 * This interface provides repository methods for the TypedCvParam class.
 *
 * @author Niels Hulstaert
 */
public interface TypedCvParamRepository extends GenericRepository<TypedCvParam, Long> {

    /**
     * Find a CV param by accession and CvParamType. Returns null if nothing was found.
     *
     * @param accession   the CV param accession
     * @param cvParamType the CvParamType instance
     * @return the found CV param
     */
    TypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find a CV param by name (ignoring the casing or not) and CvParamType. Returns null if nothing was found.
     *
     * @param name        the CV param name
     * @param cvParamType the CvParamType instance
     * @param ignoreCase  whether or not to ignore the name casing during the comparison
     * @return the found CV param
     */
    TypedCvParam findByName(String name, CvParamType cvParamType, boolean ignoreCase);

    /**
     * Find CV params by CV param property. Returns null if nothing was found.
     *
     * @param cvParamType the CvParamType instance
     * @return the found CV params
     */
    List<TypedCvParam> findByCvParamType(CvParamType cvParamType);
    
}
