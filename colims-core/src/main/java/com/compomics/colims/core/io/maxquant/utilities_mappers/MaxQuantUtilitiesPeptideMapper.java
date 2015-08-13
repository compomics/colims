package com.compomics.colims.core.io.maxquant.utilities_mappers;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.urparams.MaxQuantPtmScoring;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.springframework.stereotype.Component;

/**
 * maps a peptide assumption fetched from a parsed max quant file on a colims peptide object
 *
 * @author Davy
 */
@Component("maxQuantUtilitiesPeptideMapper")
public class MaxQuantUtilitiesPeptideMapper implements Mapper<PeptideAssumption, Peptide> {

    /**
     * maps the source max quant peptide assumption to the target Colims peptide disclaimer: needs to be from the parser
     * because we retrieve an {@code UrParameter} since there is momentarily no way to add a matchscore to the peptide
     * assumption another disclaimer: since it is at the moment also impossible to add scores to a modification match we
     * retrieve another {@code UrParameter}
     *
     * @param source the peptide assumption from the max quant parser
     * @param target the Colims peptide that needs to be filled up
     * @throws MappingException should any mapping problems arise
     */
    @Override
    public void map(final PeptideAssumption source, final Peptide target) throws MappingException {
        final MatchScore matchScore = (MatchScore) source.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY));
        PSPtmScores ptmScores = new PSPtmScores();
        for (ModificationMatch match : source.getPeptide().getModificationMatches()) {
            final MaxQuantPtmScoring aPtmScore = (MaxQuantPtmScoring) match.getUrParam(new MaxQuantPtmScoring());
            PtmScoring ptmScoring = new PtmScoring(match.getTheoreticPtm());
            ptmScoring.setProbabilisticScore(match.getModificationSite(), aPtmScore.getScore());
            ptmScoring.setDeltaScore(match.getModificationSite(), aPtmScore.getDeltaScore());
            ptmScores.addConfidentModificationSite(match.getTheoreticPtm(), match.getModificationSite());
            ptmScores.addPtmScoring(match.getTheoreticPtm(), ptmScoring);
        }
//        utilitiesPeptideMapper.map(source.getPeptide(), matchScore, ptmScores, source.getIdentificationCharge().value, target);
    }

    /**
     * Clear resources.
     */
    public void clear() {
//        utilitiesPeptideMapper.clear();
    }
}
