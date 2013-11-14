package com.compomics.colims.repository;

import com.compomics.colims.model.Protocol;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProtocolRepository extends GenericRepository<Protocol, Long> {
    
    /**
     * Find the protocol by the protocol name, returns null if no protocol
     * was found.
     *
     * @param name the protocol name
     * @return the found protocol
     */
    Protocol findByName(String name);
    
    /**
     * Find all protocols ordered by name.
     * 
     * @return the ordered list of protocols
     */
    List<Protocol> findAllOrderedByName();

}