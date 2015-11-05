package com.compomics.colims.core.service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This interface provides service methods for executing user queries.
 *
 * @author Niels Hulstaert
 */
public interface UserQueryService {

    /**
     * Execute the given user query string and return the query results.
     *
     * @param queryString the user query String
     * @return the query results
     */
    List<LinkedHashMap<String, Object>> executeQuery(String queryString);

}
