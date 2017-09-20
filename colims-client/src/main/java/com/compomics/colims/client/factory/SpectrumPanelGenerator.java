package com.compomics.colims.client.factory;

import com.compomics.colims.client.view.SpectrumDialog;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.colims_to_utilities.ColimsPeptideMapper;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSearchParametersMapper;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.IonMatch;
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
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class generates a spectrum panel for a PSM (Peptide-to-spectrum match).
 *
 * @author Niels Hulstaert
 */
@Component("spectrumPanelGenerator")
public class SpectrumPanelGenerator {

    /**
     * The ID of the current analytical run.
     */
    private Long analyticalRunId = Long.MIN_VALUE;
    /**
     * The current search engine type.
     */
    private SearchEngineType searchEngineType;
    /**
     * The Utilities search parameters mapped from the Colims search parameters.
     */
    private SearchParameters utilitiesSearchParameters;
    /**
     * The Utilities annotation settings mapped from the Colims search parameters.
     */
    private AnnotationSettings annotationSettings;
    private final UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();
    private final PTMFactory ptmFactory = PTMFactory.getInstance();

    private final SpectrumService spectrumService;
    private final ColimsSpectrumMapper colimsSpectrumMapper;
    private final ColimsPeptideMapper colimsPeptideMapper;
    private final ColimsSearchParametersMapper colimsSearchParametersMapper;

    @Autowired
    public SpectrumPanelGenerator(SpectrumService spectrumService,
                                  ColimsSpectrumMapper colimsSpectrumMapper,
                                  ColimsPeptideMapper colimsPeptideMapper,
                                  ColimsSearchParametersMapper colimsSearchParametersMapper
    ) {
        this.spectrumService = spectrumService;
        this.colimsSpectrumMapper = colimsSpectrumMapper;
        this.colimsPeptideMapper = colimsPeptideMapper;
        this.colimsSearchParametersMapper = colimsSearchParametersMapper;
    }

    /**
     * Load the search settings for the given run and map them to the
     * corresponding Utilities objects (SearchParameters, AnnotationSettings).
     *
     * @param analyticalRun the AnalyticalRun instance
     */
    public void loadSettingsForRun(AnalyticalRun analyticalRun) {
        //check if the correct the settings are already loaded
        if (!analyticalRunId.equals(analyticalRun.getId())) {
            analyticalRunId = analyticalRun.getId();

            searchEngineType = analyticalRun.getSearchAndValidationSettings().getSearchEngine().getSearchEngineType();
            com.compomics.colims.model.SearchParameters colimsSearchParameters = analyticalRun.getSearchAndValidationSettings().getSearchParameters();
            utilitiesSearchParameters = colimsSearchParametersMapper.mapForSpectrumPanel(colimsSearchParameters);

            //use the search parameters to set up the annotation settings
            annotationSettings = new AnnotationSettings(utilitiesSearchParameters);
        }
    }

    /**
     * Generate a spectrum dialog for the given peptide to spectrum match (PSM).
     *
     * @param parent  the parent JFrame component
     * @param peptide the Peptide instance
     * @return the generated SpectrumDialog instance
     * @throws MappingException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     * @throws SQLException
     * @throws IOException
     */
    public SpectrumDialog generateSpectrumDialog(JFrame parent, Peptide peptide) throws MappingException, ClassNotFoundException, InterruptedException, SQLException, IOException {
        SpectrumDialog spectrumDialog = new SpectrumDialog(parent, false, peptide);

        JPanel spectrumJPanel = spectrumDialog.getSpectrumPanel();
        JPanel secondarySpectrumPlotsJPanel = spectrumDialog.getSecondarySpectrumPlotsPanel();

        addPsm(peptide, spectrumJPanel, secondarySpectrumPlotsJPanel);

        return spectrumDialog;
    }

    /**
     * Add the Utilities SpectrumPanel for the given PSM to the given JPanel.
     *
     * @param peptide                           the Peptide instance
     * @param spectrumParentPanel               the parent panel where the spectrum will be added to
     * @param secondarySpectrumPlotsParentPanel the parent panel were the secondary spectrum plots will be added to
     * @throws com.compomics.colims.core.io.MappingException
     * @throws java.lang.InterruptedException
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    private void addPsm(Peptide peptide, JPanel spectrumParentPanel, JPanel secondarySpectrumPlotsParentPanel) throws MappingException, InterruptedException, ClassNotFoundException, SQLException, IOException {
        Spectrum spectrum = peptide.getSpectrum();

        //fetch the spectrum files associated with this spectrum
        spectrumService.fetchSpectrumFiles(spectrum);

        //map the Colims Spectrum instance onto the Utilities MSnSpectrum instance
        MSnSpectrum msnSpectrum = colimsSpectrumMapper.map(spectrum);

        //construct the spectrum panel
        Collection<Peak> peaks = msnSpectrum.getPeakList();

        //remove existing components
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

            //map the Colims Peptide instance onto the PeptideAssumption
            PeptideAssumption peptideAssumption = colimsPeptideMapper.map(peptide);

            PeptideSpectrumAnnotator peptideSpectrumAnnotator = new PeptideSpectrumAnnotator();

            SpecificAnnotationSettings specificAnnotationSettings = annotationSettings.getSpecificAnnotationPreferences(
                    msnSpectrum.getSpectrumTitle(),
                    peptideAssumption,
                    new SequenceMatchingPreferences(),
                    new SequenceMatchingPreferences()
            );

            //get the fragment ion annotations
            //for MaxQuant, get them from the database
            //for PeptideShaker, calculate them on the fly
            ArrayList<IonMatch> annotations = new ArrayList<>();
            switch (searchEngineType) {
                case MAXQUANT:
                    annotations = colimsPeptideMapper.mapFragmentAnnotations(peptide);
                    break;
                case PEPTIDESHAKER:
                    annotations = peptideSpectrumAnnotator.getSpectrumAnnotation(
                            annotationSettings,
                            specificAnnotationSettings,
                            msnSpectrum,
                            peptideAssumption.getPeptide()
                    );
                    break;
            }

            spectrumPanel.addAutomaticDeNovoSequencing(
                    peptideAssumption.getPeptide(),
                    annotations,
                    utilitiesSearchParameters.getForwardIons().get(0),
                    utilitiesSearchParameters.getRewindIons().get(0),
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
                    utilitiesSearchParameters.getPtmSettings(),
                    utilitiesSearchParameters.getForwardIons().get(0),
                    utilitiesSearchParameters.getRewindIons().get(0)
            );

            secondarySpectrumPlotsParentPanel.removeAll();
            secondarySpectrumPlotsParentPanel.add(sequenceFragmentationPanel);
            secondarySpectrumPlotsParentPanel.add(new IntensityHistogram(annotations, msnSpectrum, 0.75));

            MassErrorPlot massErrorPlot = new MassErrorPlot(annotations, msnSpectrum, annotationSettings.getFragmentIonAccuracy(), false);

            secondarySpectrumPlotsParentPanel.add(massErrorPlot);

            secondarySpectrumPlotsParentPanel.revalidate();
            secondarySpectrumPlotsParentPanel.repaint();

            spectrumParentPanel.add(spectrumPanel);
        } else {
            spectrumParentPanel.add(new JPanel());
        }
        spectrumParentPanel.revalidate();
        spectrumParentPanel.repaint();
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with PTM tags, e.g, &lt;mox&gt;. /!\
     * This method will work only if the PTM found in the peptide are in the
     * PTMFactory. /!\ This method uses the modifications as set in the
     * modification matches of this peptide and displays all of them.
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

        peptide.getModificationMatches().stream().forEach(modMatch -> {
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
        });

        return com.compomics.util.experiment.biology.Peptide.getTaggedModifiedSequence(
                utilitiesSearchParameters.getPtmSettings(),
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
