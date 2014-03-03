package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.playground.TestBean;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("monitorTest")
public class MonitorTest {

//    @Autowired
    private MBeanServerConnection clientConnector;

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    public void getQueueInfo() {
        try {
//            ObjectName objectNameRequest = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Queue,Destination=com.compomics.distributed.queue.storage");
//            QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, objectNameRequest, QueueViewMBean.class, false);
//
//            System.out.println("queue: " + queueMbean.getQueueSize());

            ObjectName activeMQ = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
            BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, activeMQ, BrokerViewMBean.class, true);

            for (ObjectName name : mbean.getQueues()) {
                QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(clientConnector, name, QueueViewMBean.class, true);
                System.out.println("testing: " + queueMbean.getQueueSize());
//                TabularData browseAsTable = queueMbean.browseAsTable();
//                CompositeData[] browse = queueMbean.browse();
//                System.out.println("test");
//            }                        

                System.out.println("test");
            }
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(MonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void getQueueInfo2() {
        try {
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
        } catch (JMSException ex) {
            Logger.getLogger(MonitorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
