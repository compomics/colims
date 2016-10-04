package com.compomics.colims.repository;

import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.enums.QuantificationEngineType;

/**
 * This interface provides repository methods for the QuantificationEngine class.
 *
 * @author Niels Hulstaert
 */
public interface QuantificationEngineRepository extends GenericRepository<QuantificationEngine, Long> {

    /**
     * Find the quantification engine by type and version. Returns null if nothing was
     * found.
     *
     * @param quantificationEngineType the quantification engine type
     * @param version the quantification engine version
     * @return the found QuantificationEngine
     */
    QuantificationEngine findByNameAndVersion(QuantificationEngineType quantificationEngineType, String version);

    
    /**
     * Find the quantification engine by type. If multiple were found (different
     * versions), a random one is returned. Returns null if nothing was found.
     *
     * @param quantificationEngineType the quantification engine type
     * @return the found QuantificationEngine instance
     */
    QuantificationEngine findByType(QuantificationEngineType quantificationEngineType);
}
