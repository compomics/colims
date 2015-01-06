package com.compomics.colims.client.factory;

import com.compomics.colims.core.logic.IonLadderMassesCalculator;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.UnknownAAException;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class generates a SpectrumPanel for a PSM.
 *
 * @author Niels Hulstaert
 */
@Component("spectrumPanelGenerator")
public class SpectrumPanelGenerator {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SpectrumPanelGenerator.class);

    /**
     * The ion ladder masses calculator.
     */
    @Autowired
    private IonLadderMassesCalculator ionLadderCalculator;

    /**
     * Return a SpectrumPanel for a given PSM.
     *
     * @param mzRatio the peptide m/z value
     * @param charge the peptide charge
     * @param spectrumPeaks the peak map
     * @param peptide the Peptide
     * @return the SpectrumPanel instance
     */
    public SpectrumPanel getSpectrumPanel(final double mzRatio, final int charge, final Map<Double, Double> spectrumPeaks, final Peptide peptide) {
        //initialize new SpectrumPanel
        SpectrumPanel spectrumPanel = new SpectrumPanel(
                Doubles.toArray(spectrumPeaks.keySet()),
                Doubles.toArray(spectrumPeaks.values()),
                mzRatio,
                Integer.toString(charge), "", 40, false, false, false);

        //remove the border
        spectrumPanel.setBorder(null);

        spectrumPanel.showAnnotatedPeaksOnly(true);

        //if the spectrum has been matched with a peptide, add peak annotations
        if (peptide != null) {
            spectrumPanel.setAnnotations(getPeakAnnotations(peptide));
        }

        return spectrumPanel;
    }

    /**
     * Constructs the vector with the peak annotations for the spectrum panel.
     *
     * @param peptide the peptide
     * @return the vector of peak annotations
     */
    private List<SpectrumAnnotation> getPeakAnnotations(final Peptide peptide) {
        List<SpectrumAnnotation> peakAnnotations = new ArrayList<>();
        try {

            //calculate B ion ladder, charge state +1 and +2
            double[] b1IonLadder = ionLadderCalculator.calculateBIonLadderMasses(peptide, 1);
            addPeakAnnotations(peakAnnotations, "b", b1IonLadder, 1);

            double[] b2IonLadder = ionLadderCalculator.calculateBIonLadderMasses(peptide, 2);
            addPeakAnnotations(peakAnnotations, "b", b2IonLadder, 2);

            //calculate Y ion ladder, charge state +1 and +2
            double[] y1IonLadder = ionLadderCalculator.calculateYIonLadderMasses(peptide, 1);
            addPeakAnnotations(peakAnnotations, "y", y1IonLadder, 1);

            double[] y2IonLadder = ionLadderCalculator.calculateYIonLadderMasses(peptide, 2);
            addPeakAnnotations(peakAnnotations, "y", y2IonLadder, 2);

        } catch (UnknownAAException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return peakAnnotations;
    }

    private void addPeakAnnotations(final List<SpectrumAnnotation> peakAnnotations, final String ionType, final double[] ionLadderMasses, final int charge) {
        for (int i = 0; i < ionLadderMasses.length; i++) {
            String label = ionType;
            label += (i + 1);

            if (charge > 1) {
                label += getIonChargeString(charge);
            }

            DefaultSpectrumAnnotation defaultSpectrumAnnotation = new DefaultSpectrumAnnotation(
                    ionLadderMasses[i], 0.1, SpectrumPanel.determineColorOfPeak(label), label);
            peakAnnotations.add(defaultSpectrumAnnotation);
        }
    }

    private String getIonChargeString(final int ionCharge) {
        StringBuilder ionChargeStringBuilder = new StringBuilder();
        for (int i = 0; i < ionCharge; i++) {
            ionChargeStringBuilder.append("+");
        }

        return ionChargeStringBuilder.toString();
    }
}
