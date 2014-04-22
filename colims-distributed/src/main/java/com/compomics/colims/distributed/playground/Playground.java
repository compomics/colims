
package com.compomics.colims.distributed.playground;

import com.compomics.colims.distributed.consumer.DbTaskConsumer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");
               
        DbTaskConsumer storageTaskConsumer = applicationContext.getBean("storageTaskConsumer", DbTaskConsumer.class);                   
    }
    
}
