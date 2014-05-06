package com.compomics.colims.client.storage.impl;

import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.client.storage.DbTaskErrorMessageConvertor;
import com.compomics.colims.distributed.model.QueueMessage;
import com.compomics.colims.distributed.model.DbTaskError;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = Logger.getLogger(QueueManagerImpl.class);

    @Value("${distributed.broker.name}")
    private String brokerName;
    @Value("${distributed.connectionfactory.broker.url}")
    private String brokerUrl;
    @Value("${distributed.jmx.service.url}")
    private String brokerJmxUrl;
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    private final DbTaskErrorMessageConvertor storageErrorMessageConvertor = new DbTaskErrorMessageConvertor();
    private final String queueObjectName = "org.apache.activemq:type=Broker,brokerName=%s,destinationType=Queue,destinationName=%s";
    private final String brokerObjectName = "org.apache.activemq:type=Broker,brokerName=%s";
    @Autowired
    private JmsTemplate queueManagerTemplate;
    @Autowired
    private MBeanServerConnection clientConnector;

    @Override
    public <T extends QueueMessage> List<T> monitorQueue(String queueName) throws JMSException {
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
        ObjectName objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
        QueueViewMBean queueViewMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.removeMessage(messageId);
    }

    @Override
    public void purgeMessages(String queueName) throws MalformedObjectNameException, Exception {
        ObjectName objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
        QueueViewMBean queueViewMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.purge();
    }

    @Override
    public void redirectStorageError(String queueName, DbTaskError dbTaskError) throws JMSException, MalformedObjectNameException, Exception {
        //set appropriate message convertor
        if (!(queueManagerTemplate.getMessageConverter() instanceof DbTaskErrorMessageConvertor)) {
            queueManagerTemplate.setMessageConverter(storageErrorMessageConvertor);
        }

        //send the message
        queueManagerTemplate.convertAndSend(queueName, dbTaskError);

        //remove the message from the error queue
        deleteMessage(errorQueueName, dbTaskError.getMessageId());
    }

    @Override
    public boolean testConnection() {
        boolean connectionAchieved = false;

        try {
            ObjectName activeMQ = new ObjectName(String.format(brokerObjectName, brokerName));
            BrokerViewMBean brokerViewMBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, activeMQ, BrokerViewMBean.class, true);
            //get broker ID to test the connection
            String brokerId = brokerViewMBean.getBrokerId();

            connectionAchieved = true;
        } catch (MalformedObjectNameException | UndeclaredThrowableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return connectionAchieved;
    }

    @Override
    public String getBrokerName() {
        return brokerName;
    }

    @Override
    public String getBrokerUrl() {
        return brokerUrl;
    }

    @Override
    public String getBrokerJmxUrl() {
        return brokerJmxUrl;
    }

}
