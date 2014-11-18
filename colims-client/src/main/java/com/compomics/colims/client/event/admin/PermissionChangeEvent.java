package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Permission;

/**
 * @author Niels Hulstaert
 */
public class PermissionChangeEvent extends EntityWithChildrenChangeEvent {

    /**
     * The Permission instance.
     */
    private final Permission permission;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param childrenAffected are the child collections affected
     * @param permission the Permission instance
     */
    public PermissionChangeEvent(final Type type, final boolean childrenAffected, final Permission permission) {
        super(type, childrenAffected);
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
    
}
