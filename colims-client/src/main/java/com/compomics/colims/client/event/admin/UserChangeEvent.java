package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityWithChildrenChangeEvent;
import com.compomics.colims.model.User;

/**
 * @author Niels Hulstaert
 */
public class UserChangeEvent extends EntityWithChildrenChangeEvent {
    
    private final User user;

    /**
     *
     * @param type
     * @param childrenAffected
     * @param user
     */
    public UserChangeEvent(final Type type, final boolean childrenAffected, final User user) {
        super(type, childrenAffected);
        this.user = user;
    }
    
    /**
     *
     * @return
     */
    public User getUser() {
        return user;
    }
}
