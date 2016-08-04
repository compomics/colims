package com.compomics.colims.model;

import com.compomics.colims.model.enums.DefaultGroup;
import com.compomics.colims.model.enums.DefaultPermission;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the logged in user and his/her credentials.
 *
 * @author Niels Hulstaert
 */
@Component("userBean")
public class UserBean {

    public static final String USER_NOT_AVAILABLE = "N/A";

    /**
     * The logged in user.
     */
    private User currentUser;
    /**
     * The default permissions map (key: default permission Enum; value: does the user has the default permission?).
     */
    private Map<DefaultPermission, Boolean> defaultPermissions = new HashMap<>();

    /**
     * No-arg constructor.
     */
    public UserBean() {
        //set a default user
        currentUser = new User(USER_NOT_AVAILABLE);

        resetDefaultPermissions();
    }

    /**
     * Get the currently logged in user.
     *
     * @return the logged in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the current user and update the default permissions.
     *
     * @param currentUser the logged in in User instance
     */
    public void setCurrentUser(final User currentUser) {
        this.currentUser = currentUser;

        //iterate over the default permissions
        for (DefaultPermission defaultPermission : defaultPermissions.keySet()) {
            grouploop:
            for (Group group : currentUser.getGroups()) {
                for (Role role : group.getRoles()) {
                    for (Permission permission : role.getPermissions()) {
                        if (permission.getName().equals(defaultPermission.dbEntry())) {
                            defaultPermissions.put(defaultPermission, true);
                            break grouploop;
                        }
                    }
                }
            }
        }
    }

    /**
     * Return the default permissions of the logged in user.
     *
     * @return the default permissions map
     */
    public Map<DefaultPermission, Boolean> getDefaultPermissions() {
        return defaultPermissions;
    }

    /**
     * Check is the user belongs to the default admin group.
     *
     * @return is the user an admin
     */
    public boolean isAdmin() {
        boolean isAdmin = false;

        for (Group group : currentUser.getGroups()) {
            if (group.getName().equals(DefaultGroup.ADMIN.dbEntry())) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    /**
     * Reset the default permissions map; all default permissions are set to false.
     */
    private void resetDefaultPermissions() {
        defaultPermissions = new HashMap<>();
        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            defaultPermissions.put(defaultPermission, Boolean.FALSE);
        }
    }
}
