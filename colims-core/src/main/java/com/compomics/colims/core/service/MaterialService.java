package com.compomics.colims.core.service;

import com.compomics.colims.model.Material;

/**
 * This interface provides service methods for the Material class.
 *
 * @author Niels Hulstaert
 */
public interface MaterialService extends GenericService<Material, Long> {

    /**
     * Count the number of materials by material name.
     *
     * @param name the material name
     * @return the number of found materials
     */
    Long countByName(String name);

}
