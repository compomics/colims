package com.compomics.colims.core.aop;

import com.compomics.colims.core.exception.PermissionException;
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
public class AuthenticationInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationInterceptor.class);
    @Autowired
    private AuthenticationBean authenticationBean;

    @Before("execution(* com.compomics.colims.core.service.*.save(..))")
    public void beforeCreateOperation(JoinPoint joinPoint) {
        if(!authenticationBean.getDefaultPermissions().get(DefaultPermission.CREATE)){
            throw new PermissionException("User" + authenticationBean.getCurrentUser() + " has no save permission.");
        }
        
    }

    @Before("execution(* com.compomics.colims.core.service.*.update(..))")
    public void beforeUpdateOperation(JoinPoint joinPoint) {
        if(!authenticationBean.getDefaultPermissions().get(DefaultPermission.UPDATE)){
            throw new PermissionException("User" + authenticationBean.getCurrentUser() + " has no update permission.");
        }
    }

    @Before("execution(* com.compomics.colims.core.service.*.delete(..))")
    public void beforeDeleteOperation(JoinPoint joinPoint) throws PermissionException {
        if(!authenticationBean.getDefaultPermissions().get(DefaultPermission.DELETE)){
            throw new PermissionException("User" + authenticationBean.getCurrentUser() + " has no delete permission.");
        }
    }

    
}