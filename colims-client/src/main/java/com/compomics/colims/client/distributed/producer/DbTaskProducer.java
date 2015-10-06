package com.compomics.colims.client.distributed.producer;

import com.compomics.colims.core.distributed.model.DbTask;
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
 * This class sends DbTask instances in JSON format to the dbtask queue.
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskProducer")
public class DbTaskProducer {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DbTaskProducer.class);

    /**
     * Mapper for converting a DbTask object to the matching JSON construct.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate dbTaskProducerTemplate;

    /**
     * Send the serialized DbTask to the db task queue.
     *
     * @param dbTask the DbTask instance
     * @throws IOException if an I/O related problem occurs
     */
    public void sendDbTask(final DbTask dbTask) throws IOException {
        //map to JSON construct
        String jsonDbTask = objectMapper.writeValueAsString(dbTask);

        dbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                TextMessage dbTaskTextMessage = session.createTextMessage(jsonDbTask);

                LOGGER.info("Sending JSON db task of class " + dbTask.getClass().getSimpleName());

                return dbTaskTextMessage;
            }
        });
    }

}
