package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.distributed.io.unimod.UnimodMarshaller;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.parameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps the Compomics Utilities modification related classes to
 * Colims modification related classes.
 *
 * @author Niels Hulstaert
 */
@SuppressWarnings("ConstantConditions")
@Component("utilitiesModificationMapper")
public class UtilitiesModificationMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesModificationMapper.class);

    /**
     * The modification service instance.
     */
    @Autowired
    private ModificationService modificationService;
    /**
     * The Ontology Lookup Service service.
     */
    @Autowired
    private OlsService olsService;
    /**
     * Contains the UNIMOD modifications.
     */
    @Autowired
    private UnimodMarshaller unimodMarshaller;
    /**
     * The map of cached modifications (key: modification name, value: the
     * modification).
     */
    private final Map<String, Modification> cachedModifications = new HashMap<>();

    /**
     * Map the utilities modification matches to the Colims peptide. The
     * Utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param modificationMatches the list of modification matches
     * @param ptmScores the PeptideShaker PTM scores
     * @param targetPeptide the Colims target peptide
     * @throws ModificationMappingException thrown in case of a modification
     * mapping problem
     */
    public void map(final ArrayList<ModificationMatch> modificationMatches, final PSPtmScores ptmScores, final Peptide targetPeptide) throws ModificationMappingException {
        //iterate over modification matches
        for (ModificationMatch modificationMatch : modificationMatches) {
            //get the CvTerm from the PTMFactory
            CvTerm cvTerm = PTMFactory.getInstance().getPTM(modificationMatch.getTheoreticPtm()).getCvTerm();

            Modification modification;
            if (cvTerm != null) {
                modification = mapByCvTerm(cvTerm);
            } else {
                modification = mapByName(modificationMatch.getTheoreticPtm());
            }

            //set entity associations if modification could be mapped
            if (modification != null) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();

                //set the Utilities name if necessary
                if (modification.getUtilitiesName() == null && PTMFactory.getInstance().containsPTM(modificationMatch.getTheoreticPtm())) {
                    modification.setUtilitiesName(modificationMatch.getTheoreticPtm());
                }

                //set location in the PeptideHasModification join entity
                int modificationIndex = modificationMatch.getModificationSite();

                //check for N-terminal modification
                if (modificationIndex == 1) {
                    PTM ptm = PTMFactory.getInstance().getPTM(modificationMatch.getTheoreticPtm());
                    if (!ptm.equals(PTMFactory.unknownPTM)) {
                        if (ptm.isNTerm()) {
                            modificationIndex = 0;
                        }
                    }
                }
                //check for C-terminal modification
                if (modificationIndex == targetPeptide.getLength()) {
                    PTM ptm = PTMFactory.getInstance().getPTM(modificationMatch.getTheoreticPtm());
                    if (!ptm.equals(PTMFactory.unknownPTM)) {
                        if (ptm.isCTerm()) {
                            modificationIndex = targetPeptide.getLength() + 1;
                        }
                    }
                }

                peptideHasModification.setLocation(modificationIndex);

                //set modification type
                if (modificationMatch.isVariable()) {
                    peptideHasModification.setModificationType(ModificationType.VARIABLE);

                    if (ptmScores != null) {
                        PtmScoring ptmScoring = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm());
                        if (ptmScoring != null) {
                            if (ptmScoring.getDSites().contains(modificationIndex)) {
                                peptideHasModification.setProbabilityScore(ptmScoring.getProbabilisticScore(modificationIndex));
                            }
                            if (ptmScoring.getProbabilisticSites().contains(modificationIndex)) {
                                peptideHasModification.setDeltaScore(ptmScoring.getDeltaScore(modificationIndex));
                            }
                        }
                    }
                } else {
                    peptideHasModification.setModificationType(ModificationType.FIXED);
                }

                //set entity associations
                peptideHasModification.setModification(modification);
                peptideHasModification.setPeptide(targetPeptide);

                targetPeptide.getPeptideHasModifications().add(peptideHasModification);
            }
        }
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
     * Map the given Utilities CvTerm instance to a Colims Modification
     * instance. Return null if no mapping was possible.
     *
     * @param cvTerm the Utilities CvTerm instance
     * @return the Colims Modification entity
     */
    private Modification mapByCvTerm(final CvTerm cvTerm) throws ModificationMappingException {
        Modification modification;

        //look for the modification in the cached modifications map
        modification = cachedModifications.get(cvTerm.getAccession());

        if (modification == null) {
            //the modification was not found in the cachedModifications map
            //look for the modification in the database by accession
            modification = modificationService.findByAccession(cvTerm.getAccession());

            if (modification == null) {
                //the modification was not found in the cached modifications or the database
                switch (cvTerm.getOntology()) {
                    case "UNIMOD":
                        //look for the modification in the UNIMOD modifications
                        modification = unimodMarshaller.getModificationByName(Modification.class, cvTerm.getName());
                        if (modification == null) {
                            //look for the modification in the PSI-MOD ontology by name and UNIMOD accession
                            modification = olsService.findModificationByNameAndUnimodAccession(Modification.class, cvTerm.getName(), cvTerm.getAccession());
                        }
                        if (modification != null) {
                            //add to cached modifications with the UNIMOD accession as key
                            cachedModifications.put(cvTerm.getAccession(), modification);
                        }
                        break;
                    case "PSI-MOD":
                        //look for the modification in the PSI-MOD ontology by accession
                        modification = olsService.findModificationByAccession(Modification.class, cvTerm.getAccession());
                        if (modification != null) {
                            //add to cached modifications with the PSI-MOD accession as key
                            cachedModifications.put(modification.getAccession(), modification);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Should not be able to get here.");
                }

                if (modification == null) {
                    modification = new Modification(cvTerm.getAccession(), cvTerm.getName());

                    //@todo check if the PTM mass is the average or the monoisotopic mass shift
                    modification.setMonoIsotopicMassShift(Double.valueOf(cvTerm.getValue()));

                    //add to cached modifications
                    cachedModifications.put(modification.getAccession(), modification);
                }
            } else {
                //add the modification to the cached modifications
                cachedModifications.put(cvTerm.getAccession(), modification);
            }
        }

        if (modification == null) {
            LOGGER.error("The Utilities CvTerm " + cvTerm.getAccession() + " could not be mapped.");
            throw new ModificationMappingException("The Utilities CvTerm " + cvTerm.getAccession() + " could not be mapped.");
        }

        return modification;
    }

    /**
     * Map the given modification name to a Modification instance. Return null
     * if no mapping was possible.
     *
     * @param modificationName the modification name
     * @return the Colims Modification instance
     * @throws com.compomics.colims.core.io.ModificationMappingException in case
     * of a modification mapping problem
     */
    public Modification mapByName(final String modificationName) throws ModificationMappingException {
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
                    modification = olsService.findModificationByExactName(Modification.class, modificationName);

                    if (modification == null) {
                        //the modification was not found in the PSI-MOD ontology
                        //look for a matching PTM in the PTMFactory
                        PTM ptm = PTMFactory.getInstance().getPTM(modificationName);

                        modification = new Modification(modificationName);
                        if (ptm.equals(PTMFactory.unknownPTM)) {
                            LOGGER.warn("The modification match " + modificationName + " could not be found in the PTMFactory.");
                        } else {
                            modification.setMonoIsotopicMassShift(ptm.getMass());
                            modification.setAverageMassShift(ptm.getMass());
                        }
                    }

                }
            }
            //add to cached modifications
            cachedModifications.put(modification.getName(), modification);
        }

        if (modification == null) {
            LOGGER.error("The modification name " + modificationName + " could not be mapped.");
            throw new ModificationMappingException("The modification name " + modificationName + " could not be mapped.");
        }

        return modification;
    }

}
