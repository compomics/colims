package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.colims.model.factory.CvParamFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.xml_soap.Map;
import org.apache.xml.xml_soap.MapItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ontology_lookup.ontologyquery.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("olsService")
public class OlsServiceImpl implements OlsService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(OlsServiceImpl.class);
    private static final String MOD_ONTOLOGY_LABEL = "MOD";
    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";

    /**
     * Modifications cache to prevent unnecessary webservice lookups. This map can contains instances of {@link
     * com.compomics.colims.model.Modification} and {@link com.compomics.colims.model.SearchModification}.
     */
    private final java.util.Map<String, AbstractModification> modificationsCache = new HashMap<>();
    /**
     * This interface provides access to the ontology lookup service.
     */
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

        //look for the modification in the cache
        if (modificationsCache.containsKey(accession)) {
            AbstractModification foundModification = modificationsCache.get(accession);
            if (clazz.isInstance(foundModification)) {
                modification = (T) foundModification;
            } else {
                modification = copyModification(clazz, foundModification);
            }
        } else {
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

                    //add modification to the cache
                    modificationsCache.put(accession, modification);
                }
            }
        }

        return modification;
    }

    @Override
    public <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession) {
        T modification = null;

        //look for the modification in the cache
        if (modificationsCache.containsKey(unimodAccession)) {
            AbstractModification foundModification = modificationsCache.get(unimodAccession);
            if (clazz.isInstance(foundModification)) {
                modification = (T) foundModification;
            } else {
                modification = copyModification(clazz, foundModification);
            }
        } else {
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

                        //add modification to the cache
                        modificationsCache.put(unimodAccession, modification);
                    }
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

    @Override
    public java.util.Map<String, AbstractModification> getModificationsCache() {
        return modificationsCache;
    }

    /**
     * Copy (the instance fields of) the modification from one subclass of AbstractModification to another.
     *
     * @param clazz     the subclass of AbstractModification
     * @param modToCopy the modification to copy
     * @param <T>       the AbstractModification subclass
     * @return the copied modification
     */
    private <T extends AbstractModification> T copyModification(Class<T> clazz, AbstractModification modToCopy) {
        T modification = null;

        try {
            modification = clazz.newInstance();
            modification.setAccession(modToCopy.getAccession());
            modification.setName(modToCopy.getName());
            if (modToCopy.getMonoIsotopicMassShift() != null) {
                modification.setMonoIsotopicMassShift(modToCopy.getMonoIsotopicMassShift());
            }
            if (modToCopy.getAverageMassShift() != null) {
                modification.setAverageMassShift(modToCopy.getAverageMassShift());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return modification;
    }

}
