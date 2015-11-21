package com.compomics.colims.model.audit;

import com.compomics.colims.model.AuditableDatabaseEntity;
import com.compomics.colims.model.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Created by Niels Hulstaert on 19/11/15.
 */
public class AuditableDatabaseEntityListener {

    @PrePersist
    public void prePersist(AuditableDatabaseEntity auditableDatabaseEntity) {
        auditableDatabaseEntity.setCreationDate(new Date());
        auditableDatabaseEntity.setModificationDate(new Date());
        auditableDatabaseEntity.setUserName("blabla");
    }

    @PreUpdate
    public void preUpdate(AuditableDatabaseEntity auditableDatabaseEntity) {
        auditableDatabaseEntity.setModificationDate(new Date());
        auditableDatabaseEntity.setUserName("blabla");
    }
}
