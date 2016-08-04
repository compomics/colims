package com.compomics.colims.core.permission;

import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.enums.DefaultPermission;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Niels Hulstaert
 */
@Aspect
public class PermissionInterceptor {

    /**
     * The Colims user bean containing the logged in user and his/her
     * credentials.
     */
    @Autowired
    private UserBean userBean;

    /**
     * This method is triggered by a save method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.persist(..))")
    public void beforeCreateOperation(final JoinPoint joinPoint) {
        if (!userBean.getDefaultPermissions().get(DefaultPermission.CREATE)) {
            throw new PermissionException("User " + userBean.getCurrentUser() + " has no save permission.");
        }
    }

    /**
     * This method is triggered by an update method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.merge(..))")
    public void beforeUpdateOperation(final JoinPoint joinPoint) {
        if (!userBean.getDefaultPermissions().get(DefaultPermission.UPDATE)) {
            throw new PermissionException("User " + userBean.getCurrentUser() + " has no update permission.");
        }
    }

    /**
     * This method is triggered by a delete method call from an interceptable
     * service.
     *
     * @param joinPoint the JoinPoint
     */
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.remove(..))")
    public void beforeDeleteOperation(final JoinPoint joinPoint) {
        if (!userBean.getDefaultPermissions().get(DefaultPermission.DELETE)) {
            throw new PermissionException("User " + userBean.getCurrentUser() + " has no delete permission.");
        }
    }

}
