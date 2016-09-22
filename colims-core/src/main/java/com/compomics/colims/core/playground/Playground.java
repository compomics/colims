package com.compomics.colims.core.playground;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(final String[] args) throws IOException, ClassNotFoundException, SQLException {
//        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
//        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "http://www.ebi.ac.uk/ols/beta";
        ResponseEntity<String> response =
                restTemplate.getForEntity(fooResourceUrl + "/api/ontologies?page=1&size=1", String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode name = root.path("name");

        System.out.println("test");

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

//        Query olsClient = (Query) applicationContext.getBean("olsClient");
//        Map ontologyNames = olsClient.getOntologyNames();
//        for (MapItem mapItem : ontologyNames.getItem()) {
//            if (mapItem.getKey().equals("MS")) {
//                System.out.println(mapItem.getKey());
//                System.out.println(mapItem.getValue());
//            }
//        }
//        Map termsByName = olsClient.getTermsByName("oxidation of m", "MOD", false);  
//        Map termsByExactName = olsClient.getTermsByExactName("methionine oxidation with neutral loss of 64 Da", "MOD");  
//        Map termXrefs = olsClient.getTermXrefs("MOD:00935", "MOD");
//        Map termMetadata = olsClient.getTermMetadata("MOD:00935", "MOD");
//        
//        List<DataHolder> termsByAnnotationData = olsClient.getTermsByAnnotationData("MOD", "DiffMono", null, 15.894915000000001, 16.094915);
    }
}
