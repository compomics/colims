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
     * @param maxResults the maximum result restriction
     * @return the query results
     */
    List<LinkedHashMap<String, Object>> executeUserQuery(String queryString, Integer maxResults);

    /**
     * Get all the queries for the given user. They are sorted on usage count.
     *
     * @param userId the user ID
     * @return the found UserQuery instances
     */
    List<UserQuery> findByUserId(Long userId);

    /**
     * Count the number queries for the given user.
     *
     * @param userId the user ID
     * @return the number of user queries
     */
    Long countByUserId(Long userId);

    /**
     * Find a query for a given user. Returns null if nothing was found.
     *
     * @param userId      the user ID
     * @param queryString the query String
     * @return the found UserQuery instance
     */
    UserQuery findByUserIdAndQueryString(Long userId, String queryString);

    /**
     * Remove the least used user query for the given user.
     *
     * @param userId the user ID
     */
    void removeLeastUsedUserQuery(Long userId);
}
