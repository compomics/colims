package com.compomics.colims.repository.playground;

import com.compomics.colims.repository.ProteinRepository;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.Arrays;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-repository-context.xml");

//        LocalSessionFactoryBean sessionFactory = applicationContext.getBean("&sessionFactory", LocalSessionFactoryBean.class);
//
//        SchemaExport schemaExport = new SchemaExport(sessionFactory.getConfiguration());
//        schemaExport.setOutputFile("/home/niels/Desktop/testing.sql");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);

        ProteinRepository proteinRepository = applicationContext.getBean("proteinRepository", ProteinRepository.class);
        proteinRepository.getConstraintLessProteinIdsForRuns(Arrays.asList(1L));

    }

}
