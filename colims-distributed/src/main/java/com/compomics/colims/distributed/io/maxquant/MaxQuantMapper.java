package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.QuantificationSettingsMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantAndromedaParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSearchSettingsParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.QuantificationEngineType;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
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
     * The quantification file name.
     */
    private static final String QUANT_FILE = "msms.txt";

    @Autowired
    private MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;
    @Autowired
    private QuantificationSettingsMapper quantificationSettingsMapper;
    @Autowired
    private FastaDbService fastaDbService;

    @Override
    public void clear() {
        maxQuantSearchSettingsParser.clear();
    }

    @Override
    public MappedData mapData(MaxQuantImport maxQuantImport) throws MappingException, JDOMException{
        LOGGER.info("started mapping folder: " + maxQuantImport.getParameterFilePath().toString());

        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        Set<ProteinGroup> proteinGroups;

        try {
            maxQuantParser.clear();

            EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
            //get the FASTA db entities from the database
            maxQuantImport.getFastaDbIds().forEach((fastaDbType, fastaDbId) -> {
                fastaDbs.put(fastaDbType, fastaDbService.findById(fastaDbId));
            });
            Path txtDirectory = Paths.get(maxQuantImport.getCombinedFolderDirectory() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
            Path andromedaDirectory = Paths.get(maxQuantImport.getCombinedFolderDirectory() + File.separator + MaxQuantConstants.ANDROMEDA_DIRECTORY.value());
            try{
                maxQuantSearchSettingsParser.parse(maxQuantImport.getCombinedFolderDirectory(), maxQuantImport.getParameterFilePath(), fastaDbs, false);
            }catch (JDOMException e){
                e.printStackTrace();
            }

            maxQuantParser.parse(maxQuantImport.getCombinedFolderDirectory(), fastaDbs, maxQuantSearchSettingsParser.getMultiplicity());

            proteinGroups = maxQuantParser.getProteinGroupSet();
            for (AnalyticalRun analyticalRun : maxQuantParser.getRuns()) {
                if(maxQuantSearchSettingsParser.getRunSettings().containsKey(analyticalRun.getName())){
                    analyticalRun.setStorageLocation(maxQuantImport.getCombinedFolderDirectory().toString());
                    SearchAndValidationSettings searchAndValidationSettings = maxQuantSearchSettingsParser.getRunSettings().get(analyticalRun.getName());
                    //set search and validation settings-run entity associations
                    analyticalRun.setSearchAndValidationSettings(searchAndValidationSettings);
                    searchAndValidationSettings.setAnalyticalRun(analyticalRun);
                    List<Spectrum> mappedSpectra = new ArrayList<>(analyticalRun.getSpectrums().size());
                    //set spectrum-run entity associations
                    for (Spectrum spectrum : analyticalRun.getSpectrums()) {
                        spectrum.setAnalyticalRun(analyticalRun);
                        mappedSpectra.add(mapSpectrum(spectrum));
                    }
                    analyticalRun.setSpectrums(mappedSpectra);

                    analyticalRuns.add(analyticalRun);

                    //               analyticalRun.setQuantificationSettings(importQuantSettings(new File(txtDirectory.toFile(), QUANT_FILE), analyticalRun));
                }
            }
        } catch (IOException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, proteinGroups);
    }

    /**
     * Create relationships for the children of a spectrum.
     *
     * @param spectrum the spectrum object
     * @return The same object but with a bunch of relations
     * @throws MappingException
     */
    private Spectrum mapSpectrum(Spectrum spectrum) throws MappingException {
        // TODO: 27/05/16 check if this still works with multiple peptides linked to one spectrum
        List<Peptide> peptides = maxQuantParser.getIdentificationForSpectrum(spectrum);
        for (Peptide peptide : peptides) {
            List<ProteinGroup> proteinGroups = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(peptide));

            proteinGroups.stream().forEach(proteinGroup -> {
                PeptideHasProteinGroup phpGroup = new PeptideHasProteinGroup();
                phpGroup.setPeptidePostErrorProbability(peptide.getPsmPostErrorProbability());
                phpGroup.setPeptideProbability(peptide.getPsmProbability());
                phpGroup.setPeptide(peptide);
                phpGroup.setProteinGroup(proteinGroup);

                proteinGroup.getPeptideHasProteinGroups().add(phpGroup);
                // set peptideHasProteinGroups in peptide
                peptide.getPeptideHasProteinGroups().add(phpGroup);
            });

            //set entity relations between Spectrum and Peptide
            spectrum.getPeptides().addAll(peptides);
            peptide.setSpectrum(spectrum);
        }

        return spectrum;
    }

    /**
     * Map the quantification settings.
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

        quantificationSettings = quantificationSettingsMapper.map(QuantificationEngineType.MAX_QUANT, maxQuantSearchSettingsParser.getVersion(), quantFiles, params);

        quantificationSettings.setAnalyticalRun(analyticalRun);

        return quantificationSettings;
    }
}
