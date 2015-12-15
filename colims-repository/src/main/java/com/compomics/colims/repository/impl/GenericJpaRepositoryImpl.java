package com.compomics.colims.repository.impl;

import com.compomics.colims.repository.GenericJpaRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author Niels Hulstaert
 * @param <T> the database entity class
 * @param <ID> the ID class
 */
public class GenericJpaRepositoryImpl<T, ID extends Serializable> implements GenericJpaRepository<T, ID> {

    /**
     * The entity class reference.
     */
    private final Class<T> entityClass;

    /**
     * The JPA entityManagerFactory instance.
     */
    @PersistenceContext
    private EntityManager entityManager;

    public GenericJpaRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Constructor.
     *
     * @param persistentClass the entity class reference
     */
    public GenericJpaRepositoryImpl(final Class<T> persistentClass) {
        super();
        this.entityClass = persistentClass;
    }

    @Override
    public T findById(final ID id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> criteriaQuery = getCriteriaQuery();
        Root<T> root = criteriaQuery.from(entityClass);
        CriteriaQuery<T> select = criteriaQuery.select(root);

        return entityManager.createQuery(select).getResultList();
    }

    @Override
    public void save(T entity) {
        getCurrentSession().save(entity);
    }

    @Override
    public void update(T entity) {
        getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public List<T> findByExample(final T exampleInstance) {
        return createCriteria(Example.create(exampleInstance)).list();
    }

    @Override
    public long countAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override

    public void persist(final T entity) {
        entityManager.persist(entity);
    }

    @Override
    public T merge(final T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void remove(final T entity) {
        entityManager.remove(entity);
    }

    /**
     * Get the entity manager.
     *
     * @return the class
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Get the Class of the entity.
     *
     * @return the class
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Internal method to quickly create a {@link Criteria} for the
     * {@link com.compomics.colims.model.DatabaseEntity} with optional
     * {@link Criterion}s.
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
     * Convenience method for retrieving a type safe CriteriaQuery.
     *
     * @return the CriteriaQuery instance
     */
    protected CriteriaQuery<T> getCriteriaQuery() {
        return entityManager.getCriteriaBuilder().createQuery(entityClass);
    }
}
