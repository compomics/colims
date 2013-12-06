
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Role;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class RoleNameComparator implements Comparator<Role>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Role role1, Role role2) {
        return role1.getName().compareToIgnoreCase(role2.getName());
    }

}
