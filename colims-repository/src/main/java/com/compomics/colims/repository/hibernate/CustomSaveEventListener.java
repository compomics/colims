package com.compomics.colims.repository.hibernate;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.compomics.colims.model.AbstractDatabaseEntity;
import com.compomics.colims.repository.SessionBean;

/**
 *
 * @author Niels Hulstaert
 */
@Component("saveEventListener")
public class CustomSaveEventListener extends DefaultSaveEventListener {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomSaveEventListener.class);
    @Autowired
    private SessionBean sessionBean;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        LOGGER.debug("Entering onSaveOrUpdate");

        onListen(event.getObject());

        super.onSaveOrUpdate(event);
    }

    private void onListen(Object object) {
        if (object instanceof AbstractDatabaseEntity) {
            AbstractDatabaseEntity entity = (AbstractDatabaseEntity) object;

            //set the user name            
            entity.setUsername(sessionBean.getCurrentUser().getName());

            // set the creation date
            if (entity.getCreationdate() == null) {
                entity.setCreationdate(new Date());
            }
            
            //set the modification date
            entity.setModificationdate(new Date());
        }
    }
}
