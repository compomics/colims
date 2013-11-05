package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.mapper.Mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesProteinMapper")
public class UtilitiesProteinMapper implements Mapper<List<ProteinMatch>, Peptide> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesProteinMapper.class);
    @Autowired
    private ProteinService proteinService;
    /**
     * The map of new proteins (key: protein accession, value: the protein)
     */
    private Map<String, Protein> newProteins = new HashMap<>();

    @Override
    public void map(List<ProteinMatch> proteinMatches, Peptide targetPeptide) throws MappingException {
        try {
            List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
            //iterate over protein matches
            for (ProteinMatch proteinMatch : proteinMatches) {
                //get best/main match
                if (proteinMatch != null) {
                    //get utilities Protein from SequenceFactory
                    com.compomics.util.experiment.biology.Protein sourceProtein = SequenceFactory.getInstance().getProtein(proteinMatch.getMainMatch());
                    if (!sourceProtein.isDecoy()) {
                        PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
                        //check if the protein is found in the newProteins
                        //@todo configure hibernate cache and check performance
                        Protein targetProtein = newProteins.get(sourceProtein.getAccession());
                        if (targetProtein == null) {
                            //check if the protein is found in the db
                            targetProtein = proteinService.findByAccession(sourceProtein.getAccession());
                            if (targetProtein == null) {
                                targetProtein = new Protein(sourceProtein.getAccession(), sourceProtein.getSequence(), sourceProtein.getDatabaseType());
                                //add to newProteins map
                                newProteins.put(targetProtein.getAccession(), targetProtein);
                            }
                        }
                        peptideHasProteins.add(peptideHasProtein);
                        //set entity relations
                        peptideHasProtein.setProtein(targetProtein);
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
}
