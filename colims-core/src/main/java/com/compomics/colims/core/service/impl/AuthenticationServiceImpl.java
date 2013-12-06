/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.*;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * The list of default permissions names
     */
    private List<String> defaultPermissions;
    /**
     * The authentication bean that holds the currently logged in user.
     */
    private AuthenticationBean authenticationBean;

    public AuthenticationServiceImpl() {
        defaultPermissions = new ArrayList<>();
        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            defaultPermissions.add(defaultPermission.getDbEntry());
        }
    }

    @Override
    public boolean hasDeletePermission() {
        boolean hasDeletePermission = false;

        outerloop:
        for (Group group : authenticationBean.getCurrentUser().getGroups()) {
            for (Role role : group.getRoles()) {
                for (Permission permission : role.getPermissions()) {
                    if (permission.getName().equals(DefaultPermission.DELETE.getDbEntry())) {
                        hasDeletePermission = true;
                        break outerloop;
                    }
                }
            }
        }

        return hasDeletePermission;
    }
}
