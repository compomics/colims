package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.DbTaskError;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

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
    private static final Logger LOGGER = Logger.getLogger(DbTaskErrorProducer.class);

    /**
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate dbTaskErrorProducerTemplate;

    /**
     * Send the serialized DbTaskError to the error queue.
     *
     * @param dbTaskError the DbTaskError
     */
    public void sendDbTaskError(final DbTaskError dbTaskError) {

        dbTaskErrorProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                //set DbTaskError instance as message body
                ObjectMessage dbTaskErrorMessage = session.createObjectMessage(dbTaskError);

                LOGGER.info("Sending database task error");

                return dbTaskErrorMessage;
            }
        });
    }

}
