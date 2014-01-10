package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.model.Permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionChangeEvent extends EntityChangeEvent {

    private Permission permission;

    public PermissionChangeEvent(final Type type, final boolean childrenAffected, final Permission permission) {
        super(type, childrenAffected);
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
    
}
