package com.compomics.colims.core.service;

import com.compomics.colims.model.Modification;

/**
 *
 * @author niels
 */
public interface ModificationService extends GenericService<Modification, Long> {

    /**
     * Find a modification by the modification name.
     *
     * @param name the modification name
     * @return the found modification
     */
    Modification findByName(String name);
}
