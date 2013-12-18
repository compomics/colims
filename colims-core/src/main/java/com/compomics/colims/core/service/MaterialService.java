package com.compomics.colims.core.service;

import com.compomics.colims.model.Material;

/**
 *
 * @author Niels Hulstaert
 */
public interface MaterialService extends GenericService<Material, Long> {

    /**
     * Find the material by name, return null if no material was found.
     *
     * @param name the material by name
     * @return the found material
     */
    Material findByName(String name);

}
