package com.compomics.colims.repository.hibernate;

import com.compomics.colims.model.AbstractDatabaseEntity;
import com.compomics.colims.repository.SessionBean;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("persistEventListener")
public class CustomPersistEventListener implements PersistEventListener {

    private static final Logger LOGGER = Logger.getLogger(CustomPersistEventListener.class);
    @Autowired
    private SessionBean sessionBean;

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        LOGGER.debug("Entering onPersist(PersistEvent event)");

        onListen(event.getObject());
    }

    @Override
    public void onPersist(PersistEvent event, Map map) throws HibernateException {
        LOGGER.debug("Entering onPersist(PersistEvent event, Map map)");
        
        onPersist(event);
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

            // set the modification date
            entity.setModificationdate(new Date());
        }
    }

}
