package com.compomics.colims.repository;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.User;
import org.junit.Assert;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private UserRepository userRepository; 

    @Test
    public void testFindAllUsers() {
        List<User> users = userRepository.findAll();

        Assert.assertEquals(3, users.size());
    }
    
    @Test
    public void testFindUserByName() {
        User user = userRepository.findByName("user1_name");

        Assert.assertNotNull(user);
    }
    
    @Test
    public void testEncryptedPassword() {
        User user = userRepository.findByName("user1_name");

        Assert.assertEquals("nielsniels", user.getPassword());
    }
    
    @Test
    public void testInstitutionRelation() {
        User user = userRepository.findByName("user1_name");

        Assert.assertNotNull(user.getInstitution());        
    }        
     
    @Test
    public void testAuthorizationRelations() {
        User user = userRepository.findByName("user1_name");
        
        //groups
        Assert.assertNotNull(user.getGroups());
        List<Group> groups = user.getGroups();
        Assert.assertEquals(1, groups.size());
        
        //roles
        Assert.assertNotNull(groups.get(0).getRoles());
        List<Role> roles = groups.get(0).getRoles();
        Assert.assertEquals(2, roles.size());
        
        //permissions
        Assert.assertNotNull(roles.get(0).getPermissions());
        List<Permission> permissions = roles.get(0).getPermissions();
        Assert.assertEquals(1, permissions.size());
    } 
}
