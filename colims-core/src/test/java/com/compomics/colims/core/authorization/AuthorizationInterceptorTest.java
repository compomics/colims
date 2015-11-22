
package com.compomics.colims.core.authorization;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Rollback
@Transactional
public class AuthorizationInterceptorTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserBean userBean;

    @Test(expected = PermissionException.class)
    public void testBeforeSaveAdvice() {
        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        userBean.setCurrentUser(user);

        User newUser = new User("testUser");
        newUser.setEmail("test@test");
        newUser.setFirstName("test");
        newUser.setLastName("test");
        newUser.setPassword("blablablabla");

        userService.persist(newUser);
    }

    @Test(expected = PermissionException.class)
    public void testBeforeUpdateAdvice() {
        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        userBean.setCurrentUser(user);

        User userToUpdate = userService.findByName("lab1");
        userToUpdate.setEmail("test@test");

        userService.merge(userToUpdate);
    }

    @Test(expected = PermissionException.class)
    public void testBeforeDeleteAdvice() {
        User user = userService.findByName("collab1");
        userService.fetchAuthenticationRelations(user);
        userBean.setCurrentUser(user);

        User userToDelete = userService.findByName("lab1");

        userService.remove(userToDelete);
    }

}
