package com.compomics.colims.repository;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This interface provides repository methods for executing user queries.
 * <p/>
 * Created by Niels Hulstaert on 5/11/15.
 */
public interface UserQueryRepository {

    /**
     * Execute the given user query string and return the query results.
     *
     * @param queryString the user query String
     * @return the query results
     */
    List<LinkedHashMap<String, Object>> executeQuery(String queryString);
}
