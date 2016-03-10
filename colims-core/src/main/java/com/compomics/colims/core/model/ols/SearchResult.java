package com.compomics.colims.core.model.ols;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an ontology term highlight returned by the Ontology
 * Lookup Service (OLS) as result of a search query.
 *
 * @author Niels Hulstaert
 */
public class SearchResult {

    /**
     * The default search fields when querying the OLS.
     */
    public static final EnumSet<SearchField> DEFAULT_SEARCH_FIELDS = EnumSet.of(SearchField.LABEL, SearchField.SYNONYM, SearchField.DESCRIPTION, SearchField.IDENTIFIER, SearchField.ANNOTATION_PROPERTY);

    /**
     * This enum represents the available search fields for querying ontology
     * terms.
     */
    public enum SearchField {

        LABEL("label"), SYNONYM("synonym"), DESCRIPTION("description"), IDENTIFIER("identifier"), ANNOTATION_PROPERTY("annotation property");

        /**
         * The query value of the search field.
         */
        private final String queryValue;

        private static final Map<String, SearchField> map;

        static {
            map = new HashMap<>();
            for (SearchField searchField : SearchField.values()) {
                map.put(searchField.queryValue, searchField);
            }
        }

        /**
         * Constructor.
         *
         * @param queryValue
         */
        private SearchField(String queryValue) {
            this.queryValue = queryValue;
        }

        public static SearchField findByQueryValue(String queryValue) {
            return map.get(queryValue);
        }

        public String getQueryValue() {
            return queryValue;
        }

    }

    /**
     * The ontology namespace.
     */
    private String ontologyNamespace;
    /**
     * The ontology term iri.
     */
    private String iri;
    /**
     * The field of the ontology term that matched the search query.
     */
    private SearchField field;
    /**
     * The ontology term field with the matched search query in HTML bold tags.
     */
    private String highlight;

    /**
     * No-arg constructor.
     */
    public SearchResult() {
    }

    public String getOntologyNamespace() {
        return ontologyNamespace;
    }

    public void setOntologyNamespace(String ontologyNamespace) {
        this.ontologyNamespace = ontologyNamespace;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public SearchField getField() {
        return field;
    }

    public void setField(SearchField field) {
        this.field = field;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    /**
     * Get the ontology term accession from the IRI.
     *
     * @return the term accession
     */
    public String getAccession() {
        return iri.substring(iri.lastIndexOf('/') + 1);
    }

}
