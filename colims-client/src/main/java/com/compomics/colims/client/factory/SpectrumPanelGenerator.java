package com.compomics.colims.client.factory;

import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.io.colims_to_utilities.PeptideMapper;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.SpectrumAnnotator;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.gui.spectrum.*;
import com.compomics.util.preferences.AnnotationPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.preferences.SpecificAnnotationPreferences;
import com.compomics.util.preferences.UtilitiesUserPreferences;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * This class adds a spectrum panel to a given JPanel.
 *
 * @author Niels Hulstaert
 */
@Component("spectrumPanelGenerator")
public class SpectrumPanelGenerator {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SpectrumPanelGenerator.class);

    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private PeptideService peptideService;
    @Autowired
    private ColimsSpectrumMapper colimsSpectrumMapper;
    @Autowired
    private PeptideMapper peptideMapper;

    private PeptideAssumption peptideAssumption;
    private ArrayList<IonMatch> annotations;
    private SearchParameters searchParameters = new SearchParameters();
    private AnnotationPreferences annotationPreferences = new AnnotationPreferences();
    private MSnSpectrum mSnSpectrum = new MSnSpectrum();
    private PTMFactory ptmFactory = PTMFactory.getInstance();

    /**
     * Initialise with the spectrum to display
     *
     * @param spectrum A spectrum
     */
    public void init(Spectrum spectrum) {
        List<com.compomics.colims.model.Peptide> peptides = peptideService.getPeptidesForSpectrum(spectrum);
        spectrumService.fetchSpectrumFiles(spectrum);

        if (!peptides.isEmpty()) {
            SpectrumMatch spectrumMatch = new SpectrumMatch();
            PeptideMatch peptideMatch = new PeptideMatch();

            peptideMapper.map(peptides.get(0), peptideMatch);
            PeptideAssumption assumption = new PeptideAssumption(peptideMatch.getTheoreticPeptide(),
                0, 0,
                new Charge(1, spectrum.getCharge() == null ? 0 : spectrum.getCharge()),
                peptides.get(0).getPsmProbability()
            );

            spectrumMatch.setBestPeptideAssumption(assumption);

            peptideAssumption = spectrumMatch.getBestPeptideAssumption();

            PeptideSpectrumAnnotator peptideSpectrumAnnotator = new PeptideSpectrumAnnotator();

            try {
                colimsSpectrumMapper.map(spectrum, mSnSpectrum);

                SpecificAnnotationPreferences specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(
                    mSnSpectrum.getSpectrumKey(),
                    peptideAssumption,
                    new SequenceMatchingPreferences(),
                    new SequenceMatchingPreferences()
                );

                annotations = peptideSpectrumAnnotator.getSpectrumAnnotation(
                    annotationPreferences,
                    specificAnnotationPreferences,
                    mSnSpectrum,
                    peptideAssumption.getPeptide()
                );
            } catch (Exception e) {
                LOGGER.error(e.getCause(), e);
            }
        }
    }

    /**
     * Add spectrum panel components
     *
     * @param spectrumJPanel Panel to use for spectrum panel
     */
    public void decorateSpectrumPanel(JPanel spectrumJPanel) {
        UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();

        Collection<Peak> peaks = mSnSpectrum.getPeakList();

        if (!peaks.isEmpty()) {
            SpectrumPanel spectrumPanel = new SpectrumPanel(
                mSnSpectrum.getMzValuesAsArray(),
                mSnSpectrum.getIntensityValuesAsArray(),
                mSnSpectrum.getPrecursor().getMz(),
                mSnSpectrum.getPrecursor().getPossibleChargesAsString(),
                "",
                40,
                false, false, false,
                2,
                false
            );

            spectrumPanel.setDeltaMassWindow(annotationPreferences.getFragmentIonAccuracy());
            spectrumPanel.setBorder(null);
            spectrumPanel.setDataPointAndLineColor(utilitiesUserPreferences.getSpectrumAnnotatedPeakColor(), 0);
            spectrumPanel.setPeakWaterMarkColor(utilitiesUserPreferences.getSpectrumBackgroundPeakColor());
            spectrumPanel.setPeakWidth(utilitiesUserPreferences.getSpectrumAnnotatedPeakWidth());
            spectrumPanel.setBackgroundPeakWidth(utilitiesUserPreferences.getSpectrumBackgroundPeakWidth());
            spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
            spectrumPanel.showAnnotatedPeaksOnly(!annotationPreferences.showAllPeaks());
            spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(annotationPreferences.yAxisZoomExcludesBackgroundPeaks());

            spectrumPanel.addAutomaticDeNovoSequencing(
                peptideAssumption.getPeptide(),
                annotations,
                searchParameters.getIonSearched1(),
                searchParameters.getIonSearched2(),
                annotationPreferences.getDeNovoCharge(),
                annotationPreferences.showForwardIonDeNovoTags(),
                annotationPreferences.showRewindIonDeNovoTags(),
                false
            );

            spectrumJPanel.removeAll();
            spectrumJPanel.add(spectrumPanel);
            spectrumJPanel.revalidate();
            spectrumJPanel.repaint();
        }
    }

    /**
     * Add secondary components
     *
     * @param secondarySpectrumPlotsJPanel Panel to use for secondary components
     */
    public void decorateSecondaryPanel(JPanel secondarySpectrumPlotsJPanel) {
        SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(
            getTaggedPeptideSequence(peptideAssumption.getPeptide(), false, false, false),
            annotations,
            true,
            searchParameters.getModificationProfile(),
            searchParameters.getIonSearched1(),
            searchParameters.getIonSearched2()
        );

        secondarySpectrumPlotsJPanel.removeAll();
        secondarySpectrumPlotsJPanel.add(sequenceFragmentationPanel);
        secondarySpectrumPlotsJPanel.add(new IntensityHistogram(annotations, mSnSpectrum, 0.75));

        MassErrorPlot massErrorPlot = new MassErrorPlot(annotations, mSnSpectrum, annotationPreferences.getFragmentIonAccuracy(), false);

        secondarySpectrumPlotsJPanel.add(massErrorPlot);

        secondarySpectrumPlotsJPanel.revalidate();
        secondarySpectrumPlotsJPanel.repaint();
    }

    /**
     * Returns the modified sequence as an tagged string with potential modification sites color coded or with PTM tags,
     * e.g, &lt;mox&gt;. /!\ This method will work only if the PTM found in the peptide are in the PTMFactory. /!\ This
     * method uses the modifications as set in the modification matches of this peptide and displays all of them.
     *
     * @param peptide                the peptide
     * @param useHtmlColorCoding     if true, color coded HTML is used, otherwise PTM tags, e.g, &lt;mox&gt;, are used
     * @param includeHtmlStartEndTag if true, start and end HTML tags are added
     * @param useShortName           if true the short names are used in the tags
     * @return the tagged sequence as a string
     */
    private String getTaggedPeptideSequence(com.compomics.util.experiment.biology.Peptide peptide, boolean useHtmlColorCoding, boolean includeHtmlStartEndTag, boolean useShortName) {
        HashMap<Integer, ArrayList<String>> confidentLocations = new HashMap<>();
        HashMap<Integer, ArrayList<String>> secondaryAmbiguousLocations = new HashMap<>();
        HashMap<Integer, ArrayList<String>> fixedModifications = new HashMap<>();

        for (ModificationMatch modMatch : peptide.getModificationMatches()) {
            String modName = modMatch.getTheoreticPtm();

            if (ptmFactory.getPTM(modMatch.getTheoreticPtm()).getType() == PTM.MODAA) { // exclude terminal ptms

                int modSite = modMatch.getModificationSite();

                if (modMatch.isVariable()) {
                    if (modMatch.isConfident()) {
                        if (!confidentLocations.containsKey(modSite)) {
                            confidentLocations.put(modSite, new ArrayList<>());
                        }

                        confidentLocations.get(modSite).add(modName);
                    } else {
                        if (!secondaryAmbiguousLocations.containsKey(modSite)) {
                            secondaryAmbiguousLocations.put(modSite, new ArrayList<>());
                        }

                        secondaryAmbiguousLocations.get(modSite).add(modName);
                    }
                } else {
                    if (!fixedModifications.containsKey(modSite)) {
                        fixedModifications.put(modSite, new ArrayList<>());
                    }

                    fixedModifications.get(modSite).add(modName);
                }
            }
        }

        return com.compomics.util.experiment.biology.Peptide.getTaggedModifiedSequence(
            searchParameters.getModificationProfile(),
            peptide,
            confidentLocations,
            new HashMap<>(),
            secondaryAmbiguousLocations,
            fixedModifications,
            useHtmlColorCoding,
            includeHtmlStartEndTag,
            useShortName
        );
    }
}
