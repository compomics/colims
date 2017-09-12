package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for the text files generated by a
 * <a href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantSpectraParser} to handle
 * specific files contained in the text folder.
 */
@Component("maxQuantParser")
public class MaxQuantParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantParser.class);

    /**
     * The map of analytical runs (key: run name; value: the
     * {@link AnalyticalRun} instance);
     */
    private final Map<String, AnalyticalRun> analyticalRuns = new HashMap<>();
    /**
     * The child parsers.
     */
    private final MaxQuantSpectraParser maxQuantSpectraParser;
    private final MaxQuantProteinGroupsParser maxQuantProteinGroupsParser;
    private final MaxQuantEvidenceParser maxQuantEvidenceParser;
    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;
    private final MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser;
    private final FastaDbService fastaDbService;

    @Autowired
    public MaxQuantParser(MaxQuantSpectraParser maxQuantSpectraParser,
                          MaxQuantProteinGroupsParser maxQuantProteinGroupsParser,
                          MaxQuantEvidenceParser maxQuantEvidenceParser,
                          MaxQuantSearchSettingsParser maxQuantSearchSettingsParser,
                          MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser,
                          FastaDbService fastaDbService) {
        this.maxQuantSpectraParser = maxQuantSpectraParser;
        this.maxQuantProteinGroupsParser = maxQuantProteinGroupsParser;
        this.maxQuantEvidenceParser = maxQuantEvidenceParser;
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
        this.maxQuantQuantificationSettingsParser = maxQuantQuantificationSettingsParser;
        this.fastaDbService = fastaDbService;
    }

    /**
     * Clear the parser's resources.
     */
    public void clear() {
        maxQuantEvidenceParser.clear();
        maxQuantSpectraParser.clear();
        maxQuantProteinGroupsParser.clear();
        maxQuantSearchSettingsParser.clear();
        maxQuantQuantificationSettingsParser.clear();
        analyticalRuns.clear();
    }

    /**
     * Parse the MaxQuant output folder and map the content of the different
     * files to Colims entities.
     *
     * @param maxQuantImport  the {@link MaxQuantImport} instance
     * @param fastasDirectory the FASTA DBs directory
     * @throws IOException          in case of an input/output related problem
     * @throws UnparseableException in case of a problem occured while parsing
     * @throws JDOMException        in case of an XML parsing related problem
     */
    public void parse(MaxQuantImport maxQuantImport, Path fastasDirectory) throws IOException, UnparseableException, JDOMException {
        EnumMap<FastaDbType, List<FastaDb>> fastaDbs = new EnumMap<>(FastaDbType.class);
        //get the FASTA db entities from the database
        maxQuantImport.getFastaDbIds().forEach((FastaDbType fastaDbType, List<Long> fastaDbIds) -> {
            List<FastaDb> fastaDbList = new ArrayList<>();
            fastaDbIds.forEach(fastaDbId -> fastaDbList.add(fastaDbService.findById(fastaDbId)));
            fastaDbs.put(fastaDbType, fastaDbList);
        });

        //parse the search settings
        LOGGER.debug("parsing search settings");
        maxQuantSearchSettingsParser.parse(maxQuantImport.getCombinedDirectory(), maxQuantImport.getMqParFile(), fastaDbs);

        //populate the analytical runs map
        maxQuantSearchSettingsParser.getAnalyticalRuns().keySet().forEach((run -> analyticalRuns.put(run.getName(), run)));

        //look for the MaxQuant txt directory
        Path txtDirectory = Paths.get(maxQuantImport.getCombinedDirectory() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
        if (!Files.exists(txtDirectory)) {
            throw new FileNotFoundException("The MaxQuant txt file " + txtDirectory.toString() + " was not found.");
        }

        //parse the protein groups file
        LOGGER.debug("parsing proteinGroups.txt");
        //we want the FASTA DB files to be parsed in the order the FastaDbType enum values are declared
        //so use a LinkedHashMap to preserve the natural FastaDbType enum order
        //(iterating over an EnumMap maintains that order as well)
        LinkedHashMap<FastaDb, Path> fastaDbMap = new LinkedHashMap<>();
        for (Map.Entry<FastaDbType, List<FastaDb>> entry : fastaDbs.entrySet()) {
            entry.getValue().forEach(fastaDb -> {
                //make the path absolute and check if it exists
                Path absoluteFastaDbPath = fastasDirectory.resolve(fastaDb.getFilePath());
                if (!Files.exists(absoluteFastaDbPath)) {
                    throw new IllegalArgumentException("The FASTA DB file " + absoluteFastaDbPath + " doesn't exist.");
                }
                fastaDbMap.put(fastaDb, absoluteFastaDbPath);
            });
        }

        //look for the proteinGroups.txt file
        Path proteinGroupsFile = Paths.get(txtDirectory.toString(), MaxQuantConstants.PROTEIN_GROUPS_FILE.value());
        if (!Files.exists(proteinGroupsFile)) {
            throw new FileNotFoundException("The proteinGroups.txt " + proteinGroupsFile.toString() + " was not found.");
        }
        maxQuantProteinGroupsParser.parse(proteinGroupsFile, fastaDbMap, maxQuantImport.getQuantificationLabel(), maxQuantImport.isIncludeContaminants(), maxQuantImport.getSelectedProteinGroupHeaders());

        LOGGER.debug("parsing msms.txt");
        maxQuantSpectraParser.parse(maxQuantImport.getCombinedDirectory(), maxQuantImport.isIncludeUnidentifiedSpectra(), maxQuantProteinGroupsParser.getOmittedProteinGroupIds());

        LOGGER.debug("parsing evidence.txt");
        Path evidenceFile = Paths.get(txtDirectory.toString(), MaxQuantConstants.EVIDENCE_FILE.value());
        if (!Files.exists(evidenceFile)) {
            throw new FileNotFoundException("The evidence.txt " + evidenceFile.toString() + " was not found.");
        }
        maxQuantEvidenceParser.parse(evidenceFile, maxQuantProteinGroupsParser.getOmittedProteinGroupIds());

        //add the identified spectra for each run and set the entity relations
        analyticalRuns.forEach((runName, run) -> {

            //set analytical run for search settings
            run.getSearchAndValidationSettings().setAnalyticalRun(run);

            //get the spectrum apl keys for each run
            Set<String> aplKeys = maxQuantSpectraParser.getMaxQuantSpectra().getRunToSpectrums().get(runName);
            aplKeys.forEach(aplKey -> {
                //get the spectrum by it's key
                AnnotatedSpectrum annotatedSpectrum = maxQuantSpectraParser.getMaxQuantSpectra().getSpectra().get(aplKey);

                //set the entity relations between run and spectrum
                run.getSpectrums().add(annotatedSpectrum.getSpectrum());
                annotatedSpectrum.getSpectrum().setAnalyticalRun(run);

                //set the child entity relations for the spectrum
                setSpectrumRelations(aplKey, annotatedSpectrum);
            });
        });

        //add the matching between runs peptides for each run
        Map<String, Set<Integer>> runToMbrPeptides = maxQuantEvidenceParser.getRunToMbrPeptides();
        runToMbrPeptides.forEach((runName, evidenceIds) -> {
            //get the run by name
            AnalyticalRun run = analyticalRuns.get(runName);

            evidenceIds.forEach(evidenceId -> {
                //create a dummy spectrum for each peptide
                Spectrum spectrum = createDummySpectrum();

                //set the entity relations between run and spectrum
                run.getSpectrums().add(spectrum);
                spectrum.setAnalyticalRun(run);

                //set the child relations for the spectrum
                setPeptideRelations(spectrum, null, null, evidenceId);
            });

        });

        //add the unidentified spectra for each run
        maxQuantSpectraParser.getMaxQuantSpectra().getUnidentifiedSpectra().forEach((runName, spectrum) -> analyticalRuns.get(runName).getSpectrums().addAll(spectrum));

        //parse the quantification settings
        //for a SILAC or ICAT experiments, we don't have any reagent name from maxquant.
        //Colims gives reagent names according to the number of samples.
        if (maxQuantImport.getQuantificationLabel().equals(MaxQuantImport.SILAC)) {
            List<String> silacReagents = new ArrayList<>();
            if (maxQuantSearchSettingsParser.getLabelMods().size() == 3) {
                silacReagents.addAll(Arrays.asList("SILAC light", "SILAC medium", "SILAC heavy"));
                maxQuantQuantificationSettingsParser.parse(new ArrayList<>(analyticalRuns.values()), maxQuantImport.getQuantificationLabel(), silacReagents);
            } else if (maxQuantSearchSettingsParser.getLabelMods().size() == 2) {
                silacReagents.addAll(Arrays.asList("SILAC light", "SILAC heavy"));
                maxQuantQuantificationSettingsParser.parse(new ArrayList<>(analyticalRuns.values()), maxQuantImport.getQuantificationLabel(), silacReagents);
            }
        } else if (maxQuantImport.getQuantificationLabel().equals(MaxQuantImport.ICAT)) {
            List<String> icatReagents = new ArrayList<>();
            icatReagents.addAll(Arrays.asList("ICAT light reagent", "ICAT heavy reagent"));
            maxQuantQuantificationSettingsParser.parse(new ArrayList<>(analyticalRuns.values()), maxQuantImport.getQuantificationLabel(), icatReagents);
        } else {
            List<String> reagents = new ArrayList<>(maxQuantSearchSettingsParser.getIsobaricLabels().values());
            maxQuantQuantificationSettingsParser.parse(new ArrayList<>(analyticalRuns.values()), maxQuantImport.getQuantificationLabel(), reagents);
        }
        //link the quantification settings to each analytical run
        analyticalRuns.values().forEach(analyticalRun -> analyticalRun.setQuantificationSettings(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().get(analyticalRun)));

        if (getSpectrumToPsms().isEmpty() || maxQuantEvidenceParser.getSpectrumToPeptides().isEmpty() || maxQuantProteinGroupsParser.getProteinGroups().isEmpty()) {
            throw new UnparseableException("One of the parsed files could not be read properly.");
        }
    }

    /**
     * Get the map that links the apl spectra with msms.txt entries (key: apl
     * key; value:).
     *
     * @return the link map
     */
    public Map<String, Set<Integer>> getSpectrumToPsms() {
        return maxQuantSpectraParser.getMaxQuantSpectra().getSpectrumToPsms();
    }

    /**
     * Return a list copy of the spectra per run map values.
     *
     * @return Collection of runs
     */
    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns.values().stream().collect(Collectors.toList());
    }

    /**
     * Get the protein groups as a set.
     *
     * @return the protein group set
     */
    public Set<ProteinGroup> getProteinGroupSet() {
        return maxQuantProteinGroupsParser.getProteinGroups().values().stream().collect(Collectors.toSet());
    }

    /**
     * Create the necessary relationships for the children of a spectrum.
     *
     * @param aplKey            the apl annotatedSpectrum key
     * @param annotatedSpectrum the {@link AnnotatedSpectrum} instance
     */
    private void setSpectrumRelations(String aplKey, AnnotatedSpectrum annotatedSpectrum) {
        //get the msms.txt IDs associated with the given annotatedSpectrum
        Set<Integer> msmsIds = maxQuantSpectraParser.getMaxQuantSpectra().getSpectrumToPsms().get(aplKey);
        for (Integer msmsId : msmsIds) {
            //get the evidence IDs associated with the msms ID
            for (Integer evidenceId : maxQuantEvidenceParser.getSpectrumToPeptides().get(msmsId)) {
                setPeptideRelations(annotatedSpectrum.getSpectrum(), annotatedSpectrum.getIonMatches(), annotatedSpectrum.getFragmentMasses(), evidenceId);
            }
        }
    }

    /**
     * Create the necessary relationships for the children of a peptide.
     *
     * @param spectrum       the associated {@link Spectrum} instance
     * @param matchedIons    the matched fragment ions
     * @param fragmentMasses the matched fragment masses
     * @param evidenceId     the peptide evidence ID
     */
    private void setPeptideRelations(Spectrum spectrum, String matchedIons, String fragmentMasses, Integer evidenceId) {
        //get the associated peptides by their evidence ID
        List<Peptide> peptides = maxQuantEvidenceParser.getPeptides().get(evidenceId);

        //look for a peptide that isn't already associated with a spectrum
        Optional<Peptide> foundPeptide = peptides.stream().filter(peptide -> peptide.getSpectrum() == null).findAny();
        if (foundPeptide.isPresent()) {
            Peptide peptide = foundPeptide.get();

            //get the protein groups IDs for each peptide
            Set<Integer> proteinGroupIds = maxQuantEvidenceParser.getPeptideToProteinGroups().get(evidenceId);

            proteinGroupIds.forEach(proteinGroupId -> {
                //get the protein group by it's ID
                ProteinGroup proteinGroup = maxQuantProteinGroupsParser.getProteinGroups().get(proteinGroupId);

                PeptideHasProteinGroup peptideHasProteinGroup = new PeptideHasProteinGroup();
                peptideHasProteinGroup.setPeptide(peptide);
                peptideHasProteinGroup.setProteinGroup(proteinGroup);

                proteinGroup.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
                //set peptideHasProteinGroups in peptide
                peptide.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
            });

            //set matched fragment ions and masses
            if (matchedIons != null && fragmentMasses != null) {
                peptide.setFragmentIons(matchedIons);
                peptide.setFragmentMasses(fragmentMasses);
            }

            //set entity relations between Spectrum and Peptide
            spectrum.getPeptides().add(peptide);
            peptide.setSpectrum(spectrum);
        } else {
            throw new IllegalStateException("No peptide without associated spectrum found for " + evidenceId);
        }
    }

    /**
     * Create a dummy spectrum for a matching between runs (MBR) identification.
     *
     * @return the dummy {@link Spectrum} instance
     */
    private Spectrum createDummySpectrum() {
        Spectrum spectrum = new Spectrum();

        spectrum.setAccession(Spectrum.MBR_SPECTRUM_ACCESSION);

        return spectrum;
    }
}
