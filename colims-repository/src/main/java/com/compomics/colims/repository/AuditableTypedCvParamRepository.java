package com.compomics.colims.repository;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;

import java.util.List;

/**
 * This interface provides repository methods for the AuditableTypedCvParam
 * class.
 *
 * @author Niels Hulstaert
 */
public interface AuditableTypedCvParamRepository extends GenericRepository<AuditableTypedCvParam, Long> {

    /**
     * Get an instance, whose state may be lazily fetched.
     *
     * @param entityClass the entity class that is a subclass of
     * AuditableTypedCvParam
     * @param id the entity ID
     * @return the referenced entity
     */
    AuditableTypedCvParam getMappedSuperclassReference(Class entityClass, Long id);

    /**
     * Find a CV param by accession and CvParamType. Returns null if nothing was
     * found.
     *
     * @param accession the CV param accession
     * @param cvParamType the CV param property
     * @return the found CV param
     */
    AuditableTypedCvParam findByAccession(String accession, CvParamType cvParamType);

    /**
     * Find a CV param by name (ignoring the casing or not) and CvParamType.
     * Returns null if nothing was found.
     *
     * @param name the CV param accession
     * @param cvParamType the CV param property
     * @param ignoreCase whether or not to ignore the name casing during the
     * comparison
     * @return the found CV param
     */
    AuditableTypedCvParam findByName(String name, CvParamType cvParamType, boolean ignoreCase);

    /**
     * Find CV params by CV param property. Returns null if nothing was found.
     *
     * @param cvParamType the CvParamType
     * @return the found CV params
     */
    List<AuditableTypedCvParam> findByCvParamType(CvParamType cvParamType);
}
