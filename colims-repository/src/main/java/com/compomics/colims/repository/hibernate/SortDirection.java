package com.compomics.colims.repository.hibernate;

/**
 * This enum represents the sort directions for querying the database.
 * <p/>
 * Created by Niels Hulstaert on 14/10/15.
 */
public enum SortDirection {

    ASCENDING("asc"), DESCENDING("desc");

    /**
     * The enum query value that is used for constructing the database query.
     */
    private final String queryValue;

    SortDirection(String queryValue) {
        this.queryValue = queryValue;
    }

    /**
     * Get the enum query value;
     *
     * @return the enum quey value
     */
    public String queryValue() {
        return queryValue;
    }

}
