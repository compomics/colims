
package com.compomics.colims.client.bean;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("authenticiationBean")
public class AuthenticationBean {            
            
    private User currentUser;
    @Autowired
    private UserService userService;
    
    public AuthenticationBean(){}

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        //fetch authentication relations
        userService.fetchAuthenticationRelations(this.currentUser);
    } 
    
    /**
     * Check if the current user has the permission to delete.
     * 
     * @return true if the user has the permission to delete
     */
    public boolean hasDeletePermission(){
        boolean hasDeletePermission = false;
        
        for(Group group : currentUser.getGroups()){            
            for(Role role : group.getRoles()){
                for(Permission permission : role.getPermissions()){
//                    if(){
//                        
//                    }
                }
            }
        }
        
        return hasDeletePermission;
    }   
    
}
