package com.compomics.colims.core.interfaces;

import com.compomics.colims.core.exception.MappingException;

/**
 * interface for mapping POJO to POJO
 *
 * @author niels
 */
public interface Mapper<S, T> {

    /**
     * Map the source POJO on the target POJO
     *
     * @param source the source POJO
     * @param target the target POJO
     * @throws MappingException the mapping exception, thrown in case of a
     * mapping error
     */
    void map(S source, T target) throws MappingException;
}
