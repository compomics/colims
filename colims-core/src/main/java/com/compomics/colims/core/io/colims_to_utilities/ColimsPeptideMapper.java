package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class maps the Colims Peptide instance onto a Utilities PeptideMatch instance.
 *
 * @author Niels Hulstaert
 */
@Component("colimspeptideMapper")
public class ColimsPeptideMapper {

    private static final String FRAGMENTS_DELIMITER = ";";
    private static final String NEUTRAL_LOSSES_PREFIX = "-";
    private static final String CHARGE_PREFIX = "(";
    private static final String CHARGE_SUFFIX = "+)";

    private final PeptideService peptideService;
    private final SpectrumService spectrumService;
    private final ColimsModificationMapper colimsModificationMapper;

    @Autowired
    public ColimsPeptideMapper(PeptideService peptideService, SpectrumService spectrumService, ColimsModificationMapper colimsModificationMapper) {
        this.peptideService = peptideService;
        this.spectrumService = spectrumService;
        this.colimsModificationMapper = colimsModificationMapper;
    }

    /**
     * Map the Colims {@link Peptide} instance on a Utilities {@link PeptideAssumption} instance.
     *
     * @param sourcePeptide the Colims peptide
     * @return the Utilities peptide assumption
     */
    public PeptideAssumption map(Peptide sourcePeptide) {
        //map peptide
        com.compomics.util.experiment.biology.Peptide targetPeptide = mapPeptide(sourcePeptide);

        Charge charge = new Charge(1, sourcePeptide.getCharge());
        return new PeptideAssumption(targetPeptide, 0, 0, charge, sourcePeptide.getPsmProbability());
    }

    /**
     * Map the matched fragment annotations. Returns an empty list of no annotations were stored in the database.
     *
     * @param peptide the Colims peptide
     * @return the list of {@link IonMatch} instances
     */
    public ArrayList<IonMatch> mapFragmentAnnotations(Peptide peptide) {
        ArrayList<IonMatch> ionMatches = new ArrayList<>();

        if (peptide.getFragmentIons() != null && peptide.getFragmentMasses() != null) {
            String[] fragmentIons = peptide.getFragmentIons().split(FRAGMENTS_DELIMITER);
            String[] fragmentMasses = peptide.getFragmentMasses().split(FRAGMENTS_DELIMITER);
            for (int i = 0; i < fragmentIons.length; i++) {
                String fragmentIon = fragmentIons[i];
                String fragmentMass = fragmentMasses[i];
                IonMatch ionMatch = mapFragmentIon(fragmentIon, fragmentMass, peptide);
            }
        }

        return ionMatches;
    }

    /**
     * Map the Colims Peptide instance onto the Utilities Peptide instance.
     *
     * @param sourcePeptide the Colims peptide instance
     * @return the Utilities Peptide instance
     */
    private com.compomics.util.experiment.biology.Peptide mapPeptide(Peptide sourcePeptide) {
        //fetch PeptideHasModifications
        peptideService.fetchPeptideHasModifications(sourcePeptide);

        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        if (!sourcePeptide.getPeptideHasModifications().isEmpty()) {
            colimsModificationMapper.map(sourcePeptide.getPeptideHasModifications(), modificationMatches);
        }

        return new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), modificationMatches);
    }

    /**
     * Map the fragment ion data onto an {@link IonMatch} instance.
     *
     * @param fragmentIonString the fragment ion String
     * @param fragmentMass      the fragment mass String
     * @param peptide           the {@link Peptide} instance
     * @return the {@link IonMatch} instance
     * @throws IOException in case of an spectrum file read related error
     */
    private IonMatch mapFragmentIon(String fragmentIonString, String fragmentMass, Peptide peptide) throws IOException {
        //look for the fragment mass peak in the spectrum
        Map<Double, Double> sortedSpectrumPeaks = spectrumService.getSortedSpectrumPeaks(peptide.getSpectrum().getSpectrumFiles().get(0));


        String fragmentIon = fragmentIonString;
        String neutralLoss = null;
        String charge = "1";
        int neutralLossesStartIndex = fragmentIonString.indexOf(NEUTRAL_LOSSES_PREFIX);
        int chargeStartIndex = fragmentIonString.indexOf(CHARGE_PREFIX);
        if (neutralLossesStartIndex > -1) {
            if (chargeStartIndex > -1) {
                neutralLoss = fragmentIonString.substring(neutralLossesStartIndex + 1, chargeStartIndex);
            } else {
                neutralLoss = fragmentIon.substring(neutralLossesStartIndex + 1, fragmentIonString.length());
            }
        }
        if (chargeStartIndex > -1) {
            charge = fragmentIonString.substring(chargeStartIndex + 1, fragmentIonString.length() - 2);
        }


        return null;
    }
}
