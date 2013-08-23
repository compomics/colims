package com.compomics.colims.core.playground;

import com.compomics.colims.core.io.peptideshaker.model.CpsParentImpl;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.PermissionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.service.impl.InstrumentServiceImpl;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
        
        
        PermissionService permissionService = applicationContext.getBean("permissionService", PermissionService.class);
//        Permission permission = new Permission();
//        permission.setName("bonobo");
//        permission.setDescription("bonobo desc");
//        permissionService.save(permission);
        
        List<Permission> findAll = permissionService.findAll();
                
        Permission findByName = permissionService.findByName("bonobo");
        findByName.setDescription("djjjjjjjjjjjjjjjjjjjjjjjjjfjdjdj");
        permissionService.update(findByName);
        
        
        System.out.println("test");
        //
        //        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
        //        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
        //        schemaExport.setFormat(true);
        //        schemaExport.setDelimiter(";");
        //        schemaExport.execute(true, false, false, true);
        //        CpsParentImpl cpsParentImpl = new CpsParentImpl();
        //        cpsParentImpl.setCpsFile(new File("C:\\Users\\niels\\Desktop\\test\\test_peptideshaker_project_3.cps"));
        //        cpsParentImpl.loadCpsFile(null);
        //        cpsParentImpl.loadSpectrumFiles(null);
        //        System.out.println("test");
    }
}
