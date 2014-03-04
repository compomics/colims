package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityWithChildrenChangeEvent {

    private final Role role;

    public RoleChangeEvent(final Type type, final boolean childrenAffected, final Role role) {
        super(type, childrenAffected);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
