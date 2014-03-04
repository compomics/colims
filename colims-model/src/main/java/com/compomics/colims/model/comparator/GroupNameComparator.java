
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Group;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class GroupNameComparator implements Comparator<Group>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final Group group1, final Group group2) {
        return group1.getName().compareToIgnoreCase(group2.getName());
    }

}
