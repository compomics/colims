package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantQuantificationSettingsParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSearchSettingsParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

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

    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    private final MaxQuantParser maxQuantParser;
    private final MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser;
    private final FastaDbService fastaDbService;

    @Autowired
    public MaxQuantMapper(MaxQuantSearchSettingsParser maxQuantSearchSettingsParser,
                          MaxQuantParser maxQuantParser,
                          MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser,
                          FastaDbService fastaDbService) {
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        this.maxQuantParser = maxQuantParser;
        this.maxQuantQuantificationSettingsParser = maxQuantQuantificationSettingsParser;
        this.fastaDbService = fastaDbService;
    }

    @Override
    public void clear() {
        maxQuantSearchSettingsParser.clear();
        maxQuantQuantificationSettingsParser.clear();
        maxQuantParser.clear();
    }

    @Override
    public MappedData mapData(MaxQuantImport maxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getParameterFilePath().toString());

        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        Set<ProteinGroup> proteinGroups;

        try {
            maxQuantParser.clear();

            EnumMap<FastaDbType, List<FastaDb>> fastaDbs = new EnumMap<>(FastaDbType.class);
            //get the FASTA db entities from the database
            maxQuantImport.getFastaDbIds().forEach((FastaDbType fastaDbType, List<Long> fastaDbIds) -> {
                List<FastaDb> fastaDbList = new ArrayList<>();
                fastaDbIds.forEach(fastaDbId -> {
                    fastaDbList.add(fastaDbService.findById(fastaDbId));
                });
                fastaDbs.put(fastaDbType, fastaDbList);
            });

            maxQuantSearchSettingsParser.parse(maxQuantImport.getCombinedFolderDirectory(), maxQuantImport.getParameterFilePath(), fastaDbs);

            maxQuantParser.parse(maxQuantImport.getCombinedFolderDirectory(), fastaDbs,
                    maxQuantImport.isIncludeContaminants(),
                    maxQuantImport.isIncludeUnidentifiedSpectra(),
                    maxQuantImport.getSelectedProteinGroupHeaders());

            for (AnalyticalRun analyticalRun : maxQuantParser.getRuns()) {
                if (maxQuantSearchSettingsParser.getRunSettings().containsKey(analyticalRun.getName())) {
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

                    //analyticalRun.setQuantificationSettings(importQuantSettings(new File(txtDirectory.toFile(), QUANT_FILE), analyticalRun));
                }
            }

            //parse the quantification settings
            //for a silac experiment, we don't have any reagent name from maxquant.
            //Colims gives reagent name due to number of sample.
            if (maxQuantImport.getQuantificationLabel().equals("SILAC")) {
                List<String> silacReagents = new ArrayList<>();
                if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) {
                    silacReagents.addAll(Arrays.asList("SILAC light", "SILAC medium", "SILAC heavy"));
                    maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), silacReagents);
                } else if (maxQuantSearchSettingsParser.getLabelMods().size() == 2) {
                    silacReagents.addAll(Arrays.asList("SILAC light", "SILAC heavy"));
                    maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), silacReagents);
                }
            } else {
                List<String> reagents = new ArrayList<>(maxQuantSearchSettingsParser.getIsobaricLabels().values());
                maxQuantQuantificationSettingsParser.parse(analyticalRuns, maxQuantImport.getQuantificationLabel(), reagents);
            }
            // link quantification settings to analytical run
            analyticalRuns.forEach(analyticalRun -> {
                analyticalRun.setQuantificationSettings(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().get(analyticalRun));
            });

        } catch (IOException | UnparseableException | JDOMException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        }

        return new MappedData(analyticalRuns, maxQuantParser.getProteinGroupSet());
    }

    /**
     * Create relationships for the children of a spectrum.
     *
     * @param spectrum the spectrum object
     * @return The same object but with a bunch of relations
     * @throws MappingException in case
     */
    private Spectrum mapSpectrum(Spectrum spectrum) {
        // TODO: 27/05/16 check if this still works with multiple peptides linked to one spectrum
        List<Peptide> peptides = maxQuantParser.getIdentificationForSpectrum(spectrum);
        for (Peptide peptide : peptides) {
            List<ProteinGroup> proteinGroups = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(peptide));

            proteinGroups.forEach(proteinGroup -> {
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
}
