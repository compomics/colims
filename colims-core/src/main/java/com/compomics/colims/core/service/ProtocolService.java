package com.compomics.colims.core.service;

import com.compomics.colims.model.Protocol;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProtocolService extends GenericService<Protocol, Long> {

    /**
     * Find the protocol by name, return null if no protocol was found.
     *
     * @param name the protocol by name
     * @return the found protocol
     */
    Protocol findByName(String name);

}
