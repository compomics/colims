package com.compomics.colims.client.model.table.model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A model to define the data to be exported for the PeptideTableRow object.
 * <p/>
 * Created by Niels Hulstaert.
 */
public class QueryResultsTableModel extends AbstractTableModel {

    /**
     * The table column names.
     */
    private String[] columnNames;
    /**
     * The query results as a list of arrays. One entry represents a result row.
     */
    private List<Object[]> resultData = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param queryResults the query results list
     */
    public QueryResultsTableModel(List<LinkedHashMap<String, Object>> queryResults) {
        //get column names
        if (!queryResults.isEmpty()) {
            columnNames = queryResults.get(0).keySet().stream().toArray(String[]::new);
        }
        queryResults.stream().forEach((result) -> {
            resultData.add(result.values().stream().toArray(Object[]::new));
        });
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<Object[]> getResultData() {
        return resultData;
    }

    /**
     * Clear the table model.
     */
    public void clear() {
        columnNames = new String[0];
        resultData = new ArrayList<>();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return resultData.size();
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
        return resultData.get(rowIndex)[columnIndex];
    }
}
