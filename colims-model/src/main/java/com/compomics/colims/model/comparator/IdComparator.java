
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.DatabaseEntity;
import java.io.Serializable;
import java.util.Comparator;


/**
 * This comparator compares ID fields of DatabaseEntity instances.
 *
 * @author Niels Hulstaert
 */
public class IdComparator implements Comparator<DatabaseEntity>, Serializable{

    @Override
    public int compare(final DatabaseEntity ade1, final DatabaseEntity ade2) {
        return Long.compare(ade1.getId(), ade2.getId());
    }

}
