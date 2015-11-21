package com.compomics.colims.repository.playground;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) throws IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = applicationContext.getBean("&entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);
        EntityManagerFactory emf = entityManagerFactoryBean.getNativeEntityManagerFactory();
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
    }

}
