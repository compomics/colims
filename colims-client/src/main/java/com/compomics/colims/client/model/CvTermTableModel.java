package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermTableModel extends AbstractTableModel {

    private final String[] columnNames = {"type", "ontology label", "accession", "name"};
    private static final int TYPE_INDEX = 0;
    private static final int ONTOLOGY_LABEL_INDEX = 1;
    private static final int ACCESSION_INDEX = 2;
    private static final int NAME_INDEX = 3;
    private List<CvTerm> cvTerms;

    public CvTermTableModel() {
        cvTerms = new ArrayList<>();
    }

    public CvTermTableModel(List<CvTerm> cvTerms) {
        this.cvTerms = cvTerms;
    }

    public List<CvTerm> getCvTerms() {
        return cvTerms;
    }

    public void setCvTerms(List<CvTerm> cvTerms) {
        this.cvTerms = cvTerms;
    }

    @Override
    public int getRowCount() {
        return cvTerms.size();
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
        CvTerm selectedCvTerm = cvTerms.get(rowIndex);
                
        switch (columnIndex) {
            case TYPE_INDEX:                
                return selectedCvTerm.getcvTermType();
            case ONTOLOGY_LABEL_INDEX:
                return selectedCvTerm.getLabel();
            case ACCESSION_INDEX:
                return selectedCvTerm.getAccession();
            case NAME_INDEX:
                return selectedCvTerm.getName();
            default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        }
    }
}
