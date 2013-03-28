package com.compomics.colims.client.event;

import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityChangeEvent {

    private Role role;

    public RoleChangeEvent(Type type, Role role) {
        this.role = role;
        this.type = type;
    }

    public Role getRole() {
        return role;
    }
}
