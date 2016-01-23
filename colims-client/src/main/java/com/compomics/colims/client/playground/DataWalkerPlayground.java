package com.compomics.colims.client.playground;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import java.util.Iterator;

/**
 * Created by Davy Maddelein on 20/08/2015.
 */
public class DataWalkerPlayground {

    public static void main(String[] args){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");
        LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean)applicationContext.getBean("&sessionFactory");

        Iterator<PersistentClass>iter = sessionFactory.getConfiguration().getClassMappings();

        while(iter.hasNext()){
            PersistentClass dbTable = iter.next();
            Iterator<Column> columns = dbTable.getTable().getColumnIterator();
            while(columns.hasNext()){
                Column column = columns.next();
                System.out.println(column.getName());
            }
        }

    }
}
