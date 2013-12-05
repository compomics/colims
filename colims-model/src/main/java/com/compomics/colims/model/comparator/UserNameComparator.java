
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.User;
import java.io.Serializable;
import java.util.Comparator;


/**
 *
 * @author Niels Hulstaert
 */
public class UserNameComparator implements Comparator<User>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(User user1, User user2) {
        return user1.getName().compareToIgnoreCase(user2.getName());
    }

}
