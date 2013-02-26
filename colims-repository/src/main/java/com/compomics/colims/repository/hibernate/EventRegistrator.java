package com.compomics.colims.repository.hibernate;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("eventRegistrator")
public class EventRegistrator {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CustomPersistEventListener persistEventListener;
    @Autowired
    private CustomSaveEventListener saveEventListener;
    @Autowired
    private CustomUpdateEventListener updateEventListener;

    @PostConstruct
    public void registerListeners() {
        final EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory)
                .getServiceRegistry().getService(EventListenerRegistry.class);

        registry.prependListeners(EventType.PERSIST, persistEventListener);
        //@Todo check if we need to listen to this event
        registry.prependListeners(EventType.SAVE, saveEventListener);
        registry.prependListeners(EventType.UPDATE, updateEventListener);

        //Adding a listener for a SaveOrUpdateEvent results in unwanted behaviour; 
        //when fetching children of one-to-many relations, the SaveOrUpdateEvent is triggered.
        //So for now, no listener will be registered for this event.
        //registry.prependListeners(EventType.SAVE_UPDATE, testListener);

    }
}
