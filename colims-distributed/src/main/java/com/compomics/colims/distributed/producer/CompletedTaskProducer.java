package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * This class sends CompletedDbTask instances to the completed queue.
 *
 * @author Niels Hulstaert
 */
@Component("completedTaskProducer")
public class CompletedTaskProducer {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CompletedTaskProducer.class);

    /**
     * Mapper for converting a CompletedDbTask object to the matching JSON construct.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JmsTemplate.
     */
    @Autowired
    private JmsTemplate completedDbTaskProducerTemplate;

    /**
     * Send the serialized CompletedDbTask to the completed queue.
     *
     * @param completedDbTask the CompletedDbTask
     * @throws IOException if an I/O related problem occurs
     */
    public void sendCompletedDbTask(final CompletedDbTask completedDbTask) throws IOException {
        //map to JSON construct
        String jsonCompletedDbTask = objectMapper.writeValueAsString(completedDbTask);

        completedDbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                TextMessage completedDbTaskTextMessage = session.createTextMessage(jsonCompletedDbTask);

                LOGGER.info("Sending completed db task");

                return completedDbTaskTextMessage;
            }
        });
    }

}
