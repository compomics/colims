package com.compomics.colims.core.service;

import com.compomics.colims.model.Protocol;

/**
 * This interface provides service methods for the Protocol class.
 *
 * @author Niels Hulstaert
 */
public interface ProtocolService extends GenericService<Protocol, Long> {

    /**
     * Count the number of protocols by protocol name.
     *
     * @param protocol the Protocol instance
     * @return the number of found protocols
     */
    Long countByName(Protocol protocol);

}
