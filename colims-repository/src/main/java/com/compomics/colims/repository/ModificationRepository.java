package com.compomics.colims.repository;

import com.compomics.colims.model.Modification;

/**
 *
 * @author Niels Hulstaert
 */
public interface ModificationRepository extends GenericRepository<Modification, Long> {

    /**
     * Find a modification by the modification name.
     *
     * @param name the modification name
     * @return the found modification
     */
    Modification findByName(String name);
}
