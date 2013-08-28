
package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class AbstractCvTermTableModel<T> extends AbstractTableModel {                
    
    protected final String[] columnNames = {"type", "label", "accession", "name"};
    protected List<CvTerm> cVTerms;

    public AbstractCvTermTableModel() {
        cVTerms = new ArrayList<>();
    }
    
    /**
     * Init the table model
     * 
     * @param t the enitity that has CV terms
     */
    public abstract void init(T t);
    
    @Override
    public int getRowCount() {
        return cVTerms.size();
    }        
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }        

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
            
}
