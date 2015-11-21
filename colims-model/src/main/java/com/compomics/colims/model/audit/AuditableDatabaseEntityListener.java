package com.compomics.colims.model.audit;

import com.compomics.colims.model.AuditableDatabaseEntity;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.util.ApplicationContextHolder;
import org.springframework.context.ApplicationContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Entity listener class for providing audit fields content for AuditableDatabaseEntity subclasses before persisting or
 * updating. This listener is managed by JPA, so we needed some way to wire the user bean into it.
 * <p/>
 * Created by Niels Hulstaert on 19/11/15.
 */
public class AuditableDatabaseEntityListener {

    /**
     * Bean with the current user and permissions.
     */
    private UserBean userBean;

    @PrePersist
    public void prePersist(AuditableDatabaseEntity auditableDatabaseEntity) {
        auditableDatabaseEntity.setCreationDate(new Date());
        auditableDatabaseEntity.setModificationDate(new Date());
        if (userBean == null) {
            loadUserBean();
        }
        auditableDatabaseEntity.setUserName(userBean.getCurrentUser().getName());
    }

    @PreUpdate
    public void preUpdate(AuditableDatabaseEntity auditableDatabaseEntity) {
        auditableDatabaseEntity.setModificationDate(new Date());
        if (userBean == null) {
            loadUserBean();
        }
        auditableDatabaseEntity.setUserName(userBean.getCurrentUser().getName());
    }

    /**
     * Load the user bean from the application context.
     */
    private void loadUserBean() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        userBean = applicationContext.getBean("userBean", UserBean.class);
    }
}
