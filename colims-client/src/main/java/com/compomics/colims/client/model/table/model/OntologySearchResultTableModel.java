package com.compomics.colims.client.model.table.model;

import com.compomics.colims.core.model.ols.SearchResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class OntologySearchResultTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ontology", "accession", "match field", "match highlight"};
    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";

    public static final int ONTOLOGY_NAMESPACE = 0;
    public static final int TERM_ACCESSION = 1;
    public static final int MATCH_FIELD = 2;
    public static final int MATCH_HIGHLIGHT = 3;

    private List<SearchResult> searchResults = new ArrayList<>();

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
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
        SearchResult searchResult = searchResults.get(rowIndex);

        switch (columnIndex) {
            case ONTOLOGY_NAMESPACE:
                return searchResult.getOntologyNamespace();
            case TERM_ACCESSION:
                return searchResult.getAccession();
            case MATCH_FIELD:
                return searchResult.getField().getQueryValue();
            case MATCH_HIGHLIGHT:
                return HTML_OPEN + searchResult.getHighlight() + HTML_CLOSE;
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }

    }
}
