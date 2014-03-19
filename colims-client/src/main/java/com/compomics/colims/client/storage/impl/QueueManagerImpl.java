package com.compomics.colims.client.storage.impl;

import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.client.storage.StorageErrorMessageConvertor;
import com.compomics.colims.distributed.model.AbstractMessage;
import com.compomics.colims.distributed.model.StorageError;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("queueManager")
public class QueueManagerImpl implements QueueManager {

    @Value("${distributed.broker.name}")
    private String brokerName;
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    private final StorageErrorMessageConvertor storageErrorMessageConvertor = new StorageErrorMessageConvertor();
    private final String name = "org.apache.activemq:brokerName=" + brokerName + ",destinationName=%s,destinationType=Queue,type=Broker";
    @Autowired
    private JmsTemplate queueManagerTemplate;
    @Autowired
    private MBeanServerConnection clientConnector;    

    @Override
    public <T extends AbstractMessage> List<T> monitorQueue(String queueName) throws JMSException {
        List<T> messages = queueManagerTemplate.browse(queueName, new BrowserCallback<List<T>>() {

            @Override
            public List<T> doInJms(Session session, javax.jms.QueueBrowser browser) throws JMSException {
                Enumeration enumeration = browser.getEnumeration();
                List<T> queueMessages = new ArrayList<>();

                while (enumeration.hasMoreElements()) {
                    ActiveMQObjectMessage message = (ActiveMQObjectMessage) enumeration.nextElement();
                    T messageObject = (T) message.getObject();
                    messageObject.setMessageId(message.getJMSMessageID());
                    
                    queueMessages.add((T) message.getObject());
                }

                return queueMessages;
            }
        });

        return messages;
    }

    @Override
    public void deleteMessage(String queueName, String messageId) throws MalformedObjectNameException, Exception {        
        ObjectName objectName = new ObjectName(String.format(name, queueName));
        QueueViewMBean queueViewMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.removeMessage(messageId);
    }

    @Override
    public void purgeMessages(String queueName) throws MalformedObjectNameException, Exception {        
        ObjectName objectName = new ObjectName(String.format(name, queueName));
        QueueViewMBean queueViewMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.purge();
    }

    @Override
    public void redirectStorageError(String queueName, StorageError storageError) throws JMSException, MalformedObjectNameException, Exception {
        //set appropriate message convertor
        if (!(queueManagerTemplate.getMessageConverter() instanceof StorageErrorMessageConvertor)) {
            queueManagerTemplate.setMessageConverter(storageErrorMessageConvertor);
        }
        
        //send the message
        queueManagerTemplate.convertAndSend(queueName, storageError);

        //remove the message from the error queue
        deleteMessage(errorQueueName, storageError.getMessageId());
    }

}
