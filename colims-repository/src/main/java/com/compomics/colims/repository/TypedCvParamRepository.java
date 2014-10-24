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
     * Find a CV param by accession and cvParamType. Returns null if nothing was
     * found.
     *
     * @param accession the CV param accession
     * @param cvParamType the CV param property
     * @return the found CV param
     */
    TypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find CV params by CV param property. Returns null if nothing was found.
     *
     * @param cvParamType the CvParamType
     * @return the found CV params
     */
    List<TypedCvParam> findByCvParamType(CvParamType cvParamType);
}
