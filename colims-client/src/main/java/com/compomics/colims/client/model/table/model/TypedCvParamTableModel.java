package com.compomics.colims.client.model.table.model;

import com.compomics.colims.model.cv.AuditableTypedCvParam;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class TypedCvParamTableModel extends AbstractTableModel {

    private final String[] columnNames = {"type", "ontology label", "accession", "name"};
    private static final int TYPE_INDEX = 0;
    private static final int ONTOLOGY_LABEL_INDEX = 1;
    private static final int ACCESSION_INDEX = 2;
    private static final int NAME_INDEX = 3;
    private List<AuditableTypedCvParam> cvParams;

    public TypedCvParamTableModel() {
        cvParams = new ArrayList<>();
    }

    public TypedCvParamTableModel(List<AuditableTypedCvParam> cvParams) {
        this.cvParams = cvParams;
    }

    public List<AuditableTypedCvParam> getCvParams() {
        return cvParams;
    }

    public void setCvParams(List<AuditableTypedCvParam> cvParams) {
        this.cvParams = cvParams;
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
            case TYPE_INDEX:
                return selectedCvParam.getCvParamType();
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
}
