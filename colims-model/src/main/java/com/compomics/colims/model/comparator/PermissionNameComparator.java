package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Permission;
import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares name fields of Permission entity instances.
 *
 * @author Niels Hulstaert
 */
public class PermissionNameComparator implements Comparator<Permission>, Serializable {

    @Override
    public int compare(final Permission permission1, final Permission permission2) {
        return permission1.getName().compareToIgnoreCase(permission2.getName());
    }

}
