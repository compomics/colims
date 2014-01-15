package com.compomics.colims.repository.hibernate;

import com.compomics.colims.model.AuditableDatabaseEntity;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.event.internal.DefaultUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.compomics.colims.repository.AuthenticationBean;

/**
 *
 * @author Niels Hulstaert
 */
@Component("updateEventListener")
public class CustomUpdateEventListener extends DefaultUpdateEventListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomUpdateEventListener.class);
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
            
            if(entity.getCreationdate() == null){
                entity.setCreationdate(new Date());
            }
            //set the modification date
            entity.setModificationdate(new Date());                        
        }
    }
}
