package com.compomics.colims.client.storage;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("queueMonitor")
public class QueueMonitor {

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    public void getQueueBrowser(String queueName) throws JMSException {                
        Connection connection = cachingConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        QueueBrowser queueBrowser = session.createBrowser(session.createQueue("com.compomics.distributed.queue.storage"));

        Enumeration enumeration = queueBrowser.getEnumeration();

        while (enumeration.hasMoreElements()) {
            Message message = (Message) enumeration.nextElement();
            Enumeration propertyNames = message.getPropertyNames();

            while (propertyNames.hasMoreElements()) {
                String propertyName = (String) propertyNames.nextElement();
                System.out.println("value: " + propertyName);
                System.out.println("value: " + message.getStringProperty(propertyName));
            }
            System.out.println("test");
        }

        session.close();
        connection.close();
    }

}
