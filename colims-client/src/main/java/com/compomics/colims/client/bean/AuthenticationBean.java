
package com.compomics.colims.client.bean;

import com.compomics.colims.model.User;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("authenticiationBean")
public class AuthenticationBean {
    
    private User currentUser;
    
    public AuthenticationBean(){}

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }                
    
}
