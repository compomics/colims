package com.compomics.colims.core.service;

import com.compomics.colims.model.User;
import com.compomics.colims.model.UserQuery;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This interface provides service methods for executing user queries.
 *
 * @author Niels Hulstaert
 */
public interface UserQueryService extends GenericService<UserQuery, Long> {

    /**
     * Execute the given user query string and return the query results. Checks if the query was already saved for the
     * given user; update the usage count if true, persist the query if false.
     *
     * @param user        the current user
     * @param queryString the user query String
     * @return the query results
     */
    List<LinkedHashMap<String, Object>> executeUserQuery(User user, String queryString);

    /**
     * Get all the queries for the given user. They are sorted on usage count.
     *
     * @param userId the user ID
     * @return the found query strings
     */
    List<String> findQueriesByUserId(Long userId);

}
