package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.Modification;

import java.util.ArrayList;
import java.util.List;

import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

/**
 * @author Niels Hulstaert
 */
@Service("olsService")
public class OlsServiceImpl implements OlsService {

    private static final String MOD_ONTOLOGY_LABEL = "MOD";
    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";
    private static final Logger LOGGER = Logger.getLogger(OlsServiceImpl.class);

    @Autowired
    private Query olsClient;

    @Override
    public <T extends AbstractModification> T findModificationByExactName(Class<T> clazz, final String name) {
        T modification = null;

        //find the modification by exact name
        Map modificationTerms = olsClient.getTermsByExactName(name, MOD_ONTOLOGY_LABEL);
        if (modificationTerms.getItem() != null) {
            //get the modification accession
            for (org.apache.xml.xml_soap.MapItem mapItem : modificationTerms.getItem()) {
                modification = findModificationByAccession(clazz, mapItem.getKey().toString());
            }
        }

        return modification;
    }

    @Override
    public List<Modification> findModificationByName(final String name) {
        List<Modification> modifications = new ArrayList<>();

        //find the modifications by name
        Map modificationsTerms = olsClient.getTermsByName(name, MOD_ONTOLOGY_LABEL, false);
        if (modificationsTerms.getItem() != null) {
            //get the modificiations
            for (org.apache.xml.xml_soap.MapItem mapItem : modificationsTerms.getItem()) {
                Modification modification = findModificationByAccession(Modification.class, mapItem.getKey().toString());
                if (modification != null) {
                    modifications.add(modification);
                }
            }
        }
        return modifications;
    }

    @Override
    public <T extends AbstractModification> T findModificationByAccession(Class<T> clazz, String accession) {
        T modification = null;

        //ReflectionUtils.

        //get the modification name
        String searchModificationName = olsClient.getTermById(accession, MOD_ONTOLOGY_LABEL);

        //check if a term was found
        if (!accession.equals(searchModificationName)) {
            //get the term metadata by accession
            Map modificationMetaData = olsClient.getTermMetadata(accession, MOD_ONTOLOGY_LABEL);
            if (modificationMetaData.getItem() != null) {
                try {
                    modification = clazz.newInstance();
                    modification.setAccession(accession);
                    modification.setName(searchModificationName);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error(e.getMessage(), e);
                }

                //get the modification properties
                for (MapItem mapItem : modificationMetaData.getItem()) {
                    if (mapItem.getKey() != null && mapItem.getValue() != null) {
                        if (mapItem.getKey().equals("DiffMono")) {
                            modification.setMonoIsotopicMassShift(Double.parseDouble(mapItem.getValue().toString()));
                        } else if (mapItem.getKey().equals("DiffAvg")) {
                            modification.setAverageMassShift(Double.parseDouble(mapItem.getValue().toString()));
                        }
                    }
                }
            }
        }

        return modification;
    }

    @Override
    public <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession) {
        T modification = null;

        //first, find the modifications by name
        Map modificationsTerms = olsClient.getTermsByName(name, MOD_ONTOLOGY_LABEL, false);
        if (modificationsTerms.getItem() != null) {
            String tempAccession = null;
            //iterate over the modifications
            outerloop:
            for (MapItem mapItem : modificationsTerms.getItem()) {
                String accession = mapItem.getKey().toString();
                //get the Xrefs
                Map termXrefs = olsClient.getTermXrefs(accession, MOD_ONTOLOGY_LABEL);
                for (MapItem xref : termXrefs.getItem()) {
                    if (StringUtils.containsIgnoreCase(xref.getValue().toString(), unimodAccession)) {
                        if (xref.getValue().toString().equalsIgnoreCase(unimodAccession)) {
                            T foundModification = findModificationByAccession(clazz, accession);
                            if (foundModification != null) {
                                modification = foundModification;
                                modification.setAlternativeAccession(unimodAccession);
                                break outerloop;
                            }
                        } else {
                            //keep track of the next best thing
                            tempAccession = accession;
                        }
                    }
                }
            }
            if (modification == null && tempAccession != null) {
                T foundModification = findModificationByAccession(clazz, tempAccession);
                if (foundModification != null) {
                    modification = foundModification;
                    modification.setAlternativeAccession(unimodAccession);
                }
            }
        }

        return modification;
    }

    @Override
    public TypedCvParam findEnzymeByName(String name) {
        TypedCvParam enzyme = null;

        //find the enzyme by name
        Map enzymeTerms = olsClient.getTermsByName(name, MS_ONTOLOGY_LABEL, false);
        if (enzymeTerms.getItem() != null) {
            for (MapItem mapItem : enzymeTerms.getItem()) {
                String enzymeName = mapItem.getValue().toString();
                if (enzymeName.equalsIgnoreCase(name)) {
                    enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, mapItem.getKey().toString(), enzymeName);
                    break;
                }
            }
        }
        return enzyme;
    }

}
