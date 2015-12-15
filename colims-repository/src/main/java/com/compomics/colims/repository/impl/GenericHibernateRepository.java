package com.compomics.colims.repository.impl;

import com.compomics.colims.repository.GenericRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author Niels Hulstaert
 * @param <T> the database entity class
 * @param <ID> the ID class
 */
public class GenericHibernateRepository<T, ID extends Serializable> implements GenericRepository<T, ID> {

    /**
     * The entity class reference.
     */
    private final Class<T> entityClass;

    /**
     * The JPA entityManagerFactory instance.
     */
    @PersistenceContext
    private EntityManager entityManager;

    public GenericHibernateRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Constructor.
     *
     * @param persistentClass the entity class reference
     */
    public GenericHibernateRepository(final Class<T> persistentClass) {
        super();
        this.entityClass = persistentClass;
    }

    @Override
    public T findById(final ID id) {
        return getCurrentSession().get(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    @Override
    public List<T> findByExample(final T exampleInstance) {
        return createCriteria(Example.create(exampleInstance)).list();
    }

    @Override
    public long countAll() {
        Criteria criteria = createCriteria();
        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public void persist(T entity) {
        entityManager.persist(entity);
    }

    @Override
    public T merge(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void remove(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public T getReference(ID id) {
        return entityManager.getReference(entityClass, id);
    }

    /**
     * Get the Class of the entity.
     *
     * @return the class
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Internal method to quickly create a {@link Criteria} for the {@link com.compomics.colims.model.DatabaseEntity}
     * with optional {@link Criterion}s.
     *
     * @param criterions the vararg of Criterion instances
     * @return the created criteria
     */
    protected Criteria createCriteria(final Criterion... criterions) {
        Criteria createCriteria = getCurrentSession().createCriteria(entityClass);
        for (Criterion criterion : criterions) {
            createCriteria.add(criterion);
        }
        return createCriteria;
    }

    /**
     * Convenience method for retrieving the current session.
     *
     * @return the current session
     */
    protected Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Convenience method.
     *
     * @param criterion the Criterion instance
     * @return the found entity
     */
    protected T findUniqueByCriteria(final Criterion... criterion) {
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * Convenience method.
     *
     * @param criterion the Criterion instance
     * @return the list of found entities
     */
    protected List<T> findByCriteria(final Criterion... criterion) {
        return findByCriteria(-1, -1, criterion);
    }

    /**
     * Convenience method.
     *
     * @param firstResult the first result
     * @param maxResults  the maximum number of results
     * @param criterion   the Criterion instances
     * @return the list of found entities
     */
    protected List<T> findByCriteria(final int firstResult,
                                     final int maxResults, final Criterion... criterion) {
        Criteria criteria = createCriteria(criterion);

        if (firstResult > 0) {
            criteria.setFirstResult(firstResult);
        }

        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }

}
