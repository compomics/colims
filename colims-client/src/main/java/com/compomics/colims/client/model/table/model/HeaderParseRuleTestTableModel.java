package com.compomics.colims.client.model.table.model;

import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class HeaderParseRuleTestTableModel extends AbstractTableModel {

    private final String[] columnNames = {"header", "parsed accession"};
    private static final int HEADER_INDEX = 0;
    private static final int PARSED_ACCESSION_INDEX = 1;
    private Map<String, String> parsedAccessions;
    private String[] keys;

    public HeaderParseRuleTestTableModel() {
    }

    public HeaderParseRuleTestTableModel(Map<String, String> parsedAccessions) {
        this.parsedAccessions = parsedAccessions;
        this.keys = parsedAccessions.keySet().toArray(new String[parsedAccessions.size()]);
    }

    public void setParsedAccessions(Map<String, String> parsedAccessions) {
        this.parsedAccessions = parsedAccessions;
        this.keys = parsedAccessions.keySet().toArray(new String[parsedAccessions.size()]);
    }

    @Override
    public int getRowCount() {
        return parsedAccessions.size();
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
        String header = keys[rowIndex];

        switch (columnIndex) {
            case HEADER_INDEX:
                return header;
            case PARSED_ACCESSION_INDEX:
                return parsedAccessions.get(header);
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }
}
