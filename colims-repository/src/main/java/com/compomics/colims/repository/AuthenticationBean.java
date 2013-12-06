package com.compomics.colims.repository;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import org.springframework.stereotype.Component;

import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultPermission;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author niels
 */
@Component("authenticationBean")
public class AuthenticationBean {

    /**
     * The logged in user.
     */
    private User currentUser;
    /**
     * The default permissions map (key: default permission enum; value: does
     * the user has the default permission?)
     */
    private Map<DefaultPermission, Boolean> defaultPermissions;

    public AuthenticationBean() {
        //set a default user
        currentUser = new User("N/A");

        defaultPermissions = new HashMap<>();
        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            defaultPermissions.put(defaultPermission, Boolean.FALSE);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the current user 
     * 
     * @param currentUser 
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;

        //iterate over the default permissions
        defaultpermissionloop:
        for (DefaultPermission defaultPermission : defaultPermissions.keySet()) {
            grouploop:
            for (Group group : currentUser.getGroups()) {
                for (Role role : group.getRoles()) {
                    for (Permission permission : role.getPermissions()) {
                        if (permission.getName().equals(defaultPermission.getDbEntry())) {
                            defaultPermissions.put(defaultPermission, Boolean.TRUE);
                            break grouploop;
                        }
                    }
                }
            }
        }
    }

    public Map<DefaultPermission, Boolean> getDefaultPermissions() {
        return defaultPermissions;
    }

    public void setDefaultPermissions(Map<DefaultPermission, Boolean> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
