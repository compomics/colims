package com.compomics.colims.repository.impl;

import com.compomics.colims.repository.UserQueryRepository;
import com.compomics.colims.repository.hibernate.LinkedAliasToEntityMapResultTransformer;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Niels Hulstaert on 5/11/15.
 */
@Repository("userQueryRepository")
public class UserQueryHibernateRepository implements UserQueryRepository {

    /**
     * The JPA entityManagerFactory instance.
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<LinkedHashMap<String, Object>> executeQuery(String queryString) {
        //create and setup the return for the entered query
        SQLQuery userQuery = entityManager.unwrap(Session.class).createSQLQuery(queryString);
        userQuery.setResultTransformer(LinkedAliasToEntityMapResultTransformer.INSTANCE());

        return userQuery.list();
    }
}
