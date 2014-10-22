package com.compomics.colims.client.distributed.producer;

import com.compomics.colims.distributed.model.DbTask;
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
 * This class sends DbTask instances to the dbtask queue.
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
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate dbTaskProducerTemplate;

    /**
     * Send the serialized DbTask to the db task queue.
     *
     * @param dbTask the DbTask
     */
    public void sendDbTask(final DbTask dbTask) {

        dbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                //set DbTask instance as message body
                ObjectMessage dbTaskTaskMessage = session.createObjectMessage(dbTask);

                LOGGER.info("Sending db task of class " + dbTaskTaskMessage.getClass().getSimpleName());

                return dbTaskTaskMessage;
            }
        });
    }

}
