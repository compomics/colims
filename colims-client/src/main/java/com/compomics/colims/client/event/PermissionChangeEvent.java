package com.compomics.colims.client.event;

import com.compomics.colims.model.Permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionChangeEvent extends EntityChangeEvent {

    private Permission permission;

    public PermissionChangeEvent(Type type, boolean childrenAffected, Permission permission) {
        super(type, childrenAffected);
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
    
}
