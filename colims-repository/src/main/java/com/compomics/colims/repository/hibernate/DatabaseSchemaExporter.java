package com.compomics.colims.repository.hibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * This class exports the Colims schema to file.
 * <p>
 * Created by Niels Hulstaert on 11/05/16.
 */
public class DatabaseSchemaExporter {

    public static void main(final String[] args) throws IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = applicationContext.getBean("&entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);

        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) entityManagerFactoryBean.getNativeEntityManagerFactory();
        SessionFactoryImplementor sf = emf.getSessionFactory();
        SessionFactoryServiceRegistryImpl serviceRegistry = (SessionFactoryServiceRegistryImpl) sf.getServiceRegistry();

        SchemaExport schemaExport = new SchemaExport(serviceRegistry, MetadataProvider.getMetadata());
        schemaExport.setOutputFile("/home/niels/Desktop/colims_export.sql");
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.execute(false, false, false, true);
    }

}