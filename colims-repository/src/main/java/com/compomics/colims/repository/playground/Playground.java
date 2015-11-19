package com.compomics.colims.repository.playground;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) throws IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = applicationContext.getBean("&entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);
    }

}
