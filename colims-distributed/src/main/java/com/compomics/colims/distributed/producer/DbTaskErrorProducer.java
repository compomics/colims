package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.DbTaskError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;
import java.io.IOException;

/**
 * This class sends DbTaskError instances to the error queue.
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskErrorProducer")
public class DbTaskErrorProducer {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbTaskErrorProducer.class);

    /**
     * Mapper for converting a DbTaskError object to the matching JSON construct.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    private final JmsTemplate dbTaskErrorProducerTemplate;

    @Autowired
    public DbTaskErrorProducer(JmsTemplate dbTaskErrorProducerTemplate) {
        this.dbTaskErrorProducerTemplate = dbTaskErrorProducerTemplate;
    }

    /**
     * Send the serialized DbTaskError to the error queue.
     *
     * @param dbTaskError the DbTaskError
     * @throws IOException if an I/O related problem occurs
     */
    public void sendDbTaskError(final DbTaskError dbTaskError) throws IOException {
        //map to JSON construct
        String jsonDbTaskError = objectMapper.writeValueAsString(dbTaskError);

        dbTaskErrorProducerTemplate.send(session -> {
            TextMessage dbTaskErrorTextMessage = session.createTextMessage(jsonDbTaskError);

            LOGGER.info("Sending database task error");

            return dbTaskErrorTextMessage;
        });
    }

}
