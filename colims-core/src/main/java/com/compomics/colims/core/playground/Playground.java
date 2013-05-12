package com.compomics.colims.core.playground;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {    
    
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");
        
        LocalSessionFactoryBean sessionFactoryBean = (LocalSessionFactoryBean) applicationContext.getBean("&sessionFactory");

        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.execute(true, false, false, true);
    }
}
