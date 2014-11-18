package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.Role;

/**
 * @author Niels Hulstaert
 */
public class RoleChangeEvent extends EntityWithChildrenChangeEvent {

    /**
     * The Role instance.
     */
    private final Role role;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param childrenAffected are the child collections affected
     * @param role the Role instance
     */
    public RoleChangeEvent(final Type type, final boolean childrenAffected, final Role role) {
        super(type, childrenAffected);
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
