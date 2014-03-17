package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.AbstractMessage;
import com.compomics.colims.distributed.model.StorageError;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;
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
@Component("queueBrowser")
public class QueueBrowser {

    private StorageErrorMessageConvertor storageErrorMessageConvertor = new StorageErrorMessageConvertor();

    @Autowired
    private JmsTemplate queueBrowserTemplate;

    /**
     * Monitor the queue with the given queue name. Returns a list of objects of
     * class T.
     *
     * @param <T>
     * @param queueName
     * @return
     * @throws JMSException
     */
    public <T> List<T> monitorQueue(String queueName) throws JMSException {
        List<T> messages = queueBrowserTemplate.browse(queueName, new BrowserCallback<List<T>>() {

            @Override
            public List<T> doInJms(Session session, javax.jms.QueueBrowser browser) throws JMSException {
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

    public void redirectStorageError(String queueName, StorageError storageError) throws JMSException {
        //

        //set appropriate message convertor
        if (!(queueBrowserTemplate.getMessageConverter() instanceof StorageErrorMessageConvertor)) {
            queueBrowserTemplate.setMessageConverter(storageErrorMessageConvertor);
        }
        queueBrowserTemplate.convertAndSend(queueName, storageError);
    }

}
