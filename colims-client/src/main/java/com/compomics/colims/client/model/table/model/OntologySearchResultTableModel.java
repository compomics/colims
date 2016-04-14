package com.compomics.colims.client.model.table.model;

import com.compomics.colims.core.model.ols.OlsSearchResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niels Hulstaert
 */
public class OntologySearchResultTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ontology", "accession", "match(es)"};
    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";

    public static final int ONTOLOGY_NAMESPACE = 0;
    public static final int TERM_ACCESSION = 1;
    public static final int MATCHES = 2;

    private List<OlsSearchResult> searchResults = new ArrayList<>();

    public List<OlsSearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<OlsSearchResult> searchResults) {
        this.searchResults = searchResults;
        this.fireTableDataChanged();
    }

    /**
     * Remove the storage error with the given index.
     *
     * @param index the index of the storage error that needs to be removed.
     */
    public void remove(int index) {
        searchResults.remove(index);
        this.fireTableDataChanged();
    }

    /**
     * Remove all messages.
     */
    public void removeAll() {
        searchResults.clear();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return searchResults.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OlsSearchResult searchResult = searchResults.get(rowIndex);

        switch (columnIndex) {
            case ONTOLOGY_NAMESPACE:
                return searchResult.getOntologyTerm().getOntologyNamespace();
            case TERM_ACCESSION:
                return searchResult.getOntologyTerm().getShortForm();
            case MATCHES:
                String matches = HTML_OPEN + searchResult.getMatchedFields().entrySet().stream().map(e -> {
                    return e.getKey().getQueryValue() + ": " + e.getValue();
                    }).collect(Collectors.joining(", ")) + HTML_CLOSE;
                return matches.isEmpty() ? "not available" : matches;
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }
}
