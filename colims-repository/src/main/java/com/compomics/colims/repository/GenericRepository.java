/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import java.io.Serializable;
import java.util.List;

/**
 * This interface provides generic repository methods for finding, saving, updating and deleting entities in the db. The
 * transactions are defined on the service layer level to avoid multiple transactions if a service method calls more
 * than one repository method.
 *
 * @param <T>  the entity class
 * @param <ID> the ID class
 * @author Niels Hulstaert
 */
public interface GenericRepository<T, ID extends Serializable> {

    /**
     * Find an entity by its primary key.
     *
     * @param id the primary key
     * @return the entity
     */
    T findById(final ID id);

    /**
     * Load all entities.
     *
     * @return the list of entities
     */
    List<T> findAll();

    /**
     * Find entities based on an example.
     *
     * @param exampleInstance the example
     * @return the list of entities
     */
    List<T> findByExample(final T exampleInstance);

    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    long countAll();

    /**
     * Persist an entity.
     *
     * @param entity the entity to save
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
