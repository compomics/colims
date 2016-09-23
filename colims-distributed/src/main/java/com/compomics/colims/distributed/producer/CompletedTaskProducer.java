package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;


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
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JmsTemplate.
     */
    private final JmsTemplate completedDbTaskProducerTemplate;

    @Autowired
    public CompletedTaskProducer(JmsTemplate completedDbTaskProducerTemplate) {
        this.completedDbTaskProducerTemplate = completedDbTaskProducerTemplate;
    }

    /**
     * Send the serialized CompletedDbTask to the completed queue.
     *
     * @param completedDbTask the CompletedDbTask
     * @throws IOException if an I/O related problem occurs
     */
    public void sendCompletedDbTask(final CompletedDbTask completedDbTask) throws IOException {
        //map to JSON construct
        String jsonCompletedDbTask = objectMapper.writeValueAsString(completedDbTask);

        completedDbTaskProducerTemplate.send(session -> {
            TextMessage completedDbTaskTextMessage = session.createTextMessage(jsonCompletedDbTask);

            LOGGER.info("Sending completed db task");

            return completedDbTaskTextMessage;
        });
    }

}
