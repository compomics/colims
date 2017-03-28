package com.compomics.colims.client.model.table.model;

import com.compomics.colims.model.PeptideHasModification;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A model to define the data to be exported for the PeptideTableRow object.
 * <p/>
 * Created by Iain on 17/09/2015.
 */
public class PeptideExportModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Sequence", "Charge", "Spectra", "Fixed Modifications", "Variable Modifications"};

    private List<PeptideTableRow> peptideTableRows = new ArrayList<>();

    public void setPeptideTableRows(List<PeptideTableRow> peptideTableRows) {
        this.peptideTableRows = peptideTableRows;
    }

    @Override
    public int getRowCount() {
        return peptideTableRows.size();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        PeptideTableRow peptideTableRow = peptideTableRows.get(rowIndex);
        StringBuilder modifications = new StringBuilder();

        switch (columnIndex) {
            case 0:
                return peptideTableRow.getSequence();
            case 2:
                return peptideTableRow.getSpectrumCount();
            case 3:
            case 4:
                for (PeptideHasModification peptideHasModification : peptideTableRow.getPeptideHasModifications()) {
                    //@// TODO: 30/09/16 remove if clause after refactoring
//                    if (peptideHasModification.getModificationType() == (columnIndex == 3 ? ModificationType.FIXED : ModificationType.VARIABLE)) {
                    modifications.append(modifications.length() > 0 ? ", " : "").append("(").append(peptideHasModification.getModification().getName()).append(", ").append(peptideHasModification.getLocation()).append(")");
//                    }
                }

                return modifications.toString();
            default:
                throw new IllegalArgumentException("Unexpected column number " + columnIndex);
        }
    }
}
