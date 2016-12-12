package com.compomics.colims.core.ontology.ols;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an ontology term highlight returned by the Ontology
 * Lookup Service (OLS) as result of a search query.
 *
 * @author Niels Hulstaert
 */
public class OlsSearchResult {

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
        SearchField(String queryValue) {
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
     * The search result ontology term.
     */
    private OntologyTerm ontologyTerm;
    /**
     * The map with the matched ontology term fields (key: the search field;
     * value: the search query matched highlighted text).
     */
    private EnumMap<SearchField, String> matchedFields = new EnumMap<>(SearchField.class);

    /**
     * No-arg constructor.
     */
    public OlsSearchResult() {
    }

    public OntologyTerm getOntologyTerm() {
        return ontologyTerm;
    }

    public void setOntologyTerm(OntologyTerm ontologyTerm) {
        this.ontologyTerm = ontologyTerm;
    }

    public EnumMap<SearchField, String> getMatchedFields() {
        return matchedFields;
    }

    public void setMatchedFields(EnumMap<SearchField, String> matchedFields) {
        this.matchedFields = matchedFields;
    }

}
