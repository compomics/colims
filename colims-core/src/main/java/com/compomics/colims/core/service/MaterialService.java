package com.compomics.colims.core.service;

import com.compomics.colims.model.Material;

/**
 *
 * @author niels
 */
public interface MaterialService extends GenericService<Material, Long> {

    /**
     * Find the instrument by name, return null if no instrument was found.
     *
     * @param name the instrument by name
     * @return the found instrument
     */
    Material findByName(String name);

}
