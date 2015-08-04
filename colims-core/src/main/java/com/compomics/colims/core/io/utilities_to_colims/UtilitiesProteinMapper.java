package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.*;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import eu.isas.peptideshaker.myparameters.PSParameter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class maps a Compomics Utilities protein objects to Colims Protein instances.
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesProteinMapper")
public class UtilitiesProteinMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesProteinMapper.class);
    /**
     * The protein service instance.
     */
    @Autowired
    private ProteinService proteinService;
    /**
     * The map of cached proteins (key: sequence, value: the protein).
     */
    protected final Map<String, Protein> cachedProteins = new HashMap<>();

    public Map<String, Protein> getCachedProteins() {
        return cachedProteins;
    }

    /**
     * Map the Utilities protein related objects to Colims proteins and add them to the peptide.
     *
     * @param proteinMatches    the map of utilities protein matches (key: ProteinMatch instance, value: PSParameter instance)
     * @param peptideMatchScore the PSM score
     * @param targetPeptide     the Colims peptide
     * @throws MappingException thrown in case of a mapping related problem
     */
    public void map(final Map<ProteinMatch, PSParameter> proteinMatches, final MatchScore peptideMatchScore, final Peptide targetPeptide) throws MappingException {
        try {
            //iterate over protein matches
            for (ProteinMatch proteinMatch : proteinMatches.keySet()) {
                //iterate over all possible matches
                if (proteinMatch != null) {
                    /**
                     * Iterate over the theoretic protein accessions.
                     * If there's more than one, it's a protein group and the main matched protein is the main group protein.
                     * Note that the peptide scores will be the same for all protein group members.
                     */
                    PeptideHasProteinGroup peptideHasProteinGroup = new PeptideHasProteinGroup();
                    //set the peptide scores
                    peptideHasProteinGroup.setPeptideProbability(peptideMatchScore.getProbability());
                    peptideHasProteinGroup.setPeptidePostErrorProbability(peptideMatchScore.getPostErrorProbability());

                    ProteinGroup proteinGroup = new ProteinGroup();
                    for (String proteinAccession : proteinMatch.getTheoreticProteinsAccessions()) {
                        ProteinGroupHasProtein proteinGroupHasProtein = new ProteinGroupHasProtein();

                        //get the utilities Protein from SequenceFactory
                        com.compomics.util.experiment.biology.Protein sourceProtein = SequenceFactory.getInstance().getProtein(proteinAccession);
                        //set protein
                        Protein matchedProtein = getProtein(sourceProtein);
                        proteinGroupHasProtein.setProtein(matchedProtein);

                        if (proteinAccession.equals(proteinMatch.getMainMatch())) {
                            //set the is main protein group flag to true
                            proteinGroupHasProtein.setIsMainGroupProtein(true);
                        }

                        //set protein accession
                        proteinGroupHasProtein.setProteinAccession(proteinAccession);

                        //set entity relations
                        proteinGroupHasProtein.setProteinGroup(proteinGroup);
                        proteinGroup.getProteinGroupHasProteins().add(proteinGroupHasProtein);
                    }
                    //set entity relations
                    proteinGroup.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
                    //set peptide
                    peptideHasProteinGroup.setPeptide(targetPeptide);
                    //set protein group
                    peptideHasProteinGroup.setProteinGroup(proteinGroup);
                    targetPeptide.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
                }
            }
        } catch (IOException | IllegalArgumentException | InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }
    }

    /**
     * Clear resources after usage.
     */
    public void clear() {
        cachedProteins.clear();
    }

    /**
     * Get the Colims protein by protein accession or sequence digest. This method looks for the protein in the newly
     * added proteins and the database. If it was not found, it looks in the utilities SequenceFactory and it's added to
     * newly added proteins.
     *
     * @param sourceProtein the utilities protein
     * @return the found Protein instance
     */
    private Protein getProtein(final com.compomics.util.experiment.biology.Protein sourceProtein) {
        Protein targetProtein;

        //first, look in the newly added proteins map
        //@todo configure hibernate cache and check performance
        targetProtein = cachedProteins.get(sourceProtein.getSequence());
        if (targetProtein == null) {
            //check if the protein is found in the db
            targetProtein = proteinService.findBySequence(sourceProtein.getSequence());

            if (targetProtein == null) {
                //map the utilities protein onto the Colims protein
                targetProtein = new Protein(sourceProtein.getSequence());
                ProteinAccession proteinAccession = new ProteinAccession(sourceProtein.getAccession());

                //set entity relations
                proteinAccession.setProtein(targetProtein);
                targetProtein.getProteinAccessions().add(proteinAccession);

                //add to cached proteins map
                cachedProteins.put(targetProtein.getSequence(), targetProtein);
            } else {
                updateAccessions(targetProtein, sourceProtein.getAccession());

                //add to cached proteins
                cachedProteins.put(targetProtein.getSequence(), targetProtein);
            }
        } else {
            updateAccessions(targetProtein, sourceProtein.getAccession());
        }

        return targetProtein;
    }

    /**
     * Update the ProteinAccessions linked to a Protein.
     *
     * @param protein   the Protein instance
     * @param accession the protein accession
     */
    private void updateAccessions(final Protein protein, final String accession) {
        //check if the protein accession is already linked to the protein
        boolean proteinAccessionPresent = false;

        for (ProteinAccession proteinAccession : protein.getProteinAccessions()) {
            if (proteinAccession.getAccession().equals(accession)) {
                proteinAccessionPresent = true;
                break;
            }
        }

        if (!proteinAccessionPresent) {
            ProteinAccession proteinAccession = new ProteinAccession(accession);
            protein.getProteinAccessions().add(proteinAccession);

            //set entity relations
            proteinAccession.setProtein(protein);
            protein.getProteinAccessions().add(proteinAccession);
        }
    }
}
