package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityWithChildrenChangeEvent {

    private final Role role;

    /**
     *
     * @param type
     * @param childrenAffected
     * @param role
     */
    public RoleChangeEvent(final Type type, final boolean childrenAffected, final Role role) {
        super(type, childrenAffected);
        this.role = role;
    }

    /**
     *
     * @return
     */
    public Role getRole() {
        return role;
    }
}
