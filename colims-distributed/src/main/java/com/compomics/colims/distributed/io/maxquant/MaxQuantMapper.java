package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.QuantificationSettingsMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParameterParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.QuantificationEngineType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The DataMapper implementation for MaxQuant projects.
 *
 * @author Davy
 * @author Iain
 */
@Component("maxQuantMapper")
public class MaxQuantMapper implements DataMapper<MaxQuantImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantMapper.class);

    /**
     * Quant file name
     */
    private static final String QUANT_FILE = "msms.txt";

    @Autowired
    private MaxQuantParameterParser parameterParser;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private QuantificationSettingsMapper quantificationSettingsMapper;
    /**
     * The fasta db entity service.
     */
    @Autowired
    private FastaDbService fastaDbService;

    @Override
    public void clear() {
        parameterParser.clear();
    }

    @Override
    public MappedData mapData(MaxQuantImport maxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();
        Set<ProteinGroup> proteinGroups = new HashSet<>();

        try {
            maxQuantParser.clear();

            FastaDb fastaDb = fastaDbService.findById(maxQuantImport.getFastaDbId());
            parameterParser.parse(maxQuantImport.getMaxQuantDirectory(), fastaDb, false);
            maxQuantParser.parseFolder(maxQuantImport.getMaxQuantDirectory(), fastaDb, parameterParser.getMultiplicity());

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

                analyticalRun.setQuantificationSettings(importQuantSettings(new File(maxQuantImport.getMaxQuantDirectory(), QUANT_FILE), analyticalRun));
            }
        } catch (IOException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return new MappedData(mappedRuns, proteinGroups);
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
     * Map the quantification settings
     *
     * @param quantFile     The file containing quant data
     * @param analyticalRun the AnalyticalRun instance onto the quantification settings will be mapped
     * @return the imported QuantificationSettings instance
     * @throws IOException thrown in case of an I/O related problem
     */
    private QuantificationSettings importQuantSettings(File quantFile, final AnalyticalRun analyticalRun) throws IOException {
        QuantificationSettings quantificationSettings;

        List<File> quantFiles = new ArrayList<>();
        quantFiles.add(quantFile);
        QuantificationParameters params = new QuantificationParameters();

        quantificationSettings = quantificationSettingsMapper.map(QuantificationEngineType.MAX_QUANT, parameterParser.getVersion(), quantFiles, params);

        quantificationSettings.setAnalyticalRun(analyticalRun);

        return quantificationSettings;
    }
}
