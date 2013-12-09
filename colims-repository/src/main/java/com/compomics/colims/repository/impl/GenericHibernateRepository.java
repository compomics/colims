package com.compomics.colims.repository.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.metamodel.source.annotations.entity.EntityClass;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.colims.repository.GenericRepository;

/**
 *
 * @author Niels Hulstaert
 */
public class GenericHibernateRepository<T, ID extends Serializable> implements GenericRepository<T, ID> {

    private final Class<T> entityClass;
    @Autowired
    private SessionFactory sessionFactory;

    public GenericHibernateRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public GenericHibernateRepository(final Class<T> persistentClass) {
        super();
        this.entityClass = persistentClass;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Internal method to quickly create a {@link Criteria} for the
     * {@link EntityClass} with optional {@link Criterion}s.
     *
     * @return
     */
    protected Criteria createCriteria(final Criterion... criterions) {
        Criteria createCriteria = getCurrentSession().createCriteria(entityClass);
        for (Criterion criterion : criterions) {
            createCriteria.add(criterion);
        }
        return createCriteria;
    }

    @Override
    public T findById(final ID id) {
        return (T) getCurrentSession().get(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    @Override
    public List<T> findByExample(final T exampleInstance) {
        List<T> result = createCriteria(Example.create(exampleInstance)).list();
        return result;
    }

    @Override
    public List<T> findByNamedQuery(final String queryName, final Object... params) {
        Query namedQuey = getCurrentSession().getNamedQuery(queryName);

        for (int i = 0; i < params.length; i++) {
            namedQuey.setParameter(i + 1, params[i]);
        }

        final List<T> result = namedQuey.list();
        return result;
    }

    @Override
    public List<T> findByNamedQueryAndNamedParams(final String queryName, final Map<String, ? extends Object> params) {
        Query namedQuey = getCurrentSession().getNamedQuery(queryName);

        for (final Map.Entry<String, ? extends Object> param : params.entrySet()) {
            namedQuey.setParameter(param.getKey(), param.getValue());
        }

        final List<T> result = namedQuey.list();
        return result;
    }

    @Override
    public long countAll() {
        return countByCriteria();
    }

    @Override
    public long countByExample(final T exampleInstance) {
        return countByCriteria(Example.create(exampleInstance));
    }

    @Override
    public void save(final T entity) {
        getCurrentSession().persist(entity);
    }

    @Override
    public void update(final T entity) {
        getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(final T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(final T entity) {
        getCurrentSession().delete(entity);
    }

    /**
     * Convenience method for retrieving the current session.
     *
     * @return the current session
     */
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Convenience method.
     */
    protected T findUniqueByCriteria(final Criterion... criterion) {
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * Convenience method.
     */
    protected List<T> findByCriteria(final Criterion... criterion) {
        return findByCriteria(-1, -1, criterion);
    }

    /**
     * Convenience method.
     */
    protected List<T> findByCriteria(final int firstResult,
            final int maxResults, final Criterion... criterion) {
        Criteria crit = createCriteria(criterion);

        if (firstResult > 0) {
            crit.setFirstResult(firstResult);
        }

        if (maxResults > 0) {
            crit.setMaxResults(maxResults);
        }

        final List<T> result = crit.list();
        return result;
    }

    /**
     * Convenience method.
     */
    protected long countByCriteria(final Criterion... criterion) {
        Criteria crit = createCriteria(criterion);
        crit.setProjection(Projections.rowCount());
        return (Long) crit.uniqueResult();
    }

    @Override
    public void lock(final T entity, final LockOptions lockOptions) {
        getCurrentSession().buildLockRequest(lockOptions).lock(entity);
    }
}
