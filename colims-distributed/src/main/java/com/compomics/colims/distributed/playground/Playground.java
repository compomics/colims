
package com.compomics.colims.distributed.playground;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");

        StorageQueueProducer storageQueueProducer = applicationContext.getBean("storageQueueProducer", StorageQueueProducer.class);
        storageQueueProducer.generateMessages();
    }
    
}
