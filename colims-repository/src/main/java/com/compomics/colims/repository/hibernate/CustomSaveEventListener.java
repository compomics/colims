package com.compomics.colims.repository.hibernate;

import com.compomics.colims.model.AuditableDatabaseEntity;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.compomics.colims.model.DatabaseEntity;
import com.compomics.colims.repository.AuthenticationBean;

/**
 *
 * @author Niels Hulstaert
 */
@Component("saveEventListener")
public class CustomSaveEventListener extends DefaultSaveEventListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomSaveEventListener.class);
    @Autowired
    private AuthenticationBean authenticationBean;

    @Override
    public void onSaveOrUpdate(final SaveOrUpdateEvent event) {
        LOGGER.debug("Entering onSaveOrUpdate");

        onListen(event.getObject());

        super.onSaveOrUpdate(event);
    }

    private void onListen(final Object object) {
        if (object instanceof AuditableDatabaseEntity) {
            AuditableDatabaseEntity entity = (AuditableDatabaseEntity) object;

            //set the user name            
            entity.setUserName(authenticationBean.getCurrentUser().getName());

            // set the creation date
            if (entity.getCreationDate() == null) {
                entity.setCreationDate(new Date());
            }

            //set the modification date
            entity.setModificationDate(new Date());
        }
    }
}
