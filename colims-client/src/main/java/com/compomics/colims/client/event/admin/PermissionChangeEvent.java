package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionChangeEvent extends EntityWithChildrenChangeEvent {

    private final Permission permission;

    /**
     *
     * @param type
     * @param childrenAffected
     * @param permission
     */
    public PermissionChangeEvent(final Type type, final boolean childrenAffected, final Permission permission) {
        super(type, childrenAffected);
        this.permission = permission;
    }

    /**
     *
     * @return
     */
    public Permission getPermission() {
        return permission;
    }
    
}
