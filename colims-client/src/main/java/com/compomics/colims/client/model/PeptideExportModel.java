package com.compomics.colims.client.model;

import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A model to define the data to be exported for the PeptideTableRow object
 *
 * Created by Iain on 17/09/2015.
 */
public class PeptideExportModel extends AbstractTableModel {
    private static final String[] columnNames = {"Sequence", "Charge", "Spectra", "Fixed Modifications", "Variable Modifications"};

    private List<PeptideTableRow> peptideTableRows = new ArrayList<>();

    @Override
    public int getRowCount() {
        return peptideTableRows.size();
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
        PeptideTableRow peptideTableRow = peptideTableRows.get(rowIndex);
        String modifications = "";

        switch (columnIndex) {
            case 0:
                return peptideTableRow.getSequence();
            case 1:
                return peptideTableRow.getCharge();
            case 2:
                return peptideTableRow.getSpectrumCount();
            case 3:
            case 4:
                for (PeptideHasModification peptideHasModification : peptideTableRow.getPeptideHasModifications()) {
                    if (peptideHasModification.getModificationType() == (columnIndex == 3 ? ModificationType.FIXED : ModificationType.VARIABLE)) {
                        modifications += (modifications.length() > 0 ? ", " : "")
                            + "(" + peptideHasModification.getModification().getName()
                            + ", " + peptideHasModification.getLocation() + ")";
                    }
                }

                return modifications;
            default:
                throw new IllegalArgumentException("Unexpected column number " + columnIndex);
        }
    }

    public void setPeptides(List<PeptideTableRow> peptideTableRows) {
        this.peptideTableRows = peptideTableRows;
    }
}
