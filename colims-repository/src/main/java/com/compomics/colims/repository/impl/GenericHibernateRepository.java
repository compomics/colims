package com.compomics.colims.repository.impl;

import com.compomics.colims.repository.GenericRepository;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public T findById(ID id) {
        return (T) getCurrentSession().get(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return findByCriteria();
    }

    @Override
    public List<T> findByExample(T exampleInstance) {
        Criteria crit = getCurrentSession().createCriteria(getEntityClass());
        final List<T> result = crit.list();
        return result;
    }

    @Override
    public List<T> findByNamedQuery(String queryName, Object... params) {
        Query namedQuey = getCurrentSession().getNamedQuery(queryName);

        for (int i = 0; i < params.length; i++) {
            namedQuey.setParameter(i + 1, params[i]);
        }

        final List<T> result = (List<T>) namedQuey.list();
        return result;
    }

    @Override
    public List<T> findByNamedQueryAndNamedParams(String queryName, Map<String, ? extends Object> params) {
        Query namedQuey = getCurrentSession().getNamedQuery(queryName);

        for (final Map.Entry<String, ? extends Object> param : params.entrySet()) {
            namedQuey.setParameter(param.getKey(), param.getValue());
        }

        final List<T> result = (List<T>) namedQuey.list();
        return result;
    }

    @Override
    public int countAll() {
        return countByCriteria();
    }

    @Override
    public int countByExample(T exampleInstance) {
        Criteria crit = getCurrentSession().createCriteria(getEntityClass());
        crit.setProjection(Projections.rowCount());
        crit.add(Example.create(exampleInstance));

        return (Integer) crit.list().get(0);
    }

    @Override
    public void save(T entity) {
        getCurrentSession().persist(entity);
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
    protected List<T> findByCriteria(final Criterion... criterion) {
        return findByCriteria(-1, -1, criterion);
    }

    /**
     * Convenience method.
     */
    protected List<T> findByCriteria(final int firstResult,
            final int maxResults, final Criterion... criterion) {
        Criteria crit = getCurrentSession().createCriteria(getEntityClass());

        for (final Criterion c : criterion) {
            crit.add(c);
        }

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
    protected int countByCriteria(Criterion... criterion) {
        Criteria crit = getCurrentSession().createCriteria(getEntityClass());
        crit.setProjection(Projections.rowCount());

        for (final Criterion c : criterion) {
            crit.add(c);
        }

        return ((Number) (crit.list().get(0))).intValue();
    }
}
