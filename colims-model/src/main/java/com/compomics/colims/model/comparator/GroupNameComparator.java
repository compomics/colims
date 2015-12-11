package com.compomics.colims.model.comparator;

import com.compomics.colims.model.Group;
import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares group name fields of Group entity instances.
 *
 * @author Niels Hulstaert
 */
public class GroupNameComparator implements Comparator<Group>, Serializable {

    @Override
    public int compare(final Group group1, final Group group2) {
        return group1.getName().compareToIgnoreCase(group2.getName());
    }

}
