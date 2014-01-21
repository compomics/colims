
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.DatabaseEntity;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class IdComparator implements Comparator<DatabaseEntity>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final DatabaseEntity ade1, final DatabaseEntity ade2) {
        return Long.compare(ade1.getId(), ade2.getId());
    }

}
