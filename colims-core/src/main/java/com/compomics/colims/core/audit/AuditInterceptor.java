package com.compomics.colims.core.audit;

import com.compomics.colims.core.authorization.PermissionException;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.model.UserBean;
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
public class AuditInterceptor {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AuditInterceptor.class);
    /**
     * The Colims authentication bean containing the logged in user and his/her
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
    @Before("execution(* com.compomics.colims.core.service.impl.interceptable.*.save(..))")
    public void beforeCreateOperation(final JoinPoint joinPoint) {
        if (!userBean.getDefaultPermissions().get(DefaultPermission.CREATE)) {
            throw new PermissionException("User " + userBean.getCurrentUser() + " has no save permission.");
        }
    }

}
