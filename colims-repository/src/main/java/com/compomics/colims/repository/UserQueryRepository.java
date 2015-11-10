package com.compomics.colims.repository;

import com.compomics.colims.model.UserQuery;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This interface provides repository methods for executing user queries.
 * <p/>
 * Created by Niels Hulstaert on 5/11/15.
 */
public interface UserQueryRepository extends GenericJpaRepository<UserQuery, Long> {

    /**
     * Execute the given user query string and return the query results.
     *
     * @param queryString the user query String
     * @return the query results
     */
    List<LinkedHashMap<String, Object>> executeQuery(String queryString);

    /**
     * Look in the database if the given query was already stored for the given user. Returns null if nothing was
     * found.
     *
     * @param userId the user ID
     * @param queryString the query String
     * @return the found UserQuery instance
     */
    UserQuery findByUserIdAndQueryString(Long userId, String queryString);
}
