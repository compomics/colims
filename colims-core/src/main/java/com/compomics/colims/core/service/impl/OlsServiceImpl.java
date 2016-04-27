package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.model.ols.Ontology;
import com.compomics.colims.core.model.ols.OlsSearchResult;
import com.compomics.colims.core.model.ols.SearchResultMetadata;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.AbstractModification;
import com.compomics.colims.model.cv.TypedCvParam;
import org.apache.log4j.Logger;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Implementation of the OlsService interface.
 *
 * @author Niels Hulstaert
 * @deprecated this implementation is based on the soon to be deprecated version
 * of the Ontology Lookup Service (OLS).
 */
//@Service("olsService")
@Deprecated
public class OlsServiceImpl implements OlsService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(OlsServiceImpl.class);
    private static final String MOD_ONTOLOGY_LABEL = "MOD";
    private static final String MS_ONTOLOGY_LABEL = "MS";
    private static final String MS_ONTOLOGY = "PSI Mass Spectrometry Ontology [MS]";

    /**
     * Modifications cache to prevent unnecessary webservice lookups. This map
     * can contains instances of {@link
     * com.compomics.colims.model.Modification} and
     * {@link com.compomics.colims.model.SearchModification}.
     */
    private final java.util.Map<String, AbstractModification> modificationsCache = new HashMap<>();

    @Override
    public <T extends AbstractModification> T findModificationByExactName(Class<T> clazz, final String name) {
//        T modification = null;
//
//        //find the modification by exact name
//        Map modificationTerms = olsClient.getTermsByExactName(name, MOD_ONTOLOGY_LABEL);
//        if (modificationTerms.getItem() != null) {
//            //get the modification accession
//            for (org.apache.xml.xml_soap.MapItem mapItem : modificationTerms.getItem()) {
//                modification = findModificationByAccession(clazz, mapItem.getKey().toString());
//            }
//        }
//
//        return modification;
        return null;
    }

    @Override
    public <T extends AbstractModification> T findModificationByAccession(Class<T> clazz, String accession) {
//        T modification = null;
//
//        //look for the modification in the cache
//        if (modificationsCache.containsKey(accession)) {
//            AbstractModification foundModification = modificationsCache.get(accession);
//            if (clazz.isInstance(foundModification)) {
//                modification = (T) foundModification;
//            } else {
//                modification = copyModification(clazz, foundModification);
//            }
//        } else {
//            //get the modification name
//            String searchModificationName = olsClient.getTermById(accession, MOD_ONTOLOGY_LABEL);
//
//            //check if a term was found
//            if (!accession.equals(searchModificationName)) {
//                //get the term metadata by accession
//                Map modificationMetaData = olsClient.getTermMetadata(accession, MOD_ONTOLOGY_LABEL);
//                if (modificationMetaData.getItem() != null) {
//                    try {
//                        modification = clazz.newInstance();
//                        modification.setAccession(accession);
//                        modification.setName(searchModificationName);
//
//                        //get the modification properties
//                        for (MapItem mapItem : modificationMetaData.getItem()) {
//                            if (mapItem.getKey() != null && mapItem.getValue() != null) {
//                                if (mapItem.getKey().equals("DiffMono")) {
//                                    try {
//                                        Double monoIsotopicsMassShift = Double.parseDouble(mapItem.getValue().toString());
//                                        modification.setMonoIsotopicMassShift(monoIsotopicsMassShift);
//                                    } catch (NumberFormatException nfe) {
//                                        LOGGER.error(nfe, nfe.getCause());
//                                    }
//                                } else if (mapItem.getKey().equals("DiffAvg")) {
//                                    try {
//                                        Double averageMassShift = Double.parseDouble(mapItem.getValue().toString());
//                                        modification.setAverageMassShift(averageMassShift);
//                                    } catch (NumberFormatException nfe) {
//                                        LOGGER.error(nfe.getMessage(), nfe);
//                                    }
//                                }
//                            }
//                        }
//                    } catch (InstantiationException | IllegalAccessException e) {
//                        LOGGER.error(e.getMessage(), e);
//                    }
//
//                    //add modification to the cache
//                    modificationsCache.put(accession, modification);
//                }
//            }
//        }
//
//        return modification;
        return null;
    }

    @Override
    public <T extends AbstractModification> T findModificationByNameAndUnimodAccession(final Class<T> clazz, final String name, final String unimodAccession) {
//        T modification = null;
//
//        //look for the modification in the cache
//        if (modificationsCache.containsKey(unimodAccession)) {
//            AbstractModification foundModification = modificationsCache.get(unimodAccession);
//            if (clazz.isInstance(foundModification)) {
//                modification = (T) foundModification;
//            } else {
//                modification = copyModification(clazz, foundModification);
//            }
//        } else {
//            //first, find the modifications by name
//            Map modificationsTerms = olsClient.getTermsByName(name, MOD_ONTOLOGY_LABEL, false);
//            if (modificationsTerms.getItem() != null) {
//                String tempAccession = null;
//                //iterate over the modifications
//                outerloop:
//                for (MapItem mapItem : modificationsTerms.getItem()) {
//                    String accession = mapItem.getKey().toString();
//                    //get the Xrefs
//                    Map termXrefs = olsClient.getTermXrefs(accession, MOD_ONTOLOGY_LABEL);
//                    for (MapItem xref : termXrefs.getItem()) {
//                        if (StringUtils.containsIgnoreCase(xref.getValue().toString(), unimodAccession)) {
//                            if (xref.getValue().toString().equalsIgnoreCase(unimodAccession)) {
//                                T foundModification = findModificationByAccession(clazz, accession);
//                                if (foundModification != null) {
//                                    modification = foundModification;
//                                    break outerloop;
//                                }
//                            } else {
//                                //keep track of the next best thing
//                                tempAccession = accession;
//                            }
//                        }
//                    }
//                }
//                if (modification == null && tempAccession != null) {
//                    T foundModification = findModificationByAccession(clazz, tempAccession);
//                    if (foundModification != null) {
//                        modification = foundModification;
//
//                        //add modification to the cache
//                        modificationsCache.put(unimodAccession, modification);
//                    }
//                }
//            }
//        }
//
//        return modification;
        return null;
    }

    @Override
    public TypedCvParam findEnzymeByName(String name) {
//        TypedCvParam enzyme = null;
//
//        //find the enzyme by name
//        Map enzymeTerms = olsClient.getTermsByName(name, MS_ONTOLOGY_LABEL, false);
//        if (enzymeTerms.getItem() != null) {
//            for (MapItem mapItem : enzymeTerms.getItem()) {
//                String enzymeName = mapItem.getValue().toString();
//                if (enzymeName.equalsIgnoreCase(name)) {
//                    enzyme = CvParamFactory.newTypedCvInstance(CvParamType.SEARCH_PARAM_ENZYME, MS_ONTOLOGY, MS_ONTOLOGY_LABEL, mapItem.getKey().toString(), enzymeName);
//                    break;
//                }
//            }
//        }
//        return enzyme;

        return null;
    }

    @Override
    public java.util.Map<String, AbstractModification> getModificationsCache() {
        return modificationsCache;
    }

    /**
     * Copy (the instance fields of) the modification from one subclass of
     * AbstractModification to another.
     *
     * @param clazz the subclass of AbstractModification
     * @param modToCopy the modification to copy
     * @param <T> the AbstractModification subclass
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

    @Override
    public List<Ontology> getAllOntologies() throws HttpClientErrorException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Ontology> getOntologiesByNamespace(List<String> namespaces) throws HttpClientErrorException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SearchResultMetadata getPagedSearchMetadata(String query, List<String> ontologyNamespaces, EnumSet<OlsSearchResult.SearchField> searchFields) throws HttpClientErrorException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OlsSearchResult> doPagedSearch(String searchUrl, int page, int pageSize) throws HttpClientErrorException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTermDescriptionByOboId(String ontologyNamespace, String oboId) throws RestClientException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
