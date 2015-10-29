package com.compomics.colims.client.distributed.impl;

import com.compomics.colims.client.distributed.DbTaskErrorMessageConvertor;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.QueueMessage;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This class provides methods for monitoring and managing the distributed module queues and topics.
 *
 * @author Niels Hulstaert
 */
@Component("queueManager")
public class QueueManagerImpl implements QueueManager {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(QueueManagerImpl.class);

    /**
     * The broker name.
     */
    @Value("${distributed.broker.name}")
    private String brokerName;
    /**
     * The broker URL.
     */
    @Value("${distributed.connectionfactory.broker.url}")
    private String brokerUrl;
    /**
     * The broker JMX URL.
     */
    @Value("${distributed.jmx.service.url}")
    private String brokerJmxUrl;
    /**
     * The name of the error queue.
     */
    @Value("${distributed.queue.error}")
    private String errorQueueName;
    /**
     * The DbTaskError to DbTask convertor.
     */
    private final DbTaskErrorMessageConvertor storageErrorMessageConvertor = new DbTaskErrorMessageConvertor();
    /**
     * The queue object name with placeholders for the broker and destination names.
     */
    private final String queueObjectName = "org.apache.activemq:type=Broker,brokerName=%s,destinationType=Queue,destinationName=%s";
    /**
     * the broker object name with a placeholder for the broker name.
     */
    private final String brokerObjectName = "org.apache.activemq:type=Broker,brokerName=%s";
    /**
     * Mapper for converting a JSON construct to the matching java object.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate queueManagerTemplate;
    /**
     * The MBean connector.
     */
    @Autowired
    private MBeanServerConnection clientConnector;

    @Override
    public <T extends QueueMessage> List<T> monitorQueue(final String queueName, final Class<T> clazz) {

        return queueManagerTemplate.browse(queueName, new BrowserCallback<List<T>>() {

            @Override
            public List<T> doInJms(final Session session, final javax.jms.QueueBrowser browser) throws JMSException {
                Enumeration enumeration = browser.getEnumeration();
                List<T> queueMessages = new ArrayList<>();

                while (enumeration.hasMoreElements()) {
                    try {
                        //get the JSON construct
                        TextMessage jsonConstruct = (TextMessage) enumeration.nextElement();
                        //map it to it's corresponding java class

                        String text = jsonConstruct.getText();
                        T mappedInstance = objectMapper.readValue(jsonConstruct.getText(), clazz);

                        mappedInstance.setMessageId(jsonConstruct.getJMSMessageID());

                        queueMessages.add(mappedInstance);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                        //@todo what's the best way of wrapping this exception?
                        throw new JMSException(e.getMessage());
                    }
                }

                return queueMessages;
            }
        });
    }

    @Override
    public void deleteMessage(final String queueName, final String messageId) throws Exception {
        ObjectName objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
        QueueViewMBean queueViewMBean = MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.removeMessage(messageId);
    }

    @Override
    public void purgeMessages(final String queueName) throws Exception {
        ObjectName objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
        QueueViewMBean queueViewMBean = MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectName, QueueViewMBean.class, true);
        queueViewMBean.purge();
    }

    @Override
    public void redirectStorageError(final String queueName, final DbTaskError dbTaskError) throws Exception {
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
    public boolean isReachable() {
        boolean connectionAchieved = false;

        try {
            ObjectName activeMQ = new ObjectName(String.format(brokerObjectName, brokerName));
            BrokerViewMBean brokerViewMBean = MBeanServerInvocationHandler.newProxyInstance(clientConnector, activeMQ, BrokerViewMBean.class, true);
            //get broker ID to test the connection
            @SuppressWarnings("UnusedAssignment") String brokerId = brokerViewMBean.getBrokerId();

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
