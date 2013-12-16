package com.compomics.colims.core.mapper.impl.MaxQuantToColims;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesPeptideMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("MaxQuantUtilitiesPeptideMapper")
public class MaxQuantUtilitiesPeptideMapper implements Mapper<PeptideAssumption, Peptide> {

    @Autowired
    UtilitiesPeptideMapper utilitiesPeptideMapper;

    @Override
    public void map(PeptideAssumption source, Peptide target) throws MappingException {
        final MatchScore matchScore = (MatchScore) source.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY));
        PSPtmScores ptmScores = new PSPtmScores();
        for (ModificationMatch match : source.getPeptide().getModificationMatches()) {

            PtmScoring ptmScoring = new PtmScoring(match.getTheoreticPtm());
            ArrayList<Integer> locations = new ArrayList();
            locations.add(match.getModificationSite());
            double oxidationScore = 100.0;
            ptmScoring.addAScore(locations, oxidationScore);
            ptmScoring.addDeltaScore(locations, oxidationScore);
            ptmScores.addMainModificationSite(match.getTheoreticPtm(), match.getModificationSite());
        }
        utilitiesPeptideMapper.map(source.getPeptide(), matchScore, ptmScores, target);
    }
}
