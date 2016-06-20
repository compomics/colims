package com.compomics.colims.repository;

import com.compomics.colims.model.cv.CvParam;
import java.util.List;

/**
 * This interface provides repository methods for the CvParam class.
 *
 * @author Niels Hulstaert
 */
public interface CvParamRepository extends GenericRepository<CvParam, Long> {

     /**
     * Get an instance, whose state may be lazily fetched.
     *
     * @param entityClass the entity class that is a subclass of CvParam
     * @param id the entity ID
     * @return the referenced entity
     */
    CvParam getMappedSuperclassReference(Class<? extends CvParam> entityClass, Long id);

    /**
     * Find CV params by accession.
     *
     * @param accession the CV param accession
     * @return the list of found CV params
     */
    List<CvParam> findByAccession(String accession);

    /**
     * Find CV params by name (ignoring the casing or not).
     *
     * @param name the CV param name
     * @param ignoreCase whether or not to ignore the name casing during the
     * comparison
     * @return the list of found CV params
     */
    List<CvParam> findByName(String name, boolean ignoreCase);
}
