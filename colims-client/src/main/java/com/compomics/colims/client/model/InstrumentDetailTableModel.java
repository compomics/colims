
package com.compomics.colims.client.model;

import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Niels Hulstaert
 */
public class InstrumentDetailTableModel extends AbstractCvTermTableModel<Instrument> {    

    public InstrumentDetailTableModel() {        
    }        
    
    @Override
    public void init(Instrument t) {        
        cVTerms.add(t.getSource());
        cVTerms.add(t.getDetector());
        for(InstrumentCvTerm analyzer : t.getAnalyzers()){
            cVTerms.add(analyzer);
        }
    }        

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CvTerm cvTerm = cVTerms.get(rowIndex);
        String[] stringArray = cvTerm.toStringArray();        
        return stringArray[columnIndex];
    }
    
}
