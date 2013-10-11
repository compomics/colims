package com.compomics.colims.repository;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Material;

/**
 *
 * @author Niels Hulstaert
 */
public interface MaterialRepository extends GenericRepository<Material, Long> {
    
    /**
     * Find the material by the material name, returns null if no material
     * was found.
     *
     * @param name the instrument name
     * @return the found instrument
     */
    Material findByName(String name);

}
