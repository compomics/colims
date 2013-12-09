package com.compomics.colims.repository.audit;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.compomics.colims.repository.AuthenticationBean;

/**
 * Hibernate interceptor for auditing purposes. Fills in the field in the
 * AbstractDatabaseEntity: user name, creation date and modification date.
 *
 * @author Niels Hulstaert
 */
@Deprecated
public class AuditInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuditInterceptor.class);
    private AuthenticationBean authenticationBean;

    public AuthenticationBean getAuthenticationBean() {
        return authenticationBean;
    }

    public void setAuthenticationBean(AuthenticationBean authenticationBean) {
        this.authenticationBean = authenticationBean;
    }            

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {       
        return audit(currentState, propertyNames);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return audit(state, propertyNames);
    }

    /**
     * Fill in the audit values (user name, creation date, modification date)
     * for the entity that will be saved or updated.
     *
     * @param currentState the current state of the entity
     * @param propertyNames
     * @return true if the state is modified in anyway
     */
    private boolean audit(Object[] currentState, String[] propertyNames) {
        boolean changed = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if ("creationDate".equals(propertyNames[i])) {
                Object currentDate = currentState[i];
                if (currentDate == null) {
                    currentState[i] = new Date();
                    changed = true;
                }
            }
            if ("modificationDate".equals(propertyNames[i])) {
                currentState[i] = new Date();
                changed = true;
            }
            if ("userName".equals(propertyNames[i])) {
//                currentState[i] = sessionBean.getCurrentUser().getName();
                currentState[i] = "N/A";
                changed = true;
            }
        }
        return changed;
    }
}
