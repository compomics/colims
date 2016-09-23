package com.compomics.colims.client.distributed.consumer;

import com.compomics.colims.client.event.CompletedDbTaskEvent;
import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.swing.*;
import java.io.IOException;

/**
 * This class listens to the completed task topic and handles incoming messages.
 *
 * @author Niels Hulstaert
 */
@Component("completedDbTaskConsumer")
public class CompletedDbTaskConsumer implements MessageListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CompletedDbTaskConsumer.class);

    /**
     * Mapper for converting JSON constructs from the queue to corresponding java objects.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The guava EventBus instance.
     */
    private final EventBus eventBus;

    @Autowired
    public CompletedDbTaskConsumer(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message the incoming JMS message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            TextMessage jsonConstruct;
            if (message instanceof TextMessage) {
                jsonConstruct = (TextMessage) message;
            } else {
                throw new IllegalStateException("The retrieved message is of an incorrect type.");
            }
            CompletedDbTask completedDbTask = objectMapper.readValue(jsonConstruct.getText(), CompletedDbTask.class);

            LOGGER.info("received completed db task message");

            //post notification on the event bus
            //we need to invoke the EDT thread
            SwingUtilities.invokeLater(() -> eventBus.post(new CompletedDbTaskEvent(completedDbTask)));
        } catch (JMSException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
