package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.model.User;

/**
 * @author Niels Hulstaert
 */
public class UserChangeEvent extends EntityChangeEvent {
    
    private User user;

    public UserChangeEvent(final Type type, final boolean childrenAffected, final User user) {
        super(type, childrenAffected);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}
