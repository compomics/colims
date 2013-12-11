/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface GenericService<T, ID extends Serializable> {

    /**
     * Find the entity by its ID.
     *
     * @param id the ID
     * @return the found entity
     */
    T findById(final ID id);

    /**
     * Find all entities.
     *
     * @return the list of found entities
     */
    List<T> findAll();

    /**
     * Save the entity.
     *
     * @param entity
     */
    void save(final T entity);

    /**
     * Save the entity.
     *
     * @param entity
     */
    void update(final T entity);

    /**
     * Save or update the entity. If the entity already exists, update it else
     * save it.
     *
     * @param entity
     */
    void saveOrUpdate(final T entity);

    /**
     * Delete the entity.
     *
     * @param entity
     */
    void delete(final T entity);
    
    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    long countAll();
}
