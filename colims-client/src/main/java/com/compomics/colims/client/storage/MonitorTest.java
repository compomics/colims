package com.compomics.colims.client.storage;

import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("monitorTest")
public class MonitorTest {

    @Autowired
    private MBeanServerConnection clientConnector;

    public void getQueueInfo() throws InvalidSelectorException, OpenDataException {
        try {
//            ObjectName objectNameRequest = new ObjectName("org.apache.activemq:brokerName=localhost,Type=Queue,Destination=com.compomics.distributed.queue.error");
//            QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectNameRequest, QueueViewMBean.class, false);
//
//            System.out.println("queue: " + queueMbean.getQueueSize());

            ObjectName activeMQ = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
            BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, activeMQ, BrokerViewMBean.class, true);

            for (ObjectName name : mbean.getQueues()) {
                QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, name, QueueViewMBean.class, true);
                System.out.println("testing: " + queueMbean.getQueueSize());
                List messages = queueMbean.browseMessages();
                TabularData browseAsTable = queueMbean.browseAsTable();
                CompositeData[] browse = queueMbean.browse();
                System.out.println("test");
//            }                        

                System.out.println("test");
            }
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(MonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
