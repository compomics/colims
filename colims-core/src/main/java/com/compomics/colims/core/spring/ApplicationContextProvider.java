/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public final class ApplicationContextProvider {

    /**
     * The Spring application context
     */
    private ApplicationContext applicationContext;
    private static final ApplicationContextProvider PROVIDER = new ApplicationContextProvider();

    /**
     * The singleton private constructor.
     */
    private ApplicationContextProvider() {
        this.applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");
    }

    /**
     * Get the singleton instance.
     *
     * @return the singleton instance
     * @throws ExceptionInInitializerError
     */
    public static synchronized ApplicationContextProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Get the Spring application context.
     *
     * @return the application context
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Setter for the application context.
     *
     * @param applicationContext the application context
     */
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
