package com.compomics.colims.client.distributed.producer;

import com.compomics.colims.distributed.model.DbTask;
import com.compomics.colims.distributed.model.PersistDbTask;
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
@Component("dbTaskProducer")
public class DbTaskProducer {

    private static final Logger LOGGER = Logger.getLogger(DbTaskProducer.class);

    @Autowired
    private JmsTemplate dbTaskProducerTemplate;

    /**
     * Send the serialized DbTask to the db task queue.
     *
     * @param dbTask the DbTask
     * @throws JmsException
     */
    public void sendDbTask(final DbTask dbTask) throws JmsException {

        dbTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set DbTask instance as message body
                ObjectMessage dbTaskTaskMessage = session.createObjectMessage(dbTask); 

                LOGGER.info("Sending db task of class " + dbTaskTaskMessage.getClass().getSimpleName());

                return dbTaskTaskMessage;
            }
        });
    }

}
