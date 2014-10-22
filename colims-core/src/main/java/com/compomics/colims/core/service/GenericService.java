package com.compomics.colims.core.service;

import java.io.Serializable;
import java.util.List;

/**
 * This interface provides generic methods for finding, saving, updating and
 * deleting entities in the db.
 *
 * @author Niels Hulstaert
 * @param <T> the entity class
 * @param <ID> the ID class
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
     * @param entity the entity to save
     */
    void save(final T entity);

    /**
     * Save the entity.
     *
     * @param entity the entity to update
     */
    void update(final T entity);

    /**
     * Save or update the entity. If the entity already exists, update it else
     * save it.
     *
     * @param entity the entity to save or update
     */
    void saveOrUpdate(final T entity);

    /**
     * Delete the entity.
     *
     * @param entity the entity to delete
     */
    void delete(final T entity);

    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    long countAll();
}
