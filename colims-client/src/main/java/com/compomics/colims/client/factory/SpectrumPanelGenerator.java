package com.compomics.colims.client.factory;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.colims_to_utilities.ColimsPeptideMapper;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSearchParametersMapper;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.gui.spectrum.IntensityHistogram;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This class generates a spectrum panel for a given spectrum.
 *
 * @author Niels Hulstaert
 */
@Component("spectrumPanelGenerator")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpectrumPanelGenerator {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SpectrumPanelGenerator.class);

    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private ColimsSpectrumMapper colimsSpectrumMapper;
    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;
    @Autowired
    private ColimsSearchParametersMapper colimsSearchParametersMapper;

    /**
     * The ID of the current analytical run.
     */
    private Long analyticalRunId;
    private SearchParameters utiltiesSearchParameters;
    private AnnotationSettings annotationSettings;
    UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();
    private PTMFactory ptmFactory = PTMFactory.getInstance();

    public SpectrumPanelGenerator() {
        System.out.println("---------------------");
    }

    /**
     * Load the search settings for the given run and map them to the corresponding Utilities objects (SearchParameters,
     * AnnotationSettings).
     *
     * @param analyticalRun the AnalyticalRun instance
     */
    public void loadSettingsForRun(AnalyticalRun analyticalRun) {
        //check if the correct the settings are already loaded
        if (analyticalRun.getId() != analyticalRunId) {
            analyticalRunId = analyticalRun.getId();

            com.compomics.colims.model.SearchParameters colimsSearchParameters = analyticalRun.getSearchAndValidationSettings().getSearchParameters();
            utiltiesSearchParameters = colimsSearchParametersMapper.mapForSpectrumPanel(colimsSearchParameters);

            //use the search parameters to set up the annotation settings
            annotationSettings = new AnnotationSettings(utiltiesSearchParameters);
        }
    }

    /**
     * Add the Utilities SpectrumPanel for the given spectrum to the given JPanel.
     *
     * @param spectrum                          the Spectrum instance
     * @param spectrumParentPanel               the parent panel where the spectrum will be added to
     * @param secondarySpectrumPlotsParentPanel the parent panel were the secondary spectrum plots will be added to
     */
    public void addSpectrum(Spectrum spectrum, JPanel spectrumParentPanel, JPanel secondarySpectrumPlotsParentPanel) throws MappingException, InterruptedException, ClassNotFoundException, SQLException, IOException {
        //fetch the spectrum files and peptides associated with this spectrum
        spectrumService.fetchSpectrumFilesAndPeptides(spectrum);

        MSnSpectrum msnSpectrum = new MSnSpectrum();
        //map the Colims Spectrum instance onto the Utilities MSnSpectrum instance
        colimsSpectrumMapper.map(spectrum, msnSpectrum);

        //construct the spectrum panel
        Collection<Peak> peaks = msnSpectrum.getPeakList();

        spectrumParentPanel.removeAll();

        if (peaks != null && !peaks.isEmpty()) {
            SpectrumPanel spectrumPanel = new SpectrumPanel(
                    msnSpectrum.getMzValuesAsArray(),
                    msnSpectrum.getIntensityValuesAsArray(),
                    msnSpectrum.getPrecursor().getMz(),
                    msnSpectrum.getPrecursor().getPossibleChargesAsString(),
                    "",
                    40,
                    false, false, false,
                    2,
                    false
            );

            spectrumPanel.setDeltaMassWindow(annotationSettings.getFragmentIonAccuracy());
            spectrumPanel.setBorder(null);
            spectrumPanel.setDataPointAndLineColor(utilitiesUserPreferences.getSpectrumAnnotatedPeakColor(), 0);
            spectrumPanel.setPeakWaterMarkColor(utilitiesUserPreferences.getSpectrumBackgroundPeakColor());
            spectrumPanel.setPeakWidth(utilitiesUserPreferences.getSpectrumAnnotatedPeakWidth());
            spectrumPanel.setBackgroundPeakWidth(utilitiesUserPreferences.getSpectrumBackgroundPeakWidth());
            spectrumPanel.showAnnotatedPeaksOnly(!annotationSettings.showAllPeaks());
            spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(annotationSettings.yAxisZoomExcludesBackgroundPeaks());

            List<Peptide> peptides = spectrum.getPeptides();
            if (!peptides.isEmpty()) {
                //map the Colims Peptide instance onto the PeptideAssumption
                PeptideAssumption peptideAssumption = colimsPeptideMapper.map(peptides.get(0));

                PeptideSpectrumAnnotator peptideSpectrumAnnotator = new PeptideSpectrumAnnotator();

                SpecificAnnotationSettings specificAnnotationSettings = annotationSettings.getSpecificAnnotationPreferences(
                        msnSpectrum.getSpectrumKey(),
                        peptideAssumption,
                        new SequenceMatchingPreferences(),
                        new SequenceMatchingPreferences()
                );

                ArrayList<IonMatch> annotations = peptideSpectrumAnnotator.getSpectrumAnnotation(
                        annotationSettings,
                        specificAnnotationSettings,
                        msnSpectrum,
                        peptideAssumption.getPeptide()
                );

                spectrumPanel.addAutomaticDeNovoSequencing(
                        peptideAssumption.getPeptide(),
                        annotations,
                        utiltiesSearchParameters.getIonSearched1(),
                        utiltiesSearchParameters.getIonSearched2(),
                        annotationSettings.getDeNovoCharge(),
                        annotationSettings.showForwardIonDeNovoTags(),
                        annotationSettings.showRewindIonDeNovoTags(),
                        false
                );

                spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));

                SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(
                        getTaggedPeptideSequence(peptideAssumption.getPeptide(), false, false, false),
                        annotations,
                        true,
                        utiltiesSearchParameters.getPtmSettings(),
                        utiltiesSearchParameters.getIonSearched1(),
                        utiltiesSearchParameters.getIonSearched2()
                );

                secondarySpectrumPlotsParentPanel.removeAll();
                secondarySpectrumPlotsParentPanel.add(sequenceFragmentationPanel);
                secondarySpectrumPlotsParentPanel.add(new IntensityHistogram(annotations, msnSpectrum, 0.75));

                MassErrorPlot massErrorPlot = new MassErrorPlot(annotations, msnSpectrum, annotationSettings.getFragmentIonAccuracy(), false);

                secondarySpectrumPlotsParentPanel.add(massErrorPlot);

                secondarySpectrumPlotsParentPanel.revalidate();
                secondarySpectrumPlotsParentPanel.repaint();
            }
            spectrumParentPanel.add(spectrumPanel);
        } else {
            spectrumParentPanel.add(new JPanel());
        }
        spectrumParentPanel.revalidate();
        spectrumParentPanel.repaint();
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
                utiltiesSearchParameters.getPtmSettings(),
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
