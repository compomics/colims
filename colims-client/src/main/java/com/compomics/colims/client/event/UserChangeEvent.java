package com.compomics.colims.client.event;

import com.compomics.colims.model.User;

/**
 * @author Niels Hulstaert
 */
public class UserChangeEvent extends EntityChangeEvent {
    
    private User user;

    public UserChangeEvent(Type type, User user) {
        this.user = user;
        this.type = type;
    }
    
    public User getUser() {
        return user;
    }
}
