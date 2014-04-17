package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantProteinMapperStub")
public class MaxQuantProteinMapperStub extends UtilitiesProteinMapper {

    @Override
    public void map(List<ProteinMatch> proteinMatches, MatchScore peptideMatchScore, Peptide targetPeptide) throws MappingException {
        try {
            List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
            //iterate over protein matches
            for (ProteinMatch proteinMatch : proteinMatches) {
                //iterate over all possible matches                
                if (proteinMatch != null) {
                    //get main match
                    Protein mainMatchedProtein = getProtein(proteinMatch.getMainMatch());
                    //iterate over theoretic protein accessions if there is more than one.
                    //This means there is a protein group and the main matched protein is the main group protein.
                    //Note that the peptide scores will be the same for all group members
                    if (proteinMatch.getTheoreticProteinsAccessions().size() > 1) {
                        for (String proteinAccession : proteinMatch.getTheoreticProteinsAccessions()) {
                            Protein matchedProtein = getProtein(proteinAccession);
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
            throw new MappingException(ex);
        }
    }

    @Override
    public Protein getProtein(String proteinAccession) throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException {
        Protein protein = new Protein();
        if (!getCachedProteins().containsKey(proteinAccession)) {
            protein.setAccession(proteinAccession);

        } else {
            protein = getCachedProteins().get(proteinAccession);
        }
        return protein;
    }
}
