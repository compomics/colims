package com.compomics.colims.core.io.utilities_to_colims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesProteinMapper")
public class UtilitiesProteinMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesProteinMapper.class);
    @Autowired
    private ProteinService proteinService;
    /**
     * The map of chached proteins (key: sequence, value: the protein)
     */
    protected Map<String, Protein> cachedProteins = new HashMap<>();

    public Map<String, Protein> getCachedProteins() {
        return cachedProteins;
    }

    /**
     * Map the utilities protein related objects to colims proteins and add them
     * to the peptide.
     *
     * @param proteinMatches the utilities list of protein matches
     * @param peptideMatchScore
     * @param targetPeptide the colims peptide
     * @throws MappingException
     */
    public void map(List<ProteinMatch> proteinMatches, MatchScore peptideMatchScore, Peptide targetPeptide) throws MappingException {
        try {
            List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
            //iterate over protein matches
            for (ProteinMatch proteinMatch : proteinMatches) {
                //iterate over all possible matches                
                if (proteinMatch != null) {
                    //get utilities Protein from SequenceFactory
                    com.compomics.util.experiment.biology.Protein mainSourceProtein = SequenceFactory.getInstance().getProtein(proteinMatch.getMainMatch());
                    //get main match
                    Protein mainMatchedProtein = getProtein(mainSourceProtein);
                    //iterate over theoretic protein accessions if there is more than one.
                    //This means there is a protein group and the main matched protein is the main group protein.
                    //Note that the peptide scores will be the same for all group members
                    if (proteinMatch.getTheoreticProteinsAccessions().size() > 1) {
                        for (String proteinAccession : proteinMatch.getTheoreticProteinsAccessions()) {
                            //get utilities Protein from SequenceFactory
                            com.compomics.util.experiment.biology.Protein sourceProtein = SequenceFactory.getInstance().getProtein(proteinAccession);
                            Protein matchedProtein = getProtein(sourceProtein);
                            if (matchedProtein != null) {
                                PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
                                peptideHasProtein.setPeptideProbability(peptideMatchScore.getProbability());
                                peptideHasProtein.setPeptidePostErrorProbability(peptideMatchScore.getPostErrorProbability());
                                peptideHasProteins.add(peptideHasProtein);
                                //set entity relations
                                peptideHasProtein.setProtein(matchedProtein);
                                peptideHasProtein.setPeptide(targetPeptide);
                                peptideHasProtein.setMainGroupProtein(mainMatchedProtein);
                            }
                        }
                    } else {
                        //only set the main matched protein as the protein and leave the main group protein empty
                        PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
                        peptideHasProtein.setPeptideProbability(peptideMatchScore.getProbability());
                        peptideHasProtein.setPeptidePostErrorProbability(peptideMatchScore.getPostErrorProbability());
                        peptideHasProteins.add(peptideHasProtein);
                        //set entity relations
                        peptideHasProtein.setProtein(mainMatchedProtein);
                        peptideHasProtein.setPeptide(targetPeptide);
                    }
                }
            }
            targetPeptide.setPeptideHasProteins(peptideHasProteins);
        } catch (IOException | IllegalArgumentException | InterruptedException | ClassNotFoundException ex) {
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
     * Get the colims Protein by protein accession or sequence digest. This
     * method looks for the protein in the newly added proteins and the
     * database. If it was not found, look in the utilities SequenceFactory and
     * it to newly added proteins.
     *
     * @param sourceProtein the utilities protein
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    private Protein getProtein(com.compomics.util.experiment.biology.Protein sourceProtein) throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException {
        Protein targetProtein;

        //first, look in the newly added proteins map
        //@todo configure hibernate cache and check performance
        targetProtein = cachedProteins.get(sourceProtein.getSequence());
        if (targetProtein == null) {
            //check if the protein is found in the db
            targetProtein = proteinService.findBySequence(sourceProtein.getSequence());

            if (targetProtein == null) {
                //map the utilities protein onto the colims protein
                targetProtein = new Protein(sourceProtein.getSequence());
                ProteinAccession proteinAccession = new ProteinAccession(sourceProtein.getAccession());
                targetProtein.getProteinAccessions().add(proteinAccession);
                //add to cached proteins map
                cachedProteins.put(targetProtein.getSequence(), targetProtein);
            } else {
                //add to cached proteins
                cachedProteins.put(targetProtein.getSequence(), targetProtein);
            }
        }

        return targetProtein;
    }
}