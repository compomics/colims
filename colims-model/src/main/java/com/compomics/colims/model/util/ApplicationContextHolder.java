package com.compomics.colims.model.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Application context holder class for getting a reference to the application context.
 * <p/>
 * Created by Niels Hulstaert on 20/11/15.
 */
@Component("applicationContextHolder")
public class ApplicationContextHolder implements ApplicationContextAware {

    /**
     * The Spring application context.
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

}
