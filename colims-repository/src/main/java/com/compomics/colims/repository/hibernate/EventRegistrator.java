package com.compomics.colims.repository.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * This class registers the different custom event listeners to the hibernate EventListenerRegistry.
 *
 * @author Niels Hulstaert
 */
@Component("eventRegistrator")
public class EventRegistrator {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    /**
     * The persist event listener.
     */
    @Autowired
    private CustomPersistEventListener persistEventListener;
    /**
     * The save event listener.
     */
    @Autowired
    private CustomSaveEventListener saveEventListener;
    /**
     * The update event listener.
     */
    @Autowired
    private CustomUpdateEventListener updateEventListener;

    /**
     * Register the custom event listeners.
     */
    @PostConstruct
    public void registerListeners() {
        final EventListenerRegistry registry = ((SessionFactoryImpl) entityManagerFactory.unwrap(SessionFactory.class))
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
