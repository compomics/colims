package com.compomics.colims.distributed.producer;

import com.compomics.colims.distributed.model.DbTaskError;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskErrorProducer")
public class DbTaskErrorProducer {

    private static final Logger LOGGER = Logger.getLogger(DbTaskErrorProducer.class);

    @Autowired
    private JmsTemplate dbTaskErrorProducerTemplate;

    /**
     * Send the serialized DbTaskError to the error queue.
     *
     * @param dbTaskError the DbTaskError
     * @throws JmsException
     */
    public void sendDbTaskError(final DbTaskError dbTaskError) throws JmsException {

        dbTaskErrorProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set DbTaskError instance as message body
                ObjectMessage dbTaskErrorMessage = session.createObjectMessage();

                LOGGER.info("Sending database task error");

                return dbTaskErrorMessage;
            }
        });
    }

}
