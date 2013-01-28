package com.compomics.colims.core.aop;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Niels Hulstaert
 */
@Aspect
public class HibernateCacheMonitor {

    private static final Logger LOGGER = Logger.getLogger(HibernateCacheMonitor.class);
    private static final NumberFormat NF = new DecimalFormat("0.0###");
    @Autowired
    private SessionFactory sessionFactory;

    //pointcut for the execution of any method in a repository interface.
    //The implementations are assumed to be in sub packages.
    @Pointcut("execution(* com.compomics.colims.repository.*.*(..))")
    public void dataAccessOperation() {
    }

    @Around("dataAccessOperation()")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        if (!LOGGER.isDebugEnabled()) {
            return pjp.proceed();
        }

        //get hibernate SessionFactory through EntityManagerFactory
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        long hit0 = statistics.getQueryCacheHitCount();
        long miss0 = statistics.getSecondLevelCacheMissCount();

        Object result = pjp.proceed();

        long hit1 = statistics.getQueryCacheHitCount();
        long miss1 = statistics.getQueryCacheMissCount();

        double ratio = (double) hit1 / (hit1 + miss1);

        if (hit1 > hit0) {
            LOGGER.debug(String.format("CACHE HIT; Ratio=%s; Signature=%s#%s()", NF.format(ratio), pjp.getTarget().getClass().getName(), pjp.getSignature().toShortString()));
        } else if (miss1 > miss0) {
            LOGGER.debug(String.format("CACHE MISS; Ratio=%s; Signature=%s#%s()", NF.format(ratio), pjp.getTarget().getClass().getName(), pjp.getSignature().toShortString()));
        } else {
            LOGGER.debug("query cache not used");
        }

        return result;
    }
}
