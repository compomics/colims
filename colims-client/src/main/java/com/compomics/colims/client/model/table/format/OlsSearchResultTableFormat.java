package com.compomics.colims.client.model.table.format;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import com.compomics.colims.core.ontology.ols.OlsSearchResult;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * This class represents the format of ProteinGroup instances shown in the
 * protein group table.
 * <p/>
 * Created by Niels Hulstaert.
 */
public class OlsSearchResultTableFormat implements AdvancedTableFormat<OlsSearchResult> {

    public final static String[] COLUMN_NAMES = {"ontology", "accession", "match(es)"};
    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";
    public static final int ONTOLOGY_NAMESPACE = 0;
    public static final int TERM_ACCESSION = 1;
    public static final int MATCHES = 2;

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case ONTOLOGY_NAMESPACE:
                return String.class;
            case TERM_ACCESSION:
                return String.class;
            case MATCHES:
                return String.class;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }

    @Override
    public Comparator getColumnComparator(int column) {
        return GlazedLists.comparableComparator();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getColumnValue(OlsSearchResult searchResult, int column) {
        switch (column) {
            case ONTOLOGY_NAMESPACE:
                return searchResult.getOntologyTerm().getOntologyNamespace();
            case TERM_ACCESSION:
                return searchResult.getOntologyTerm().getShortForm();
            case MATCHES:
                String matches = "";
                if (searchResult.getMatchedFields().size() == 1) {
                    matches = HTML_OPEN + searchResult.getMatchedFields().values().stream().collect(Collectors.toList()).get(0) + HTML_CLOSE;
                } else if (searchResult.getMatchedFields().size() > 1) {
                    matches = HTML_OPEN + searchResult.getMatchedFields().entrySet().stream().map(e -> {
                        return e.getKey().getQueryValue() + ": " + e.getValue();
                    }).collect(Collectors.joining(", ")) + HTML_CLOSE;
                }
                return matches.isEmpty() ? "not available" : matches;
            default:
                throw new IllegalArgumentException("Unexpected column number " + column);
        }
    }
}
