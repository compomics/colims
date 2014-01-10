package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityChangeEvent {

    private Role role;

    public RoleChangeEvent(final Type type, final boolean childrenAffected, final Role role) {
        super(type, childrenAffected);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
