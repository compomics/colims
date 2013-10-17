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
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;
import uk.ac.ebi.ook.web.model.DataHolder;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();                        
        
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
        
        Query olsClient = (Query) applicationContext.getBean("olsClient");
        Map ontologyNames = olsClient.getOntologyNames();
        for (MapItem mapItem : ontologyNames.getItem()) {
            if (mapItem.getKey().equals("MS")) {
                System.out.println(mapItem.getKey());
                System.out.println(mapItem.getValue());
            }
        }
        Map termsByExactName = olsClient.getTermsByExactName("electrospray ionization", "MS");  
        Map termXrefs = olsClient.getTermXrefs("MS:1000073", "MS");
        Map termMetadata = olsClient.getTermMetadata("MS:1000073", "MS");
        
        List<DataHolder> termsByAnnotationData = olsClient.getTermsByAnnotationData("MOD", "DiffMono", null, 15.894915000000001, 16.094915);
        
        System.out.println("test");
    }
}
