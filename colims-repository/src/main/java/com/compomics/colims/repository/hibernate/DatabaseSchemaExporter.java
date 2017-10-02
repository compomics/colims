package com.compomics.colims.repository.hibernate;

import java.util.EnumSet;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * This class exports the Colims schema to file.
 * <p>
 * Created by Niels Hulstaert on 11/05/16.
 */
public class DatabaseSchemaExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSchemaExporter.class);
    
    public static void main(final String[] args) throws IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = applicationContext.getBean("&entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);

        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) entityManagerFactoryBean.getNativeEntityManagerFactory();
        SessionFactoryImplementor sf = emf.getSessionFactory();
        SessionFactoryServiceRegistryImpl serviceRegistry = (SessionFactoryServiceRegistryImpl) sf.getServiceRegistry();

        LOGGER.info("Starting schema export");
        
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile("/home/niels/Desktop/colims_export.sql");
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.SCRIPT);
        schemaExport.createOnly(targetTypes, MetadataProvider.getMetadata());
    }

}
