package com.compomics.colims.repository.impl;

import com.compomics.colims.model.UserQuery;
import com.compomics.colims.model.UserQuery_;
import com.compomics.colims.model.User_;
import com.compomics.colims.repository.UserQueryRepository;
import com.compomics.colims.repository.hibernate.LinkedAliasToEntityMapResultTransformer;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
    public List<LinkedHashMap<String, Object>> executeQuery(String queryString) {
        //create and setup the return for the entered query
        SQLQuery userQuery = getEntityManager().unwrap(Session.class).createSQLQuery(queryString);
        userQuery.setResultTransformer(LinkedAliasToEntityMapResultTransformer.INSTANCE());

        return userQuery.list();
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
}
