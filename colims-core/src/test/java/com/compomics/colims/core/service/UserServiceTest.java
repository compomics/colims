
package com.compomics.colims.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testFindGroupsByUserId() {         
        User user = userService.findById(1L);
        userService.fetchAuthenticationRelations(user);
                  
        Assert.assertEquals(2, user.getUserHasGroups().size());
        Group group = user.getUserHasGroups().get(0).getGroup();
        Assert.assertNotNull(group);
        Assert.assertEquals(2, group.getGroupHasRoles().size());
        Role role = group.getGroupHasRoles().get(0).getRole();
        Assert.assertNotNull(role);
        Assert.assertEquals(2, role.getRoleHasPermissions().size());
        Permission permission = role.getRoleHasPermissions().get(0).getPermission();
        Assert.assertNotNull(permission);               
    }
    
}
