package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Role;
import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares name fields of Role entity instances.
 *
 * @author Niels Hulstaert
 */
public class RoleNameComparator implements Comparator<Role>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final Role role1, final Role role2) {
        return role1.getName().compareToIgnoreCase(role2.getName());
    }

}
