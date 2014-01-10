
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.AbstractDatabaseEntity;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class IdComparator implements Comparator<AbstractDatabaseEntity>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final AbstractDatabaseEntity ade1, final AbstractDatabaseEntity ade2) {
        return Long.compare(ade1.getId(), ade2.getId());
    }

}
