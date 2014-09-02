
package com.compomics.colims.distributed.playground;

import com.compomics.colims.distributed.model.Notification;
import com.compomics.colims.distributed.producer.NotificationProducer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");
               
        NotificationProducer notificationProducer = (NotificationProducer) applicationContext.getBean("notificationProducer");
        notificationProducer.sendNotification(new Notification());
    }
    
}
