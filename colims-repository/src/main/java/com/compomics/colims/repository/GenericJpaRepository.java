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
public interface GenericJpaRepository<T, ID extends Serializable> {

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
     * Save an entity. That means insert an entry if the identifier doesn't exist, else throw an exception. If the
     * primary key is already present in the table, it cannot be inserted.
     *
     * @param entity the entity to save
     */
    void save(final T entity);

    /**
     * Update an entity. Update method in the hibernate is used for updating the entity using an identifier. If the
     * identifier is missing or doesn't exist, throw an exception.
     *
     * @param entity the entity to update
     */
    void update(final T entity);

    /**
     * Save or update an entity. This method calls save() or update() based on the operation. If the entity's identifier
     * exists, it will call the update method else the save method.
     *
     * @param entity the entity to update
     */
    void saveOrUpdate(final T entity);

    /**
     * Delete an entity from the database.
     *
     * @param entity the entity to delete
     */
    void delete(final T entity);

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
