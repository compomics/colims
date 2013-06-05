package com.compomics.colims.client.playground;

import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {
    
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");
        Query olsClient = (Query) applicationContext.getBean("olsClient");
        Map ontologyNames = olsClient.getOntologyNames();
        for (MapItem mapItem : ontologyNames.getItem()) {
            if (mapItem.getKey().equals("MS")) {
                System.out.println(mapItem.getKey());
                System.out.println(mapItem.getValue());
            }
        }
        Map termsByExactName = olsClient.getTermsByExactName("electrospray ionization", "MS");        
    }
}
