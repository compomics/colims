
package com.compomics.colims.client.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class TypedCvParamTableModel2 extends AbstractTableModel {

    private static final String[] columnNames = {"ontology label", "accession", "name"};
    private static final int ONTOLOGY_LABEL_INDEX = 0;
    private static final int ACCESSION_INDEX = 1;
    private static final int NAME_INDEX = 2;

    private List<AuditableTypedCvParam> cvParams;

    public TypedCvParamTableModel2() {
        cvParams = new ArrayList<>();
    }

    public TypedCvParamTableModel2(List<AuditableTypedCvParam> cvParams) {
        this.cvParams = cvParams;
    }

    public List<AuditableTypedCvParam> getCvParams() {
        return cvParams;
    }

    public void setCvParams(List<AuditableTypedCvParam> cvParams) {
        this.cvParams = cvParams;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return cvParams.size();
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
        AuditableTypedCvParam selectedCvParam = cvParams.get(rowIndex);

        switch (columnIndex) {
             case ONTOLOGY_LABEL_INDEX:
                return selectedCvParam.getLabel();
             case ACCESSION_INDEX:
                return selectedCvParam.getAccession();
             case NAME_INDEX:
                return selectedCvParam.getName();
             default:
                throw new IllegalArgumentException("Invalid column index: " + columnIndex);
         }
    }

    /**
     * Add a CV param as the last entry.
     *
     * cvParam the CV param to add
     */
    public void addCvParam(AuditableTypedCvParam cvParam){
        //add CV param to list
        cvParams.add(cvParam);
        fireTableRowsInserted(cvParams.size() - 1, cvParams.size() - 1);
    }

    /**
     * Update a CV param already present in the table.
     *
     * @param cvParam the CV param to update
     * @param rowIndex the row index of the CV param to update
     */
    public void updateCvParam(AuditableTypedCvParam cvParam, int rowIndex){
        cvParams.set(rowIndex, cvParam);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * Remove the CV param with the given row index from the model.
     *
     * @param rowIndex
     */
    public void removeCvParam(int rowIndex){
        cvParams.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
