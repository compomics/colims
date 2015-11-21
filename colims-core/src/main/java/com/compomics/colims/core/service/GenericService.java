package com.compomics.colims.core.service;

import java.io.Serializable;
import java.util.List;

/**
 * This interface provides generic service methods for finding, saving, updating and deleting entities in the db. The
 * service methods are transactional (defined in the implementation), not the repository methods called in the
 * services.
 *
 * @param <T>  the entity class
 * @param <ID> the ID class
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
     * Count all entities.
     *
     * @return the number of entities
     */
    long countAll();

    /**
     * Persist an entity.
     *
     * @param entity the entity to persist
     */
    void persist(final T entity);

    /**
     * Merge an entity.
     *
     * @param entity the entity to merge
     * @return the merged entity
     */
    T merge(final T entity);

    /**
     * Remove an entity from the database.
     *
     * @param entity the entity to remove
     */
    void remove(final T entity);

}
