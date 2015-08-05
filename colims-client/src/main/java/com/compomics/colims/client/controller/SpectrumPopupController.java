package com.compomics.colims.client.controller;

import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SpectrumPopupDialog;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.io.colims_to_utilities.PsmMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.SpectrumAnnotator;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.gui.spectrum.IntensityHistogram;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.preferences.AnnotationPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.preferences.SpecificAnnotationPreferences;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Controller for spectrum panel pop-up window
 * <p/>
 * Created by Iain on 28/07/2015.
 */
@Component
public class SpectrumPopupController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(SpectrumPopupController.class);

    private SpectrumPopupDialog spectrumPopupDialog;
    private SearchParameters searchParameters = new SearchParameters();
    private PTMFactory ptmFactory = PTMFactory.getInstance();

    @Autowired
    MainController mainController;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    ColimsSpectrumMapper colimsSpectrumMapper;
    @Autowired
    PsmMapper psmMapper;

    @Override
    public void init() {
        spectrumPopupDialog = new SpectrumPopupDialog(mainController.getMainFrame(), true);
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), spectrumPopupDialog);
        spectrumPopupDialog.setVisible(true);
    }

    /**
     * Update the panel for the given spectrum then display it
     *
     * @param spectrum A spectrum to show
     */
    public void updateView(Spectrum spectrum) {
        AnnotationPreferences annotationPreferences = new AnnotationPreferences();
        UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();

        MSnSpectrum mSnSpectrum = new MSnSpectrum();

        spectrumService.fetchSpectrumFiles(spectrum);

        try {
            colimsSpectrumMapper.map(spectrum, mSnSpectrum);

            Collection<Peak> peaks = mSnSpectrum.getPeakList();

            if (peaks != null && !peaks.isEmpty()) {
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

                if (!spectrum.getPeptides().isEmpty()) {
                    SpectrumMatch spectrumMatch = new SpectrumMatch();
                    psmMapper.map(spectrum, spectrumMatch);

                    PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();

                    SpecificAnnotationPreferences specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(
                            mSnSpectrum.getSpectrumKey(),
                            peptideAssumption,
                            new SequenceMatchingPreferences(),
                            new SequenceMatchingPreferences()
                    );

                    PeptideSpectrumAnnotator peptideSpectrumAnnotator = new PeptideSpectrumAnnotator();

                    ArrayList<IonMatch> annotations = peptideSpectrumAnnotator.getSpectrumAnnotation(
                            annotationPreferences,
                            specificAnnotationPreferences,
                            mSnSpectrum,
                            peptideAssumption.getPeptide()
                    );

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

                    spectrumPopupDialog.getSpectrumJPanel().removeAll();
                    spectrumPopupDialog.getSpectrumJPanel().add(spectrumPanel);
                    spectrumPopupDialog.getSpectrumJPanel().revalidate();
                    spectrumPopupDialog.getSpectrumJPanel().repaint();

                    SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(
                            getTaggedPeptideSequence(peptideAssumption.getPeptide(), false, false, false),
                            annotations,
                            true,
                            searchParameters.getModificationProfile(),
                            searchParameters.getIonSearched1(),
                            searchParameters.getIonSearched2()
                    );

                    spectrumPopupDialog.getSecondarySpectrumPlotsJPanel().removeAll();
                    spectrumPopupDialog.getSecondarySpectrumPlotsJPanel().add(sequenceFragmentationPanel);
                    spectrumPopupDialog.getSecondarySpectrumPlotsJPanel().add(new IntensityHistogram(annotations, mSnSpectrum, 0.75));

                    MassErrorPlot massErrorPlot = new MassErrorPlot(annotations, mSnSpectrum, annotationPreferences.getFragmentIonAccuracy(), false);

                    spectrumPopupDialog.getSecondarySpectrumPlotsJPanel().add(massErrorPlot);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getCause(), e);
        }

        showView();
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
    private String getTaggedPeptideSequence(Peptide peptide, boolean useHtmlColorCoding, boolean includeHtmlStartEndTag, boolean useShortName) {
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

        return Peptide.getTaggedModifiedSequence(
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
