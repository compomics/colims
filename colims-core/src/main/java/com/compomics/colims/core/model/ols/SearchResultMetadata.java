package com.compomics.colims.core.model.ols;

/**
 * This class represents the metadata from an Ontology Lookup Service (OLS)
 * search.
 *
 * @author Niels Hulstaert
 */
public class SearchResultMetadata {

    /**
     * The number of search result pages.
     */
    private int numberOfResultPages;
    /**
     * The OLS REST request.
     */
    private String requestUrl;

    /**
     * Constructor.
     *
     * @param numberOfResults the number of search results
     * @param requestUrl the rest request URL
     */
    public SearchResultMetadata(int numberOfResults, String requestUrl) {
        this.numberOfResultPages = numberOfResults;
        this.requestUrl = requestUrl;
    }

    public int getNumberOfResultPages() {
        return numberOfResultPages;
    }

    public void setNumberOfResultPages(int numberOfResultPages) {
        this.numberOfResultPages = numberOfResultPages;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

}
