package com.compomics.colims.client.event;

import com.compomics.colims.model.Permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionChangeEvent extends EntityChangeEvent {

    private Permission permission;

    public PermissionChangeEvent(Type type, Permission permission) {
        this.permission = permission;
        this.type = type;
    }

    public Permission getPermission() {
        return permission;
    }
    
}
