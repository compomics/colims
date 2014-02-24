package com.compomics.colims.distributed.playground;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
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
@Component("storageQueueProducer")
public class StorageQueueProducer {

    private static final Logger LOGGER = Logger.getLogger(StorageQueueProducer.class);
    protected static final String MESSAGE_COUNT = "messageCount";

    @Autowired
    private JmsTemplate template = null;
    private int messageCount = 100;

    /**
     * Generates JMS messages
     */
    public void generateMessages() throws JmsException {
        for (int i = 0; i < messageCount; i++) {
            final int index = i;
            final String text = "Message number is " + i + ".";
            final TestBean testBean = new TestBean("testName " + i, "testAddress " + i);

            template.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
//                    TextMessage message = session.createTextMessage(text);                    
                    ObjectMessage message = session.createObjectMessage(testBean);
                    
                    message.setIntProperty(MESSAGE_COUNT, index);

                    LOGGER.info("Sending message: " + testBean);

                    return message;
                }
            });
        }
    }

}
