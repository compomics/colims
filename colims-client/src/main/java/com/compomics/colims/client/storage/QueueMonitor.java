package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.StorageMetadata;
import com.compomics.colims.distributed.model.StorageTask;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("queueMonitor")
public class QueueMonitor {    

    @Autowired
    private JmsTemplate queueMonitorTemplate;
    
    public <T> List<T> monitorQueue(String queueName) throws JMSException {
        List<T> messages = queueMonitorTemplate.browse(queueName, new BrowserCallback<List<T>>() {

            @Override
            public List<T> doInJms(Session session, QueueBrowser browser) throws JMSException {
                Enumeration enumeration = browser.getEnumeration();
                List<T> queueMessages = new ArrayList<>();

                while (enumeration.hasMoreElements()) {
                    ActiveMQObjectMessage message = (ActiveMQObjectMessage) enumeration.nextElement();

                    queueMessages.add((T) message.getObject());
                }

                return queueMessages;
            }
        });

        return messages;
    }

}
