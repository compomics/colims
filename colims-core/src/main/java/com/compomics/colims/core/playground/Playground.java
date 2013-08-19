package com.compomics.colims.core.playground;

import com.compomics.colims.core.io.peptideshaker.model.CpsParentImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-core-context.xml");
//        
//        LocalSessionFactoryBean sessionFactoryBean = (LocalSessionFactoryBean) applicationContext.getBean("&sessionFactory");
//
//        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
//        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);
        
        CpsParentImpl cpsParentImpl = new CpsParentImpl();
        cpsParentImpl.setCpsFile(new File("C:\\Users\\niels\\Desktop\\test\\test_peptideshaker_project_3.cps"));
        cpsParentImpl.loadCpsFile(null);
        cpsParentImpl.loadSpectrumFiles(null);
        System.out.println("test");
    }
}
