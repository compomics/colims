package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.User;

/**
 * @author Niels Hulstaert
 */
public class UserChangeEvent extends EntityWithChildrenChangeEvent {

    /**
     * The User instance.
     */
    private final User user;

    /**
     * Constructor.
     *
     * @param type the change type
     * @param childrenAffected are the child collections affected
     * @param user the User instance
     */
    public UserChangeEvent(final Type type, final boolean childrenAffected, final User user) {
        super(type, childrenAffected);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
