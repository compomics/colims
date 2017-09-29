package com.compomics.colims.client.distributed.producer;

import com.compomics.colims.core.distributed.model.DbTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;
import java.io.IOException;

/**
 * This class sends DbTask instances in JSON format to the dbtask queue.
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskProducer")
public class DbTaskProducer {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbTaskProducer.class);

    /**
     * Mapper for converting a DbTask object to the matching JSON construct.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    private final JmsTemplate dbTaskProducerTemplate;

    @Autowired
    public DbTaskProducer(JmsTemplate dbTaskProducerTemplate) {
        this.dbTaskProducerTemplate = dbTaskProducerTemplate;
    }

    /**
     * Send the serialized DbTask to the db task queue.
     *
     * @param dbTask the DbTask instance
     * @throws IOException if an I/O related problem occurs
     */
    public void sendDbTask(final DbTask dbTask) throws IOException {
        //map to JSON construct
        String jsonDbTask = objectMapper.writeValueAsString(dbTask);

        dbTaskProducerTemplate.send(session -> {
            TextMessage dbTaskTextMessage = session.createTextMessage(jsonDbTask);

            LOGGER.info("Sending JSON db task of class " + dbTask.getClass().getSimpleName());

            return dbTaskTextMessage;
        });
    }

}
