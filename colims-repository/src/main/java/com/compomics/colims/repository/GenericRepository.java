/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Niels Hulstaert
 */
public interface GenericRepository<T, ID extends Serializable> {

    /**
     * Get the Class of the entity.
     *
     * @return the class
     */
    Class<T> getEntityClass();

    /**
     * Find an entity by its primary key
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
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQuery(
            final String queryName,
            Object... params);

    /**
     * Find using a named query.
     *
     * @param queryName the name of the query
     * @param params the query parameters
     * @return the list of entities
     */
    List<T> findByNamedQueryAndNamedParams(
            final String queryName,
            final Map<String, ? extends Object> params);

    /**
     * Count all entities.
     *
     * @return the number of entities
     */
    int countAll();

    /**
     * Count entities based on an example.
     *
     * @param exampleInstance the search criteria
     * @return the number of entities
     */
    int countByExample(final T exampleInstance);

    /**
     * save an entity. That means insert an entry if the identifier doesn’t
     * exist, else throw an exception. If the primary key is already present in
     * the table, it cannot be inserted.
     *
     * @param entity the entity to save     
     */
    void save(final T entity);

    /**
     * update an entity. Update method in the hibernate is used for updating the
     * entity using an identifier. If the identifier is missing or doesn’t
     * exist, throw an exception.
     *
     * @param entity the entity to update
     */
    void update(final T entity);

    /**
     * save or update an entity. This method calls save() or update() based on
     * the operation. If the entity's identifier exists, it will call the update
     * method else the save method.
     *
     * @param entity the entity to update
     */
    void saveOrUpdate(final T entity);

    /**
     * delete an entity from the database.
     *
     * @param entity the entity to delete
     */
    void delete(final T entity);
}
