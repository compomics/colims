package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.distributed.io.QuantificationSettingsMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParameterParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataImporter class for MaxQuant projects.
 *
 * @author Davy
 * @author Iain
 */
@Component
public class MaxQuantImporter implements DataImporter<MaxQuantImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantImporter.class);

    @Autowired
    private MaxQuantParameterParser parameterParser;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private QuantificationSettingsMapper quantificationSettingsMapper;

    @Override
    public void clear() {
        parameterParser.clear();
    }

    @Override
    public List<AnalyticalRun> importData(MaxQuantImport maxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();

        try {
            maxQuantParser.clear();

            parameterParser.parse(maxQuantImport.getMaxQuantDirectory(), maxQuantImport.getFastaDb(), false);
            maxQuantParser.parseFolder(maxQuantImport.getMaxQuantDirectory(), maxQuantImport.getFastaDb(), parameterParser.getMultiplicity());

            for (AnalyticalRun analyticalRun : maxQuantParser.getRuns()) {
                analyticalRun.setStorageLocation(maxQuantImport.getMaxQuantDirectory().getCanonicalPath());

                SearchAndValidationSettings searchAndValidationSettings = parameterParser.getRunSettings().values().iterator().next();
                analyticalRun.setSearchAndValidationSettings(searchAndValidationSettings);
                searchAndValidationSettings.setAnalyticalRun(analyticalRun);

                List<Spectrum> mappedSpectra = new ArrayList<>(analyticalRun.getSpectrums().size());

                for (Spectrum spectrum : analyticalRun.getSpectrums()) {
                    spectrum.setAnalyticalRun(analyticalRun);

                    mappedSpectra.add(mapSpectrum(spectrum));
                }

                analyticalRun.setSpectrums(mappedSpectra);
                mappedRuns.add(analyticalRun);
            }

        } catch (IOException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return mappedRuns;
    }

    /**
     * Create relationships for the children of a spectrum
     *
     * @param spectrum A spectrum object
     * @return The same object but with a bunch of relations
     * @throws MappingException
     */
    private Spectrum mapSpectrum(Spectrum spectrum) throws MappingException {
        Peptide peptide = maxQuantParser.getIdentificationForSpectrum(spectrum);

        List<ProteinGroup> proteinGroups = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(peptide));

        for (ProteinGroup proteinGroup : proteinGroups) {
            PeptideHasProteinGroup phpGroup = new PeptideHasProteinGroup();
            phpGroup.setPeptidePostErrorProbability(peptide.getPsmPostErrorProbability());
            phpGroup.setPeptideProbability(peptide.getPsmProbability());
            phpGroup.setPeptide(peptide);
            phpGroup.setProteinGroup(proteinGroup);

            proteinGroup.getPeptideHasProteinGroups().add(phpGroup);
        }

        spectrum.getPeptides().add(peptide);
        peptide.setSpectrum(spectrum);

        return spectrum;
    }

    /**
     * Map the quantification settings. To be refactored
     *
     * @param maxQuantImport The MaxQuantImport instance
     * @param analyticalRun  the AnalyticalRun instance onto the quantification settings will be mapped
     * @return the imported QuantificationSettings instance
     * @throws IOException thrown in case of an I/O related problem
     */
//    private QuantificationSettings importQuantSettings(final MaxQuantImport maxQuantImport, final AnalyticalRun analyticalRun) throws IOException {
//        QuantificationSettings quantificationSettings;
//
//        List<File> quantFiles = new ArrayList<>();
//        quantFiles.add(new File(maxQuantImport.getMaxQuantDirectory(), "msms.txt"));  // TODO: make a constant also is this the right file?
//        QuantificationParameters params = new QuantificationParameters();
//
//        quantificationSettings = quantificationSettingsMapper.map(QuantificationEngineType.MAX_QUANT, parameterParser.getMaxQuantVersion(), quantFiles, params);
//
//        //set entity relations
//        analyticalRun.setQuantificationSettings(quantificationSettings);
//        quantificationSettings.setAnalyticalRun(analyticalRun);
//
//        return quantificationSettings;
//    }
}
