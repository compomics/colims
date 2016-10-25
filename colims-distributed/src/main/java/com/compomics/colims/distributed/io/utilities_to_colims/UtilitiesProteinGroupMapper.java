package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import eu.isas.peptideshaker.parameters.PSParameter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class maps Compomics Utilities protein objects to Colims ProteinGroup
 * and related instances.
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesProteinGroupMapper")
public class UtilitiesProteinGroupMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesProteinGroupMapper.class);
    /**
     * The protein service instance.
     */
    private final ProteinService proteinService;

    @Autowired
    public UtilitiesProteinGroupMapper(ProteinService proteinService) {
        this.proteinService = proteinService;
    }

    /**
     * Map the Utilities protein related objects to Colims ProteinGroup and
     * related objects.
     *
     * @param proteinMatch the ProteinMatch instance
     * @param proteinGroupScore the PSParameter instance with the protein
     * (group) scores
     * @param proteinGroup the Colims ProteinGroup entity onto the ProteinMatch
     * instance will be mapped
     * @throws MappingException thrown in case of a mapping related problem
     */
    public void map(final ProteinMatch proteinMatch, final PSParameter proteinGroupScore, final ProteinGroup proteinGroup) throws MappingException {
        try {
            /**
             * Iterate over the theoretic protein accessions. If there's more
             * than one, it's a protein group and the main matched protein is
             * the main group protein. Note that the protein scores will be the
             * same for all protein group members.
             */
            //set scores
            proteinGroup.setProteinProbability(proteinGroupScore.getProteinProbabilityScore());
            proteinGroup.setProteinPostErrorProbability(proteinGroupScore.getProteinProbability());

            for (String proteinAccession : proteinMatch.getTheoreticProteinsAccessions()) {
                ProteinGroupHasProtein proteinGroupHasProtein = new ProteinGroupHasProtein();

                //get the utilities Protein from SequenceFactory
                com.compomics.util.experiment.biology.Protein sourceProtein = SequenceFactory.getInstance().getProtein(proteinAccession);
                //get protein
                Protein matchedProtein = proteinService.getProtein(sourceProtein.getSequence());

                if (proteinAccession.equals(proteinMatch.getMainMatch())) {
                    //set the is main protein group flag to true
                    proteinGroupHasProtein.setIsMainGroupProtein(true);
                }

                //set protein accession
                proteinGroupHasProtein.setProteinAccession(proteinAccession);

                //set entity associations
                proteinGroupHasProtein.setProteinGroup(proteinGroup);
                proteinGroupHasProtein.setProtein(matchedProtein);

                proteinGroup.getProteinGroupHasProteins().add(proteinGroupHasProtein);
            }
        } catch (IOException | IllegalArgumentException | InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }
    }

    /**
     * Clear resources.
     */
    public void clear() {
        proteinService.clear();
    }

}
