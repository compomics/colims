package com.compomics.colims.repository.impl;

import com.compomics.colims.model.UserQuery;
import com.compomics.colims.model.UserQuery_;
import com.compomics.colims.model.User_;
import com.compomics.colims.repository.UserQueryRepository;
import com.compomics.colims.repository.hibernate.LinkedAliasToEntityMapResultTransformer;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.jws.soap.SOAPBinding;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Niels Hulstaert on 5/11/15.
 */
@Repository("userQueryRepository")
public class UserQueryJpaRepository extends GenericJpaRepositoryImpl<UserQuery, Long> implements UserQueryRepository {

    @Override
    public List<LinkedHashMap<String, Object>> executeUserQuery(String queryString, Integer maxResults) {
        //create and setup the return for the entered query
        SQLQuery userQuery = getEntityManager().unwrap(Session.class).createSQLQuery(queryString);
        userQuery.setResultTransformer(LinkedAliasToEntityMapResultTransformer.INSTANCE());
        userQuery.setMaxResults(maxResults);

        return userQuery.list();
    }

    @Override
    public List<UserQuery> findByUserId(Long userId) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserQuery> criteriaQuery = criteriaBuilder.createQuery(UserQuery.class);
        Root<UserQuery> userQueryRoot = criteriaQuery.from(UserQuery.class);

        ParameterExpression<Long> userIdParam = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(
                criteriaBuilder.equal(userQueryRoot.get(UserQuery_.user).get(User_.id), userIdParam)
        );

        //order by usage count
        criteriaQuery.orderBy(criteriaBuilder.desc(userQueryRoot.get(UserQuery_.usageCount)));

        TypedQuery<UserQuery> query = getEntityManager().createQuery(criteriaQuery);
        query.setParameter(userIdParam, userId);

        return query.getResultList();
    }

    @Override
    public Long countByUserId(Long userId) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserQuery> userQueryRoot = criteriaQuery.from(UserQuery.class);

        criteriaQuery.select(criteriaBuilder.count(userQueryRoot));

        ParameterExpression<Long> userIdParam = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(
                criteriaBuilder.equal(userQueryRoot.get(UserQuery_.user).get(User_.id), userIdParam)
        );

        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        query.setParameter(userIdParam, userId);

        return query.getSingleResult();
    }

    @Override
    public UserQuery findByUserIdAndQueryString(Long userId, String queryString) {
        UserQuery userQuery = null;

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserQuery> criteriaQuery = criteriaBuilder.createQuery(UserQuery.class);
        Root<UserQuery> userQueryRoot = criteriaQuery.from(UserQuery.class);

        ParameterExpression<Long> userIdParam = criteriaBuilder.parameter(Long.class);
        ParameterExpression<String> queryStringParam = criteriaBuilder.parameter(String.class);
        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(userQueryRoot.get(UserQuery_.user).get(User_.id), userIdParam),
                        criteriaBuilder.equal(userQueryRoot.get(UserQuery_.queryString), queryStringParam)
                )
        );

        TypedQuery<UserQuery> query = getEntityManager().createQuery(criteriaQuery);
        query.setParameter(userIdParam, userId);
        query.setParameter(queryStringParam, queryString);

        try {
            userQuery = query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing
        }
        return userQuery;
    }

    @Override
    public void removeLeastUsedUserQuery(Long userId) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserQuery> criteriaQuery = criteriaBuilder.createQuery(UserQuery.class);
        Root<UserQuery> userQueryRoot = criteriaQuery.from(UserQuery.class);

        ParameterExpression<Long> userIdParam = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(
                criteriaBuilder.equal(userQueryRoot.get(UserQuery_.user).get(User_.id), userIdParam)
        );

        //order by usage count and modification date
        criteriaQuery.orderBy(criteriaBuilder.asc(userQueryRoot.get(UserQuery_.usageCount)), criteriaBuilder.asc(userQueryRoot.get(UserQuery_.modificationDate)));

        TypedQuery<UserQuery> query = getEntityManager().createQuery(criteriaQuery);
        query.setParameter(userIdParam, userId);
        query.setFirstResult(0);
        query.setMaxResults(1);

        UserQuery userQuery = null;
        try {
            userQuery = query.getSingleResult();
        } catch (NoResultException e) {
            //do nothing
        }

        if(userQuery != null){
            remove(userQuery);
        }
    }
}
