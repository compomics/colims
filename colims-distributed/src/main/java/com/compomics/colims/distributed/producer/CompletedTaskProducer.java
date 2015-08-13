package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
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
     * The JmsTemplate.
     */
    @Autowired
    private JmsTemplate completedDbTaskProducerTemplate;

    /**
     * Send the serialized CompletedDbTask to the completed queue.
     *
     * @param completedDbTask the CompletedDbTask
     */
    public void sendCompletedDbTask(final CompletedDbTask completedDbTask) {

        completedDbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                //set CompletedDbTask instance as message body
                ObjectMessage completedDbTaskMessage = session.createObjectMessage(completedDbTask);

                LOGGER.info("Sending completed db task");

                return completedDbTaskMessage;
            }
        });
    }

}
