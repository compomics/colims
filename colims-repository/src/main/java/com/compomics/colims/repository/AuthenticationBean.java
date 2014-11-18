package com.compomics.colims.repository;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import org.springframework.stereotype.Component;

import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultGroup;
import com.compomics.colims.model.enums.DefaultPermission;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the logged in user and his/her credentials.
 *
 * @author Niels Hulstaert
 */
@Component("authenticationBean")
public class AuthenticationBean {

    /**
     * The logged in user.
     */
    private User currentUser;
    /**
     * The default permissions map (key: default permission Enum; value: does
     * the user has the default permission?).
     */
    private Map<DefaultPermission, Boolean> defaultPermissions = new HashMap<>();

    /**
     * No-arg constructor.
     */
    public AuthenticationBean() {
        //set a default user
        currentUser = new User("N/A");

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
     * Reset the default permissions map; all default permissions are set to
     * false.
     */
    private void resetDefaultPermissions() {
        defaultPermissions = new HashMap<>();
        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            defaultPermissions.put(defaultPermission, Boolean.FALSE);
        }
    }
}
