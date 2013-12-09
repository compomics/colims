package com.compomics.colims.client.factory;

import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.core.logic.IonLadderMassesCalculator;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.UnknownAAException;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.google.common.primitives.Doubles;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("spectrumPanelGenerator")
public class SpectrumPanelGenerator {

    private static final Logger LOGGER = Logger.getLogger(SpectrumPanelGenerator.class);
    @Autowired
    private IonLadderMassesCalculator ionLadderCalculator;

    public SpectrumPanel getSpectrumPanel(double mzRatio, int charge, Map<Double, Double> spectrumPeaks, Peptide peptide) {
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
    private Vector<DefaultSpectrumAnnotation> getPeakAnnotations(Peptide peptide) {
        Vector<DefaultSpectrumAnnotation> peakAnnotations = new Vector();
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

    private void addPeakAnnotations(Vector<DefaultSpectrumAnnotation> peakAnnotations, String ionType, double[] ionLadderMasses, int charge) {
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

    private String getIonChargeString(int ionCharge) {
        StringBuilder ionChargeStringBuilder = new StringBuilder();
        for (int i = 0; i < ionCharge; i++) {
            ionChargeStringBuilder.append("+");
        }

        return ionChargeStringBuilder.toString();
    }
}
