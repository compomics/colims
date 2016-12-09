package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.OntologyTerm;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.service.TypedCvParamService;
import com.compomics.colims.distributed.io.SearchModificationMapper;
import com.compomics.colims.distributed.io.maxquant.FixedTabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.headers.MqParHeader;
import com.compomics.colims.distributed.io.maxquant.headers.MqParHeaders;
import com.compomics.colims.distributed.io.maxquant.headers.SummaryHeader;
import com.compomics.colims.distributed.io.maxquant.headers.SummaryHeaders;
import com.compomics.colims.model.*;
import com.compomics.colims.model.cv.TypedCvParam;
import com.compomics.colims.model.enums.*;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class parses the MaxQuant parameter files and maps them onto the Colims
 * SearchAndValidationSettings and related entities.
 *
 * @author Davy
 * @author Iain
 * @author niels
 */
@Component("maxQuantSearchSettingsParser")
public class MaxQuantSearchSettingsParser {

    private static final String NOT_APPLICABLE = "N/A";
    private static final String DEFAULT_SEARCH_TYPE_ACCESSION = "MS:1001083";
    private static final String MODIFICATION_NAME_ONLY = " ";
    private static final String VERSION = "maxQuantVersion";
    private static final String FILE_PATHS = "filepaths";
    private static final String EXPERIMENTS = "experiments";
    private static final String PARAM_GROUP_INDICES = "paramgroupindices";
    private static final String PARAMETER_GROUPS = "parametergroups";
    private static final String PARAMETER_DELIMITER = ";";

    /**
     * The MaxQuant version.
     */
    private String version = NOT_APPLICABLE;
    /**
     * The default search type.
     */
    private SearchCvParam defaultSearchType;
    /**
     * The settings used in the experiment, indexed by run.
     */
    private final Map<String, SearchAndValidationSettings> runSettings = new HashMap<>();
    /**
     * The mqpar.xml parameters with raw file name (key: raw file name; value:
     * enum map of spectrum parameters).
     */
    private final Map<String, EnumMap<MqParHeader, String>> mqParParamsWithRawFile = new HashMap<>();
    /**
     * The analytical run name with experiment name (key: analyticalRun ; value:
     * experiment name).
     */
    private final Map<AnalyticalRun, String> analyticalRuns = new HashMap<>();
    /**
     * Isobaric labels for labeled quantification.(key: index , value : isobaric
     * label)
     */
    private final Map<Integer, String> isobaricLabels = new HashMap<>();
    /**
     * label mods for SILAC experiments.(key: index , value : isobaric label)
     */
    private final Map<Integer, String> labelMods = new HashMap<>();
    /**
     * The PSM FDR threshold.
     */
    private Double psmFdr;
    /**
     * The protein FDR threshold.
     */
    private Double proteinFdr;
    /**
     * The MaxQuant to UNIMOD modification mappings.
     */
    private final Map<String, OntologyTerm> modificationMappings;
    /**
     * The parsed files' headers of interest.
     */
    private final MqParHeaders mqParHeaders;
    private final SummaryHeaders summaryHeaders;
    /**
     * Beans.
     */
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;
    private final TypedCvParamService typedCvParamService;
    private final SearchModificationMapper searchModificationMapper;

    /**
     * Constructor.
     *
     * @param searchAndValidationSettingsService the search and validation settings service
     * @param typedCvParamService                the type CV param service
     * @param searchModificationMapper           the search modification mapper
     * @param ontologyMapper                     the ontology mapper
     * @throws IOException in case of an Input/Output related problem while parsing the headers.
     */
    public MaxQuantSearchSettingsParser(SearchAndValidationSettingsService searchAndValidationSettingsService, TypedCvParamService typedCvParamService, SearchModificationMapper searchModificationMapper, OntologyMapper ontologyMapper) throws IOException {
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
        this.typedCvParamService = typedCvParamService;
        this.searchModificationMapper = searchModificationMapper;
        //get the modification mappings from the OntologyMapper
        modificationMappings = ontologyMapper.getMaxQuantMapping().getModifications();
        summaryHeaders = new SummaryHeaders();
        mqParHeaders = new MqParHeaders();
    }

    /**
     * Get the version of MaxQuant used.
     *
     * @return Version number string
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the run settings stored in this object
     *
     * @return Copy of the filename/settings map
     */
    public Map<String, SearchAndValidationSettings> getRunSettings() {
        return Collections.unmodifiableMap(runSettings);
    }

    /**
     * Get analytical runs name (experiment name) which have link with
     * analytical runs.
     *
     * @return analyticalRuns
     */
    public Map<AnalyticalRun, String> getAnalyticalRuns() {
        return analyticalRuns;
    }

    /**
     * Get isobaric label for labeled quantification
     *
     * @return isobaricLabels map
     */
    public Map<Integer, String> getIsobaricLabels() {
        return isobaricLabels;
    }

    /**
     * Get label modifications for SILAC experiments.
     *
     * @return labelMods
     */
    public Map<Integer, String> getLabelMods() {
        return labelMods;
    }

    /**
     * Clear data stored in parser.
     */
    public void clear() {
        runSettings.clear();
        mqParParamsWithRawFile.clear();
        analyticalRuns.clear();
        isobaricLabels.clear();
        labelMods.clear();
    }

    /**
     * Parse the search parameters for a MaxQuant experiment.
     *
     * @param combinedFolderDirectory the MaxQuant combined folder directory path
     * @param mqParFile               the mqpar.xml parameter file
     * @param fastaDbs                the FASTA databases used in the experiment
     * @throws IOException in case of of an I/O related problem
     */
    public void parse(Path combinedFolderDirectory, Path mqParFile, EnumMap<FastaDbType, List<FastaDb>> fastaDbs) throws IOException, JDOMException {
        runSettings.clear();

        Path txtDirectory = Paths.get(combinedFolderDirectory + File.separator + MaxQuantConstants.TXT_DIRECTORY.value());
        //parse the mxpar.xml file
        parseMqParFile(mqParFile);

        //parse the summary.txt file
        FixedTabularFileIterator<SummaryHeader> summaryIterator = new FixedTabularFileIterator<>(Paths.get(txtDirectory.toString(), MaxQuantConstants.SUMMARY_FILE.value()), summaryHeaders);
        EnumMap<SummaryHeader, String> summaryEntry;
        while (summaryIterator.hasNext()) {
            summaryEntry = summaryIterator.next();
            //parse the search settings
            if (mqParParamsWithRawFile.containsKey(summaryEntry.get(SummaryHeader.RAW_FILE))) {
                SearchAndValidationSettings searchAndValidationSettings
                        = parseSearchSettings(fastaDbs, summaryEntry.get(SummaryHeader.RAW_FILE));

                runSettings.put(summaryEntry.get(SummaryHeader.RAW_FILE), searchAndValidationSettings);
            }
        }

    }

    /**
     * Parse the search settings for the given experiment and map them onto a
     * Colims SearchAndValidationSettings instance.
     *
     * @param fastaDbs the FASTA databases used in the experiment
     * @return the mapped SearchAndValidationSettings instance
     * @throws IOException thrown in case of of an I/O related problem
     */
    private SearchAndValidationSettings parseSearchSettings(EnumMap<FastaDbType, List<FastaDb>> fastaDbs, String rawFileName) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //set the FASTA databases entity associations
        fastaDbs.forEach((k, v) -> {
            v.forEach(fastaDb -> {
                SearchSettingsHasFastaDb searchSettingsHasFastaDb = new SearchSettingsHasFastaDb(k, searchAndValidationSettings, fastaDb);
                searchAndValidationSettings.getSearchSettingsHasFastaDbs().add(searchSettingsHasFastaDb);
            });
        });

        //map the search parameters onto a Colims {@link SearchParameters} instance.
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchType(defaultSearchType);

        //set the target-decoy scoring strategy default to FDR
        searchParameters.setScoreType(ScoreType.FDR);

        //precursor mass tolerance
        String precursorMassToleranceString = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.PEPTIDE_MASS_TOLERANCE);
        searchParameters.setPrecMassTolerance(Double.parseDouble(precursorMassToleranceString));

        //fragment mass tolerance
        String fragmentMassToleranceString = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.FRAGMENT_MASS_TOLERANCE);
        searchParameters.setFragMassTolerance(Double.parseDouble(fragmentMassToleranceString));

        //mass tolerance unit, same for precursor and fragment ions
        MassAccuracyType massAccuracyType = MassAccuracyType.PPM;
        Boolean massToleranceValue = Boolean.parseBoolean(mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.MASS_TOLERANCE_UNIT));
        if (!massToleranceValue) {
            massAccuracyType = MassAccuracyType.DA;
        }
        searchParameters.setPrecMassToleranceUnit(massAccuracyType);
        searchParameters.setFragMassToleranceUnit(massAccuracyType);

        //PSM and protein FDR
        searchParameters.setPsmThreshold(psmFdr);
        searchParameters.setProteinThreshold(proteinFdr);

        //enzyme
        String enzymes = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.ENZYMES);
        if (enzymes != null) {
            searchParameters.setEnzymes(enzymes);
        }

        //missed cleavages
        String missedCleavages = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.MAX_MISSED_CLEAVAGES);
        searchParameters.setNumberOfMissedCleavages(missedCleavages);

        //upper charge
        String upperCharge = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.MAX_CHARGE);
        searchParameters.setUpperCharge(Integer.parseInt(upperCharge));

        //add modifications
        searchParameters.getSearchParametersHasModifications().addAll(createModifications(searchParameters, rawFileName));

        //look for the given search parameter settings in the database
        searchParameters = searchAndValidationSettingsService.getSearchParameters(searchParameters);

        //set the search engine
        searchAndValidationSettings.setSearchEngine(searchAndValidationSettingsService.getSearchEngine(SearchEngineType.MAXQUANT, version));

        //set entity relations between SearchAndValidationSettings and SearchParameters
        searchAndValidationSettings.setSearchParameters(searchParameters);

        // TODO: 6/13/2016 change!
        //searchParameters.getSearchAndValidationSettingses().add(searchAndValidationSettings);
        return searchAndValidationSettings;
    }

    /**
     * Get the default search type from the database and assign it to the class
     * field.
     */
    @PostConstruct
    private void getDefaultSearchType() {
        //look for the default search type in the database
        TypedCvParam searchType = typedCvParamService.findByAccession(DEFAULT_SEARCH_TYPE_ACCESSION, CvParamType.SEARCH_TYPE);
        if (searchType != null) {
            defaultSearchType = (SearchCvParam) searchType;
        } else {
            throw new IllegalStateException("The default search type CV term was not found in the database.");
        }
    }

    /**
     * Create modifications for the given search parameter and the raw file that
     * linked to that parameter.
     *
     * @param searchParameters the search parameters
     * @param rawFileName      the RAW file name
     * @return list of SearchParametersHasModification objects
     */
    private List<SearchParametersHasModification> createModifications(SearchParameters searchParameters, String rawFileName) {
        List<SearchParametersHasModification> searchParametersHasModifications = new ArrayList<>();
        // retrieve fixed modification names
        String fixedModifications = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.FIXED_MODIFICATIONS);
        // retrieve the variable modification names
        String variableModifications = mqParParamsWithRawFile.get(rawFileName).get(MqParHeader.VARIABLE_MODIFICATIONS);

        if (fixedModifications != null && !fixedModifications.isEmpty()) {
            String[] modifications = fixedModifications.split(PARAMETER_DELIMITER);
            for (String modification : modifications) {
                SearchParametersHasModification searchParametersHasModification = createSearchParametersHasModification(searchParameters, modification, ModificationType.FIXED);

                searchParametersHasModifications.add(searchParametersHasModification);
            }
        }

        if (variableModifications != null && !variableModifications.isEmpty()) {
            String[] modifications = variableModifications.split(PARAMETER_DELIMITER);
            for (String modification : modifications) {
                SearchParametersHasModification searchParametersHasModification = createSearchParametersHasModification(searchParameters, modification, ModificationType.VARIABLE);

                searchParametersHasModifications.add(searchParametersHasModification);
            }
        }

        return searchParametersHasModifications;
    }

    /**
     * Create a {@link SearchParametersHasModification} instance.
     *
     * @param searchParameters
     * @return searchParametersHasModification instance
     */
    private SearchParametersHasModification createSearchParametersHasModification(SearchParameters searchParameters, String modificationName, ModificationType modificationType) {
        SearchParametersHasModification searchParametersHasModification = new SearchParametersHasModification();

        SearchModification searchModification;
        //look for the modification in the mapping file
        if (modificationMappings.containsKey(modificationName)) {
            OntologyTerm modificationTerm = modificationMappings.get(modificationName);
            searchModification = searchModificationMapper.mapByOntologyTerm(
                    modificationTerm.getOntologyPrefix(),
                    modificationTerm.getOboId(),
                    modificationTerm.getLabel());
        } else {
            searchModification = searchModificationMapper.mapByName(modificationName.split(MODIFICATION_NAME_ONLY)[0]);
        }

        searchParametersHasModification.setModificationType(modificationType);

        //set entity relation
        searchParametersHasModification.setSearchModification(searchModification);
        searchParametersHasModification.setSearchParameters(searchParameters);

        return searchParametersHasModification;
    }

    /**
     * Parse the mqpar.xml file and match with runs.
     *
     * @param mqParFile the mqpar.xml file path
     * @throws JDOMException in case of an problem occurring in one of the JDOM classes
     * @throws IOException   in case of an problem with the mqpar.xml file
     */
    private void parseMqParFile(Path mqParFile) throws JDOMException, IOException {
        mqParParamsWithRawFile.clear();
        analyticalRuns.clear();
        isobaricLabels.clear();
        labelMods.clear();
        psmFdr = null;
        proteinFdr = null;

        //create a map to hold raw files for each run (key: group index; value: raw file).
        Map<Integer, String> rawFilePath = new HashMap<>();
        //create a map to hold raw file groups for each run (key: group index; value: group number).
        Map<Integer, Integer> rawFileGroup = new HashMap<>();
        //create a map to hold enum map of spectrum parameters and their group(key: group number; value: enum map of spectrum parameters).
        Map<Integer, EnumMap<MqParHeader, String>> spectrumParamsWithGroup = new HashMap<>();
        //create a map to hold experiment names for each run (key: group index; value: experiment name).
        Map<Integer, String> experimentsName = new HashMap<>();

        Resource mqParResource = new FileSystemResource(mqParFile.toFile());
        SAXBuilder builder = new SAXBuilder();
        Document document;
        document = builder.build(mqParResource.getInputStream());

        Element root = document.getRootElement();
        //get the version
        Element versionElement = getChildByName(root, VERSION);
        version = versionElement.getText();

        //keep each raw file in a map (key: int, value: name of file).
        Element filePathsElement = getChildByName(root, FILE_PATHS);
        int counter = 0;
        for (Element filePathElement : filePathsElement.getChildren()) {
            rawFilePath.put(counter, FilenameUtils.getBaseName(filePathElement.getContent().get(0).getValue()));
            counter++;
        }

        Element analyticalRunNamesElement = getChildByName(root, EXPERIMENTS);
        counter = 0;
        for (Element analyticalRunNameElement : analyticalRunNamesElement.getChildren()) {
            if (!analyticalRunNameElement.getContent().isEmpty()) {
                experimentsName.put(counter, analyticalRunNameElement.getContent().get(0).getValue());
                counter++;
            } else {
                throw new IllegalStateException("Experiment name in mqpar file is empty.");
            }
        }

        // keep each raw file group number in a map (key: int; value: group number).
        Element rawFileGroupsElement = getChildByName(root, PARAM_GROUP_INDICES);
        counter = 0;
        for (Element rawFileGroupElement : rawFileGroupsElement.getChildren()) {
            int groupNo = Integer.parseInt(rawFileGroupElement.getContent().get(0).getValue());
            rawFileGroup.put(counter, groupNo);
            counter++;
        }

        //get the values for the different levels of FDR
        Element peptideFdrElement = getChildByName(root, mqParHeaders.get(MqParHeader.PEPTIDE_FDR));
        psmFdr = Double.valueOf(peptideFdrElement.getText());
        Element proteinFdrElement = getChildByName(root, mqParHeaders.get(MqParHeader.PROTEIN_FDR));
        proteinFdr = Double.valueOf(proteinFdrElement.getText());

        // keep each search parameters in a map (key: group number; value = maxQuantSpectrumParameterHeaders)
        Element parameterGroupsElement = getChildByName(root, PARAMETER_GROUPS);
        counter = 0;
        for (Element parameterGroupElement : parameterGroupsElement.getChildren()) {
            //create enumMap for mqpar.xml parameters (key: MqParHeader enum; value: parameter value).
            EnumMap<MqParHeader, String> mqParParameters = new EnumMap<>(MqParHeader.class);
            //iterate over the mandatory headers
            mqParHeaders.getMandatoryHeaders().forEach((mqParHeader) -> {
                Optional<String> foundHeader = mqParHeader.getValues().stream().filter(value -> parameterGroupElement.getChild(value) != null).findFirst();
                if (foundHeader.isPresent()) {
                    mqParHeader.setParsedValue(mqParHeader.getValues().indexOf(foundHeader.get()));
                    //if the Element has children (enzymes, fixed/variable modifications), concatenate them.
                    Element param = parameterGroupElement.getChild(foundHeader.get());
                    if (param.getChildren().isEmpty()) {
                        mqParParameters.put(MqParHeader.valueOf(mqParHeader.getName()), param.getValue());
                    } else {
                        //join the enzymes
                        String concatenation = getChildByName(parameterGroupElement, foundHeader.get()).getChildren().stream().map(enzyme
                                -> enzyme.getContent().get(0).getValue()).collect(Collectors.joining(PARAMETER_DELIMITER));
                        mqParParameters.put(MqParHeader.valueOf(mqParHeader.getName()), concatenation);
                    }
                }
            });

            spectrumParamsWithGroup.put(counter, mqParParameters);
            counter++;

            Element isobaricLabelsElement = getChildByName(parameterGroupElement, "isobaricLabels");
            int labelCounter = 0;
            for (Element isobaricLabelElement : isobaricLabelsElement.getChildren()) {
                if (!isobaricLabelElement.getContent().isEmpty()) {
                    isobaricLabels.put(labelCounter, isobaricLabelElement.getContent().get(0).getValue());
                } else {
                    isobaricLabels.put(labelCounter, null);
                }

                labelCounter++;
            }

            Element labelModsElement = getChildByName(parameterGroupElement, "labelMods");
            labelCounter = 0;
            for (Element labelModElement : labelModsElement.getChildren()) {
                if (!labelModElement.getContent().isEmpty()) {
                    labelMods.put(labelCounter, labelModElement.getContent().get(0).getValue());
                } else {
                    labelMods.put(labelCounter, null);
                }

                labelCounter++;
            }
        }

        // match all maps and put them in mqParParamsWithRawFile map
        // also put raw file name and experiment name in analyticalRuns
        rawFilePath.entrySet().forEach(entry -> {
            // find group number of each file name and using group number find the enumMap of spectrum parameters. Then put them in the map.
            mqParParamsWithRawFile.put(entry.getValue(), spectrumParamsWithGroup.get(rawFileGroup.get(entry.getKey())));
            // fill analyticalRuns
            AnalyticalRun analyticalRun = new AnalyticalRun();
            analyticalRun.setName(entry.getValue());
            analyticalRuns.put(analyticalRun, experimentsName.get(entry.getKey()));
        });
    }

    /**
     * Find child element by name, case insensitive. Returns null if nothing was
     * found.
     *
     * @param parent    the parent element
     * @param childName the child name
     * @return the found child element
     */
    private Element getChildByName(Element parent, String childName) {
        Element element = parent.getChild(childName);
        if (element == null) {
            for (Element e : parent.getChildren()) {
                String tagName = e.getName();
                if (tagName.equalsIgnoreCase(childName)) {
                    return e;
                }
            }
        }
        return element;
    }
}
