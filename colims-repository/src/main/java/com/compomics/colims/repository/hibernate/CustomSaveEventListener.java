package com.compomics.colims.repository.hibernate;

import com.compomics.colims.model.AuditableDatabaseEntity;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.compomics.colims.model.UserBean;

/**
 * This custom event listener listens to save events. For all entities that
 * subclass AuditableDatabaseEntity, the user name, creation and modification
 * date columns in the database are updated.
 *
 * @author Niels Hulstaert
 */
@Component("saveEventListener")
public class CustomSaveEventListener extends DefaultSaveEventListener {

    private static final long serialVersionUID = 1L;
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomSaveEventListener.class);
    /**
     * The authentication bean with the logged in user and his/her permissions.
     */
    @Autowired
    private UserBean userBean;

    @Override
    public void onSaveOrUpdate(final SaveOrUpdateEvent event) {
        LOGGER.debug("Entering onSaveOrUpdate");

        onListen(event.getObject());

        super.onSaveOrUpdate(event);
    }

    /**
     * This method updates the user name, creation and modification date fields
     * if the entity class is a subclass of AuditableDatabaseEntity.
     *
     * @param object the entity that triggered the event.
     */
    private void onListen(final Object object) {
        if (object instanceof AuditableDatabaseEntity) {
            AuditableDatabaseEntity entity = (AuditableDatabaseEntity) object;

            //set the user name
            entity.setUserName(userBean.getCurrentUser().getName());

            // set the creation date
            if (entity.getCreationDate() == null) {
                entity.setCreationDate(new Date());
            }

            //set the modification date
            entity.setModificationDate(new Date());
        }
    }
}
