package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * maps a peptide assumption fetched from a parsed max quant file on a colims
 * peptide object
 *
 * @author Davy
 */
@Component("MaxQuantUtilitiesPeptideMapper")
public class MaxQuantUtilitiesPeptideMapper implements Mapper<PeptideAssumption, Peptide> {

    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;

    /**
     * maps the source max quant peptide assumption to the target colims peptide
     * disclaimer: needs to be from the parser because we retrieve an
     * {@code UrParameter} since there is momentarily no way to add a matchscore
     * to the peptide assumption another disclaimer: since it is at the moment
     * also impossible to add scores to a modification match we retrieve another
     * {@code UrParameter}
     *
     * @param source the peptide assumption from the max quant parser
     * @param target the colims peptide that needs to be filled up
     * @throws MappingException should any mapping problems arise
     */
    @Override
    public void map(final PeptideAssumption source, final Peptide target) throws MappingException {
        final MatchScore matchScore = (MatchScore) source.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY));
        PSPtmScores ptmScores = new PSPtmScores();
        for (ModificationMatch match : source.getPeptide().getModificationMatches()) {
            final MaxQuantPtmScoring aPtmScore = (MaxQuantPtmScoring) match.getUrParam(new MaxQuantPtmScoring());
            PtmScoring ptmScoring = new PtmScoring(match.getTheoreticPtm());
            ArrayList<Integer> locations = new ArrayList();
            locations.add(match.getModificationSite());
//            @todo ask Davy
//            ptmScoring.addAScore(locations, aPtmScore.getScore());
//            ptmScoring.addDeltaScore(locations, aPtmScore.getDeltaScore());
            ptmScores.addMainModificationSite(match.getTheoreticPtm(), match.getModificationSite());
        }
        utilitiesPeptideMapper.map(source.getPeptide(), matchScore, ptmScores, target);
    }
}
