package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.Modification;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 *
 * @author Niels Hulstaert
 */
@Service("olsService")
public class OlsServiceImpl implements OlsService {

    @Autowired
    private Query olsClient;

    @Override
    public Modification findModifiationByExactName(final String name) {
        Modification modification = null;

        //find the modification by exact name        
        Map modificationTerms = olsClient.getTermsByExactName(name, "MOD");
        if (modificationTerms.getItem() != null) {
            //get the modificiation accession
            for (MapItem mapItem : modificationTerms.getItem()) {
                modification = findModifiationByAccession(mapItem.getKey().toString());
            }
        }

        return modification;
    }
    
    @Override
    public List<Modification> findModifiationByName(String name) {
        List<Modification> modifications = new ArrayList<>();
        
        //find the modifications by name        
        Map modificationsTerms = olsClient.getTermsByName(name, "MOD", false);
        if (modificationsTerms.getItem() != null) {
            //get the modificiations
            for (MapItem mapItem : modificationsTerms.getItem()) {
                Modification modification = findModifiationByAccession(mapItem.getKey().toString());
                if(modification != null){
                    modifications.add(modification);
                }
            }
        }
        
        return modifications;
    }

    @Override
    public Modification findModifiationByAccession(final String accession) {
        Modification modification = null;

        //get the modification name
        String modificationName = olsClient.getTermById(accession, "MOD");

        //check if a term was found
        if (!accession.equals(modificationName)) {
            //get the term metadata by accession
            Map modificationMetaData = olsClient.getTermMetadata(accession, "MOD");
            if (modificationMetaData.getItem() != null) {
                modification = new Modification(accession, modificationName);

                //get the modificiation properties
                for (MapItem mapItem : modificationMetaData.getItem()) {
                    if (mapItem.getKey() != null & mapItem.getValue() != null) {
                        if (mapItem.getKey().equals("MassMono")) {
                            modification.setMonoIsotopicMass(Double.parseDouble(mapItem.getValue().toString()));
                        } else if (mapItem.getKey().equals("DiffMono")) {
                            modification.setMonoIsotopicMassShift(Double.parseDouble(mapItem.getValue().toString()));
                        } else if (mapItem.getKey().equals("MassAvg")) {
                            modification.setAverageMass(Double.parseDouble(mapItem.getValue().toString()));
                        } else if (mapItem.getKey().equals("DiffAvg")) {
                            modification.setAverageMassShift(Double.parseDouble(mapItem.getValue().toString()));
                        }
                    }
                }
            }
        }

        return modification;
    }
    
}
