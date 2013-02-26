package com.compomics.colims.repository;

import org.springframework.stereotype.Component;

import com.compomics.colims.model.User;

/**
 *
 * @author niels
 */
@Component("sessionBean")
public class SessionBean {

    /**
     * The logged in user.
     */
    private User currentUser;

    public SessionBean() {
        //set a default user
        currentUser = new User("N/A");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
