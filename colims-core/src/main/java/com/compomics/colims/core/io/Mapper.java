package com.compomics.colims.core.io;

/**
 * interface for mapping POJO to POJO
 *
 * @author Niels Hulstaert
 * @param <S> the source class
 * @param <T> the target class
 */
public interface Mapper<S, T> {

    /**
     * Map the source POJO on the target POJO
     *
     * @param source the source POJO
     * @param target the target POJO
     */
    void map(S source, T target);
}
