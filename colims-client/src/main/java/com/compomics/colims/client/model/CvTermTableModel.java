
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
    
    private final String[] columnNames = {"type", "label", "accession", "name"};
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
        return selectedCvTerm.toStringArray()[columnIndex];
    }
            
}
