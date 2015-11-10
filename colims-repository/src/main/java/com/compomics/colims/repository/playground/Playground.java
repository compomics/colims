package com.compomics.colims.repository.playground;

import com.compomics.colims.model.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) throws IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = applicationContext.getBean("&entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);

//        final Properties prop = new Properties();
//        prop.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect");
//        final PersistenceUnitInfo info = entityManagerFactoryBean.getPersistenceUnitInfo();
//        final PersistenceUnitInfoDescriptor puid = new PersistenceUnitInfoDescriptor(info);
//        final EntityManagerFactoryBuilderImpl emfbi = new EntityManagerFactoryBuilderImpl(puid, prop);
//        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
//
//
//        MetadataSources metadata = new MetadataSources(
//                new StandardServiceRegistryBuilder()
//                        .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")
//                        .build());
//
//        info.getManagedClassNames().forEach(metadata::addAnnotatedClassName);
//
//        MetadataImplementor metadataImplementor = (MetadataImplementor) metadata.buildMetadata();
//
//        SchemaExport schemaExport = new SchemaExport(metadataImplementor);
//        schemaExport.setOutputFile("/home/niels/Desktop/testing.sql");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);
    }

}
