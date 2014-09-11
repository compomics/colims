
package com.compomics.colims.distributed.playground;

import com.compomics.colims.distributed.model.Notification;
import com.compomics.colims.distributed.model.enums.NotificationType;
import com.compomics.colims.distributed.producer.NotificationProducer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) {
//        try {
//            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");
//            
//            NotificationProducer notificationProducer = (NotificationProducer) applicationContext.getBean("notificationProducer");
//            
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdjdjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdddfdfjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdjgfbhbhjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdjbfbdfbsjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdngnmgmmjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "djdggjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "ghttjjdjdjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "jyjyjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "dyjyjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "ththdjjssh"));
//            Thread.sleep(5000L);
//            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, "ththjssh"));
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Playground.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        System.out.println("----------------------" + System.getProperty("java.io.tmpdir"));
    }
    
}
