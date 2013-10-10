
package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermWithoutTypeTableModel extends AbstractTableModel {                
    
    private static final String[] columnNames = {"ontology label", "accession", "name"};
    private static final int ONTOLOGY_LABEL_INDEX = 0;
    private static final int ACCESSION_INDEX = 1;
    private static final int NAME_INDEX = 2;
    
    private List<CvTerm> cvTerms;

    public CvTermWithoutTypeTableModel() {
        cvTerms = new ArrayList<>();
    }
    
    public CvTermWithoutTypeTableModel(List<CvTerm> cvTerms) {
        this.cvTerms = cvTerms;
    }

    public List<CvTerm> getCvTerms() {
        return cvTerms;
    }

    public void setCvTerms(List<CvTerm> cvTerms) {
        this.cvTerms = cvTerms;
        fireTableDataChanged();
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
    
    /**
     * Add a CV term as the last entry.
     * 
     * @param cvTerm the CV term to add 
     */
    public void addCvTerm(CvTerm cvTerm){
        //add CV term to list
        cvTerms.add(cvTerm);
        fireTableRowsInserted(cvTerms.size() - 1, cvTerms.size() - 1);
    }
    
    /**
     * Update a CV term already present in the table.
     * 
     * @param cvTerm the CV term to update 
     * @param rowIndex the row index of the CV term to update
     */
    public void updateCvTerm(CvTerm cvTerm, int rowIndex){
        cvTerms.set(rowIndex, cvTerm);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    /**
     * Remove the CV term with the given row index from the model.
     * 
     * @param rowIndex 
     */
    public void removeCvTerm(int rowIndex){
        cvTerms.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
