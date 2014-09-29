package com.compomics.colims.core.service;

import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface TypedCvParamService extends GenericService<TypedCvParam, Long> {

    /**
     * Find a CV param by accession and cvParamType. Returns null if nothing
     * was found.
     *
     * @param accession the CV param accession
     * @param cvParamType the CV param property
     * @return the found CV param
     */
    TypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find CV params by CvparamType. Returns null if nothing was found.
     *
     * @param cvParamType the cvParamType
     * @return the found CV params
     */
    List<TypedCvParam> findByCvParamByType(CvParamType cvParamType);

    /**
     * Find CV params by CvParamType. Returns null if nothing was found.
     *
     * @param <T> the CV param class
     * @param clazz the CvParam sub class
     * @param cvParamType the cvParamType
     * @return the found CV params
     */
    <T extends TypedCvParam> List<T> findByCvParamByType(Class<T> clazz, CvParamType cvParamType);
}
