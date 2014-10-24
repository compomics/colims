package com.compomics.colims.core.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This singleton class provides methods the set and get the Spring
 * ApplicationContext.
 *
 * @author Niels Hulstaert
 */
public final class ApplicationContextProvider {

    /**
     * The Spring application context.
     */
    private ApplicationContext applicationContext;
    /**
     * private and final ApplicationContext holder.
     */
    private static final ApplicationContextProvider APPLICATION_CONTEXT_PROVIDER = new ApplicationContextProvider();

    /**
     * Private no-arg constructor.
     */
    private ApplicationContextProvider() {
    }

    /**
     * Get the instance of this singleton class.
     *
     * @return the singleton instance
     */
    public static synchronized ApplicationContextProvider getInstance() {
        return APPLICATION_CONTEXT_PROVIDER;
    }

    /**
     * Get the Spring ApplicationContext.
     *
     * @return the ApplicationContext
     */
    public ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("The application context is not set yet.");
        }
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Load the default ApplicationContext.
     *
     */
    public void setDefaultApplicationContext() {
        this.applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");
    }

    /**
     * Get a bean from the ApplicationContext by name.
     *
     * @param <T> the bean class
     * @param beanName the bean name
     * @return the found bean
     */
    public <T> T getBean(final String beanName) {
        if (applicationContext == null) {
            throw new IllegalStateException("The application context is not set yet.");
        }
        return (T) applicationContext.getBean(beanName);
    }
}
