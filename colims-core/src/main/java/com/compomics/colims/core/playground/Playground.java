package com.compomics.colims.core.playground;

import org.springframework.context.ApplicationContext;
import com.compomics.colims.core.config.ApplicationContextProvider;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;
import uk.ac.ebi.ook.web.model.DataHolder;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {    
    
    public static void main(final String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();                        
        
        //
        //        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
        //        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
        //        schemaExport.setFormat(true);
        //        schemaExport.setDelimiter(";");
        //        schemaExport.execute(true, false, false, true);
        //        CpsParentImpl cpsParentImpl = new CpsParentImpl();
        //        cpsParentImpl.setCpsFile(new File("C:\\Users\\niels\\Desktop\\test\\data/peptideshaker/test_peptideshaker_project.cps"));
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
        Map termsByName = olsClient.getTermsByName("oxidation of m", "MOD", false);  
        Map termsByExactName = olsClient.getTermsByExactName("methionine oxidation with neutral loss of 64 Da", "MOD");  
        Map termXrefs = olsClient.getTermXrefs("MOD:00935", "MOD");
        Map termMetadata = olsClient.getTermMetadata("MOD:00935", "MOD");
        
        List<DataHolder> termsByAnnotationData = olsClient.getTermsByAnnotationData("MOD", "DiffMono", null, 15.894915000000001, 16.094915);
        
        System.out.println("test");
    }
}
