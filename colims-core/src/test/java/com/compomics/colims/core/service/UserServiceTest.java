package com.compomics.colims.core.service;

import com.compomics.colims.core.exception.PermissionException;
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
import com.compomics.colims.repository.AuthenticationBean;
import java.util.List;

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
    @Autowired
    private AuthenticationBean authenticationBean;
    
    @Test
    public void testFindGroupsByUserId() {
        
        List<User> findAll = userService.findAll();
        
        System.out.println("test");
    }
}
