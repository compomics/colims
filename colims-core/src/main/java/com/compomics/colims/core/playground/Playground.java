package com.compomics.colims.core.playground;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");

        UserService userService = (UserService) applicationContext.getBean("userService");
        User user = userService.findById(1L);

        userService.fetchAuthenticationRelations(user);
    }
}
