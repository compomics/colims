
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Permission;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class PermissionNameComparator implements Comparator<Permission>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final Permission permission1, final Permission permission2) {
        return permission1.getName().compareToIgnoreCase(permission2.getName());
    }

}
