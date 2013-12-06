package com.compomics.colims.core.aop;

import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import java.util.Map;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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

    @Pointcut("execution(* com.compomics.colims.core.service.*.save(..))")
    public void beforeCreateOperation() {
    }

    //pointcut for the execution of a delete method.
    //The implementations are assumed to be in sub packages.
    @Pointcut("execution(* com.compomics.colims.core.service.*.update(..))")
    public void beforeUpdateOperation() {
    }

    @Pointcut("execution(* com.compomics.colims.core.service.*.delete(..))")
    public void beforeDeleteOperation() {
    }

    @Before("beforeCrudOperation()")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        Map<DefaultPermission, Boolean> defaultPermissions = authenticationBean.getDefaultPermissions();

        return pjp.proceed();
    }
}
