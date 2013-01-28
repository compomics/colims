package com.compomics.colims.client.event;

import com.compomics.colims.model.User;

/**
 * @author Niels Hulstaert
 */
public class UserChangeEvent {

    public enum Type {

        CREATED, DELETED, UPDATED;
    }
    private Type type;
    private User user;

    public UserChangeEvent(Type type, User user) {
        this.user = user;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public User getUser() {
        return user;
    }
}
