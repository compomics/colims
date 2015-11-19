package com.compomics.colims.core.playground;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.service.UserQueryService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import org.springframework.context.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class Playground2 {

    public static void main(String[] args) {
        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

        UserService userService = (UserService) applicationContext.getBean("userService");
        User user = userService.findById(1L);

        UserQueryService userQueryService = (UserQueryService) applicationContext.getBean("userQueryService");
        List<LinkedHashMap<String, Object>> linkedHashMaps = userQueryService.executeUserQuery(user, "select * from project");

        System.out.println("test");
    }

}
