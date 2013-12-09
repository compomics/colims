package com.compomics.colims.repository;

import com.compomics.colims.model.Material;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface MaterialRepository extends GenericRepository<Material, Long> {
    
    /**
     * Find the material by the material name, returns null if no material
     * was found.
     *
     * @param name the material name
     * @return the found material
     */
    Material findByName(String name);
    
    /**
     * Find all materials ordered by name.
     * 
     * @return the ordered list of materials
     */
    List<Material> findAllOrderedByName();

}
