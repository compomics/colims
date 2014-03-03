package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.storage.model.StorageMetadata;
import com.compomics.colims.distributed.storage.model.StorageTask;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQObjectMessage;
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

    public List<StorageMetadata> getMessages(String queueName) throws JMSException {
        List<StorageMetadata> messages = new ArrayList<>();

        Connection connection = cachingConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        QueueBrowser queueBrowser = session.createBrowser(session.createQueue("com.compomics.distributed.queue.storage"));

        Enumeration enumeration = queueBrowser.getEnumeration();

        while (enumeration.hasMoreElements()) {
            ActiveMQObjectMessage message = (ActiveMQObjectMessage) enumeration.nextElement();
            StorageTask storageTask = (StorageTask) message.getObject();
            
            messages.add(storageTask.getStorageMetadata());
        }

        session.close();
        connection.close();

        return messages;
    }

}
