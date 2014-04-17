package com.compomics.colims.distributed.producer;

import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.model.CompletedDbTask;
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
@Component("completedTaskProducer")
public class CompletedTaskProducer {

    private static final Logger LOGGER = Logger.getLogger(CompletedTaskProducer.class);

    @Autowired
    private JmsTemplate completedDbTaskProducerTemplate;

    /**
     * Send the serialized CompletedDbTask to the completed queue.
     *
     * @param completedDbTask the CompletedDbTask
     * @throws JmsException
     */
    public void sendCompletedDbTask(final CompletedDbTask completedDbTask) throws JmsException {

        completedDbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set CompletedDbTask instance as message body
                ObjectMessage completedDbTask = session.createObjectMessage();

                LOGGER.info("Sending completed db task");

                return completedDbTask;
            }
        });
    }

}
