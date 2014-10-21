package com.compomics.colims.core.authorization;

import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Niels Hulstaert
 */
@Aspect
public class AuthorizationInterceptor {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthorizationInterceptor.class);
    /**
     * The Colims authentication bean containing the logged in user and his/her
     * credentials.
     */
    @Autowired
    private AuthenticationBean authenticationBean;

    /**
     * This method is triggered by a save method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.save(..))")
    public void beforeCreateOperation(final JoinPoint joinPoint) {
        if (!authenticationBean.getDefaultPermissions().get(DefaultPermission.CREATE)) {
            throw new PermissionException("User " + authenticationBean.getCurrentUser() + " has no save permission.");
        }
    }

    /**
     * This method is triggered by an update method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.update(..))")
    public void beforeUpdateOperation(final JoinPoint joinPoint) {
        if (!authenticationBean.getDefaultPermissions().get(DefaultPermission.UPDATE)) {
            throw new PermissionException("User " + authenticationBean.getCurrentUser() + " has no update permission.");
        }
    }

    /**
     * This method is triggered by a delete method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.delete(..))")
    public void beforeDeleteOperation(final JoinPoint joinPoint) {
        if (!authenticationBean.getDefaultPermissions().get(DefaultPermission.DELETE)) {
            throw new PermissionException("User " + authenticationBean.getCurrentUser() + " has no delete permission.");
        }
    }

}
