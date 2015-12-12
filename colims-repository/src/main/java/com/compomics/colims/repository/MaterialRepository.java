package com.compomics.colims.repository;

import com.compomics.colims.model.Material;

import java.util.List;

/**
 * This interface provides repository methods for the Material class.
 *
 * @author Niels Hulstaert
 */
public interface MaterialRepository extends GenericRepository<Material, Long> {

    /**
     * Count the number of materials by material name.
     *
     * @param material the Material instance
     * @return the number of found materials
     */
    Long countByName(Material material);

    /**
     * Find all materials ordered by name.
     *
     * @return the ordered list of materials
     */
    List<Material> findAllOrderedByName();

}
