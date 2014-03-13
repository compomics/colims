package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.StorageError;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("queueManager")
public class QueueManager {

    private StorageErrorMessageConvertor storageErrorMessageConvertor = new StorageErrorMessageConvertor();

    @Autowired
    private JmsTemplate queueManagerTemplate;

    public <T> List<T> monitorQueue(String queueName) throws JMSException {
        List<T> messages = queueManagerTemplate.browse(queueName, new BrowserCallback<List<T>>() {

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

    public <T> Map<String, T> monitorQueueAsMap(String queueName) throws JMSException {
        Map<String, T> messages = queueManagerTemplate.browse(queueName, new BrowserCallback<Map<String, T>>() {

            @Override
            public Map<String, T> doInJms(Session session, QueueBrowser browser) throws JMSException {
                Enumeration enumeration = browser.getEnumeration();
                Map<String, T> queueMessages = new HashMap<>();

                while (enumeration.hasMoreElements()) {
                    ActiveMQObjectMessage message = (ActiveMQObjectMessage) enumeration.nextElement();
                   
                    queueMessages.put(message.getJMSMessageID(), (T) message.getObject());
                }

                return queueMessages;
            }
        });

        return messages;
    }
    
    public void redirectStorageError(String queueName, StorageError storageError) throws JMSException {
        //
                
        //set appropriate message convertor
        if (!(queueManagerTemplate.getMessageConverter() instanceof StorageErrorMessageConvertor)) {
            queueManagerTemplate.setMessageConverter(storageErrorMessageConvertor);
        }
        queueManagerTemplate.convertAndSend(queueName, storageError);
    }

}
