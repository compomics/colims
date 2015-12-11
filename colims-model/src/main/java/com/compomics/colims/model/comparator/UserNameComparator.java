package com.compomics.colims.model.comparator;

import com.compomics.colims.model.User;
import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares name fields of User entity instances.
 *
 * @author Niels Hulstaert
 */
public class UserNameComparator implements Comparator<User>, Serializable {

    @Override
    public int compare(final User user1, final User user2) {
        return user1.getName().compareToIgnoreCase(user2.getName());
    }

}
