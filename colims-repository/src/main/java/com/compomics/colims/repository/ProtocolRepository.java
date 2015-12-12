package com.compomics.colims.repository;

import com.compomics.colims.model.Protocol;
import java.util.List;

/**
 * This interface provides repository methods for the Protocol class.
 *
 * @author Niels Hulstaert
 */
public interface ProtocolRepository extends GenericRepository<Protocol, Long> {

    /**
     * Count the number of protocols by protocol name.
     *
     * @param protocol the Protocol instance
     * @return the number of found protocols
     */
    Long countByName(Protocol protocol);

    /**
     * Find all protocols ordered by name.
     *
     * @return the ordered list of protocols
     */
    List<Protocol> findAllOrderedByName();

}
