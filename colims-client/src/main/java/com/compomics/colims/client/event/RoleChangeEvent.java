package com.compomics.colims.client.event;

import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityChangeEvent {

    private Role role;

    public RoleChangeEvent(Type type, boolean childrenAffected, Role role) {
        super(type, childrenAffected);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
