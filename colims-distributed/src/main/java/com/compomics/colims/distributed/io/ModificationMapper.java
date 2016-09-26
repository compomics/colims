package com.compomics.colims.distributed.io;

import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.distributed.io.unimod.UnimodMarshaller;
import com.compomics.colims.model.Modification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps modifications from input resources onto
 * Colims {@link Modification} related classes.
 *
 * @author Niels Hulstaert
 */
@SuppressWarnings("ConstantConditions")
@Component("modificationMapper")
public class ModificationMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ModificationMapper.class);

    private static final String UNIMOD_PREFIX = "UNIMOD";
    private static final String MOD_PREFIX = "MOD";
    private static final String UNKNOWN_UTILITIES_PTM = "unknown";

    /**
     * The modification service instance.
     */
    private final ModificationService modificationService;
    /**
     * The Ontology Lookup Service service.
     */
    private final OlsService olsService;
    /**
     * Contains the UNIMOD modifications.
     */
    private final UnimodMarshaller unimodMarshaller;
    /**
     * The map of cached modifications (key: modification name, value: the
     * modification).
     */
    private final Map<String, Modification> cachedModifications = new HashMap<>();

    @Autowired
    public ModificationMapper(ModificationService modificationService, OlsService olsService, UnimodMarshaller unimodMarshaller) {
        this.modificationService = modificationService;
        this.olsService = olsService;
        this.unimodMarshaller = unimodMarshaller;
    }

    /**
     * Clear resources after usage.
     */
    public void clear() {
        cachedModifications.clear();
        //clear the cached modifications of the OlsService as well
        olsService.getModificationsCache().clear();
    }

    /**
     * Map the given ontology term to a SearchModification instance.
     *
     * @param ontologyPrefix the ontology prefix
     * @param accession      the term accession
     * @param label          the term label
     * @param value          the term value
     * @param utilitiesName  the Compomics Utilities modification name
     * @return the Colims SearchModification entity
     */
    public Modification mapByOntologyTerm(final String ontologyPrefix, final String accession, final String label, final String value, String utilitiesName) {
        Modification modification;

        //look for the modification in the cached modifications map
        modification = cachedModifications.get(accession);

        if (modification == null) {
            //the modification was not found in the cachedModifications map
            //look for the modification in the database by accession
            modification = modificationService.findByAccession(accession);

            if (modification == null) {
                //the modification was not found in the cached modifications or the database
                switch (ontologyPrefix) {
                    case UNIMOD_PREFIX:
                        //look for the modification in the UNIMOD modifications
                        modification = unimodMarshaller.getModificationByAccession(Modification.class, accession);
                        break;
                    case MOD_PREFIX: {
                        try {
                            //look for the modification in the PSI-MOD ontology by accession
                            modification = olsService.findModificationByAccession(Modification.class, accession);
                        } catch (RestClientException | IOException ex) {
                            //log exception and continue
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                    break;
                    default:
                        throw new IllegalStateException("Should not be able to get here.");
                }

                if (modification == null) {
                    modification = new Modification(accession, label);

                    if (value != null) {
                        //@todo check if the PTM mass is the average or the monoisotopic mass shift
                        modification.setMonoIsotopicMassShift(Double.valueOf(value));
                    }
                }

                //if available, set the utilities name
                if (utilitiesName != null && !utilitiesName.isEmpty()) {
                    modification.setUtilitiesName(utilitiesName);
                }
            } else {
                //add the modification to the cached modifications
                cachedModifications.put(accession, modification);
            }
        }

        return modification;
    }

    /**
     * Map the given ontology term to a Modification instance.
     * Return null if no mapping was possible.
     *
     * @param ontologyPrefix the ontology prefix
     * @param accession      the term accession
     * @param label          the term label
     * @return the Colims SearchModification entity
     */
    public Modification mapByOntologyTerm(final String ontologyPrefix, final String accession, final String label) {
        return mapByOntologyTerm(ontologyPrefix, accession, label, null, null);
    }

    /**
     * Map the given modification name to a Modification instance. Return null
     * if no mapping was possible.
     *
     * @param modificationName the modification name
     * @return the Colims Modification instance
     */
    public Modification mapByName(final String modificationName) {
        Modification modification;

        //look for the modification in the cached modifications
        modification = cachedModifications.get(modificationName);

        if (modification == null) {
            //the modification was not found in the cached modifications map
            //look for the modification in the database by name
            modification = modificationService.findByName(modificationName);

            if (modification == null) {
                //the modification was not found by name in the database
                //try to find it in the UNIMOD modifications
                modification = unimodMarshaller.getModificationByName(Modification.class, modificationName);

                if (modification == null) {
                    //the modification was not found in the UNIMOD ontology
                    //look for the modification in the PSI-MOD ontology by exact name
                    try {
                        modification = olsService.findModificationByExactName(Modification.class, modificationName);
                    } catch (IOException e) {
                        //log exception and continue
                        LOGGER.error(e.getMessage(), e);
                    }

                    if (modification == null) {
                        //the search modification was not found in any ontology
                        //look for a matching PTM in the PTMFactory
                        PTM ptm = PTMFactory.getInstance().getPTM(modificationName);

                        modification = new Modification(modificationName);
                        if (ptm.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                            LOGGER.warn("The modification " + modificationName + " could not be mapped to any resource.");
                        } else {
                            if (ptm.getCvTerm() != null) {
                                //map by the CV term if it's not null
                                modification = mapByOntologyTerm(
                                        ptm.getCvTerm().getOntology(),
                                        ptm.getCvTerm().getAccession(),
                                        ptm.getCvTerm().getName(),
                                        ptm.getCvTerm().getValue(),
                                        ptm.getName());
                            } else {
                                modification.setUtilitiesName(ptm.getName());
                                modification.setMonoIsotopicMassShift(ptm.getMass());
                                modification.setAverageMassShift(ptm.getMass());
                            }
                        }
                    }
                }
            }
            //add to cached modifications
            cachedModifications.put(modification.getName(), modification);
        }

        return modification;
    }

}
