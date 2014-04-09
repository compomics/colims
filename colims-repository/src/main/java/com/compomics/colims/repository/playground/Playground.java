package com.compomics.colims.repository.playground;

import com.compomics.colims.model.Project;
import com.compomics.colims.repository.ProjectRepository;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");

        LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean) applicationContext.getBean("&sessionFactory", LocalSessionFactoryBean.class);

        SchemaExport schemaExport = new SchemaExport(sessionFactory.getConfiguration());
        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.sql");
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.execute(true, false, false, true);                           

    }
       
}
