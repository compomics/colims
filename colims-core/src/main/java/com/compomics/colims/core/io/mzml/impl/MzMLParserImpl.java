/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.mzml.impl;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.mzml.MzMLParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.CvParamType;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Niels Hulstaert
 */
@Component("mzMLParser")
public class MzMLParserImpl implements MzMLParser {

    private static final Logger LOGGER = Logger.getLogger(MzMLParserImpl.class);
    private static final String DEFAULT_SAMPLE_ACCESSION = "default_sample";
    private final Map<String, MzMLUnmarshaller> mzMLUnmarshallers;

    public MzMLParserImpl() {
        mzMLUnmarshallers = new HashMap<>();
    }

    @Override
    public void importMzMLFiles(List<File> mzMlfiles) {
        //clear map before importing
        mzMLUnmarshallers.clear();
        mzMlfiles.stream().forEach((mzMlFile) -> {
            MzMLUnmarshaller mzMlUnmarshaller = new MzMLUnmarshaller(mzMlFile);
            mzMLUnmarshallers.put(mzMlFile.getName(), mzMlUnmarshaller);
        });
    }

    @Override
    public Experiment parseMzMlFile(String mzMLFileName) throws MzMLUnmarshallerException, IOException, MappingException {
        LOGGER.info("Start parsing experiment from file " + mzMLFileName);
        Experiment experiment = new Experiment();

        //get the MzMLUnmarschaller
        MzMLUnmarshaller mzMLUnmarshaller = mzMLUnmarshallers.get(mzMLFileName);
        if (mzMLUnmarshaller == null) {
            throw new IllegalArgumentException("The given MzML file " + mzMLFileName + " could not be found by the parser.");
        }

        //add samples
        addSamples(mzMLFileName, experiment);

        return experiment;
    }

    /**
     * Adds the samples to the experiment. If no samples are found, a default
     * sample is added.
     *
     * @param mzMLFileName the mzML file name
     * @param experiment the experiment
     */
    private void addSamples(String mzMLFileName, Experiment experiment) throws MzMLUnmarshallerException, IOException, MappingException {
        LOGGER.info("Start parsing samples");
        List<Sample> samples = new ArrayList<>();

        //get sample list
        SampleList sampleList = mzMLUnmarshallers.get(mzMLFileName).unmarshalFromXpath("/sampleList", SampleList.class);
        if (sampleList != null) {
            LOGGER.debug("Unmarshalling " + sampleList.getCount() + " sample(s) from mzML file: " + mzMLFileName);
            sampleList.getSample().stream().forEach(mzMLSample -> {
                Sample sample = new Sample(mzMLSample.getId());

                //a sample can contain mutliple materials
                //Material material = new Material();
                sample.setExperiment(experiment);
                samples.add(sample);
            });
        }

        //add default sample if list is empty
        if (samples.isEmpty()) {
            Sample sample = new Sample(DEFAULT_SAMPLE_ACCESSION);
            sample.setExperiment(experiment);
            samples.add(sample);
        }

        //set experiment sample collection
        experiment.setSamples(samples);

        //add run to samples
        addRun(mzMLFileName, samples);
    }

    /**
     * Adds the run to the correct sample from the sample list, as there is only
     * one run present in a mzML file.
     *
     * @param mzMLFileName the mzML file name
     * @param samples the sample list
     */
    private void addRun(String mzMLFileName, List<Sample> samples) throws MzMLUnmarshallerException, IOException, MappingException {
        //get run
        Run run = mzMLUnmarshallers.get(mzMLFileName).unmarshalFromXpath("/run", Run.class);
        LOGGER.debug("Unmarshalling run from mzML file: " + mzMLFileName);

        AnalyticalRun analyticalRun = new AnalyticalRun();
        analyticalRun.setName(run.getId());
        if (run.getStartTimeStamp() != null) {
            analyticalRun.setStartDate(run.getStartTimeStamp().getTime());
        }

        //find sample associated with this run.
        Sample foundSample = null;
        if (run.getSampleRef() != null) {
            for (Sample sample : samples) {
                if (run.getSampleRef().equals(sample.getName())) {
                    foundSample = sample;
                    break;
                }
            }
        }
        //if the found sample is null, take a default sample
        if (foundSample == null) {
            foundSample = samples.get(0);
        }

        //set relations
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        analyticalRun.setSample(foundSample);
        analyticalRuns.add(analyticalRun);
        foundSample.setAnalyticalRuns(analyticalRuns);

        //add instrument to analytical run
        addInstrument(mzMLFileName, analyticalRun);
        //add spectra to analytical run
        addSpectra(mzMLFileName, analyticalRun);
    }

    /**
     * Adds the instrument to the analytical run
     *
     * @param mzMLFileName the mzML file name
     * @param analyticalRun the analytical run
     */
    private void addInstrument(String mzMLFileName, AnalyticalRun analyticalRun) {
        LOGGER.debug("Adding instrument to run: " + analyticalRun.getName());
        InstrumentConfigurationList instrumentConfigurationList = mzMLUnmarshallers.get(mzMLFileName).unmarshalFromXpath("/instrumentConfigurationList", InstrumentConfigurationList.class);

        //For the moment, only consider the first InstrumentConfiguration
        //@todo include also possible other InstrumentConfigurations
        InstrumentConfiguration instrumentConfiguration = instrumentConfigurationList.getInstrumentConfiguration().get(0);
        Instrument instrument = new Instrument(instrumentConfiguration.getId());

        ComponentList componentList = instrumentConfiguration.getComponentList();
        //set source
        if (componentList.getSource() != null && !componentList.getSource().isEmpty()) {
            //for the moment, only consider the first source
            SourceComponent sourceComponent = componentList.getSource().get(0);
            InstrumentCvParam source = new InstrumentCvParam();
            if (sourceComponent.getCvParam() != null && !sourceComponent.getCvParam().isEmpty()) {
                CVParam cVParam = sourceComponent.getCvParam().get(0);
                source.setAccession(cVParam.getAccession());
                source.setName(cVParam.getName());
                source.setLabel(cVParam.getCvRef());
            }
            source.setcvTermType(CvParamType.SOURCE);

            //set relations
            instrument.setSource(source);
        }

        //set detector
        if (componentList.getDetector() != null && !componentList.getDetector().isEmpty()) {
            //for the moment, only consider the first detector
            DetectorComponent detectorComponent = componentList.getDetector().get(0);
            InstrumentCvParam detector = new InstrumentCvParam();
            if (detectorComponent.getCvParam() != null && !detectorComponent.getCvParam().isEmpty()) {
                CVParam cVParam = detectorComponent.getCvParam().get(0);
                detector.setAccession(cVParam.getAccession());
                detector.setName(cVParam.getName());
                detector.setLabel(cVParam.getCvRef());
            }
            detector.setcvTermType(CvParamType.ANALYZER);

            //set relations
            instrument.setSource(detector);
        }

        //set analyzers
        if (componentList.getAnalyzer() != null && !componentList.getAnalyzer().isEmpty()) {
            List<InstrumentCvParam> analyzers = new ArrayList<>();
            componentList.getAnalyzer().stream().forEach(analyzerComponent -> {
                InstrumentCvParam analyzer = new InstrumentCvParam();
                if (analyzerComponent.getCvParam() != null && !analyzerComponent.getCvParam().isEmpty()) {
                    CVParam cVParam = analyzerComponent.getCvParam().get(0);
                    analyzer.setAccession(cVParam.getAccession());
                    analyzer.setName(cVParam.getName());
                    analyzer.setLabel(cVParam.getCvRef());
                }
                analyzer.setcvTermType(CvParamType.ANALYZER);

                analyzers.add(analyzer);
            });
            //set relations
            instrument.setAnalyzers(analyzers);
        }

        //set relations
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        analyticalRuns.add(analyticalRun);
        instrument.setAnalyticalRuns(analyticalRuns);
    }

    /**
     * Adds spectra to the analytical run
     *
     * @param mzMLFileName the mzML file name
     * @param analyticalRun the analytical run
     */
    private void addSpectra(String mzMLFileName, AnalyticalRun analyticalRun) throws MzMLUnmarshallerException, IOException, MappingException {
        LOGGER.debug("Adding spectra to run: " + analyticalRun.getName());
        List<Spectrum> spectrums = new ArrayList<>();
        Set<String> spectrumIds = mzMLUnmarshallers.get(mzMLFileName).getSpectrumIDs();

        for (String spectrumId : spectrumIds) {
            try {
                Spectrum spectrum = getSpectrumById(mzMLFileName, spectrumId);
                spectrum.setAnalyticalRun(analyticalRun);
                spectrums.add(spectrum);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Spectrum with ID: " + spectrumId + " is an MS1 spectrum");
            }
        }

        analyticalRun.setSpectrums(spectrums);
    }

    private Spectrum getSpectrumById(String mzMLFileName, String spectrumId) throws MzMLUnmarshallerException, IOException, MappingException {
        Spectrum spectrum = new Spectrum();

        uk.ac.ebi.jmzml.model.mzml.Spectrum mzMLSpectrum = mzMLUnmarshallers.get(mzMLFileName).getSpectrumById(spectrumId);
        if (mzMLUnmarshallers.get(mzMLFileName) == null) {
            throw new IOException("MzML file not found: \'" + mzMLFileName + "\'!");
        }
        if (mzMLUnmarshallers.get(mzMLFileName).getSpectrumById(spectrumId) == null) {
            throw new IOException("Spectrum \'" + spectrumId + "\' in mzML file \'" + mzMLFileName + "\' not found!");
        }

        int level = 2;
        double mzRatio = 0.0;
        double scanTime = -1.0;
        int charge = 0;

        for (CVParam cvParam : mzMLSpectrum.getCvParam()) {
            if (cvParam.getAccession().equals("MS:1000511")) {
                level = Integer.parseInt(cvParam.getValue());
                break;
            }
        }
        //check for MS1 spectrum
        if (level == 1) {
            throw new IllegalArgumentException("MS1 spectrum.");
        }

        ScanList scanList = mzMLSpectrum.getScanList();
        if (scanList != null) {
            for (CVParam cvParam : scanList.getScan().get(scanList.getScan().size() - 1).getCvParam()) {
                if (cvParam.getAccession().equals("MS:1000016")) {
                    scanTime = Double.parseDouble(cvParam.getValue());
                    break;
                }
            }
        }
        PrecursorList precursorList = mzMLSpectrum.getPrecursorList();
        if (precursorList != null) {
            if (precursorList.getCount() == 1) {
                SelectedIonList sIonList = precursorList.getPrecursor().get(0).getSelectedIonList();
                if (sIonList != null) {
                    for (CVParam cvParam : sIonList.getSelectedIon().get(0).getCvParam()) {
                        switch (cvParam.getAccession()) {
                            case "MS:1000744":
                            case "MS:1000040":
                                mzRatio = Double.valueOf(cvParam.getValue());
                                break;
                            case "MS:1000041":
                                charge = Integer.parseInt(cvParam.getValue());
                                break;
                            default:
                        }
                    }
                }
            }
        }

        //create new MSnSpectrum
        ArrayList<Charge> possibleCharges = new ArrayList<>();
        possibleCharges.add(new Charge(Charge.PLUS, charge));
        Precursor precursor = new Precursor(0.0, mzRatio, possibleCharges);

        //get mz ratios and intensities and put them in a map (key: mz ratio, value: intensity)
        BinaryDataArray mzBinaryDataArray = mzMLSpectrum.getBinaryDataArrayList().getBinaryDataArray().get(0);
        Number[] mzNumbers = mzBinaryDataArray.getBinaryDataAsNumberArray();
        BinaryDataArray intBinaryDataArray = mzMLSpectrum.getBinaryDataArrayList().getBinaryDataArray().get(1);
        Number[] intNumbers = intBinaryDataArray.getBinaryDataAsNumberArray();
        HashMap<Double, Peak> peaks = new HashMap<>();
        for (int i = 0; i < mzNumbers.length; i++) {
            peaks.put(mzNumbers[i].doubleValue(), new Peak(mzNumbers[i].doubleValue(), intNumbers[i].doubleValue()));
        }

        MSnSpectrum mSnSpectrum = new MSnSpectrum(2, precursor, mzMLSpectrum.getId(), peaks, mzMLFileName);
        mSnSpectrum.setScanStartTime(scanTime);
        //map MSnSpectrum to colims spectrum
        //@TODO change this unnecessary mapping
//        utilitiesSpectrumMapper.map(mSnSpectrum, FragmentationType.EDT, spectrum);

        return spectrum;
    }
}
