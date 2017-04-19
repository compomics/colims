package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.core.io.colims_to_utilities.ColimsPeptideMapper;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.ontology.ModificationOntologyTerm;
import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.ols.Ontology;
import com.compomics.colims.core.ontology.ols.OntologyTerm;
import com.compomics.colims.core.service.*;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.core.util.ResourceUtils;
import com.compomics.colims.core.util.SequenceUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.cv.*;
import com.compomics.colims.model.enums.*;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Role;
import uk.ac.ebi.jmzidml.model.mzidml.params.AdditionalSearchParamsCvParam;
import uk.ac.ebi.jmzidml.model.mzidml.params.AdditionalSearchParamsUserParam;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MzIdentML exporter class, populates models from the jmzidml library then uses
 * the MzIdentMLMarshaller to marshal them into valid XML.
 *
 * @author Iain
 * @author Niels Hulstaert
 */
@Component("mzIdentMlExporter")
public class MzIdentMlExporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzIdentMlExporter.class);

    private static final String MODIFICATION_ACCESSION_DELIMITER = ":";
    private static final String SEARCH_DB_ID = "SEARCHDB-%d";
    private static final String DB_SEQUENCE_ID = "DBSEQ-%d";
    private static final String PEPTIDE_ID = "PEP-%d";
    private static final String SPECTRUM_DATA_ID = "SPECDAT-%d";
    private static final String SPECTRUM_IDENTIFICATION_ID = "SI-%d";
    private static final String SPECTRUM_IDENTIFICATION_ITEM_ID = "SII-%d";
    private static final String SPECTRUM_IDENTIFICATION_RESULT_ID = "SIR-%d";
    private static final String SPECTRUM_IDENTIFICATION_LIST_ID = "SIL-%d";
    private static final String PEPTIDE_EVIDENCE_ID = "PE-%d";
    private static final String PROTEIN_DETECTION_LIST_ID = "PDL-%d";
    private static final String PROTEIN_DETECTION_ID = "PD-%d";
    private static final String PROTEIN_DETECTION_HYPOTHESIS_ID = "PDH-%d";
    private static final String PROTEIN_AMBIGUITY_GROUP_ID = "PAG-%d";
    private static final String MGF_SPECTRUM_INDEX = "Index=%d";
    private static final String RESIDUES_ANY = ".";
    private static final String RESIDUES_UNKNOWN = "N/A";
    private static final String NO_POST_PRE_AA = "-";

    @Value("${mzidentml.version}")
    private final String MZIDENTML_VERSION = "1.1.0";
    @Value("${colims-core.version}")
    private final String COLIMS_VERSION = "latest";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectReader objectReader = objectMapper.reader();
    private JsonNode ontologyTerms;
    /**
     * The MzIdentMlExport instance with the export resources.
     */
    private MzIdentMlExport mzIdentMlExport;
    /**
     * The used search engine.
     */
    private SearchEngine searchEngine;
    /**
     * The MzIdentML instance from the MzIdentML object model.
     */
    private MzIdentML mzIdentML;
    /**
     * The Inputs instance from the MzIdentML object model.
     */
    private Inputs inputs;
    /**
     * The CVs used in the MzIdentML file (key: CV reference; value: {@link Cv}
     * instance).
     */
    private final Map<String, Cv> cvs = new HashMap<>();
    /**
     * The map of protein accessions parsed from the used FASTA DB files.
     */
    private LinkedHashMap<FastaDb, Set<String>> proteinAccessions;
    /**
     * This map links the used {@link FastaDb} instances to their corresponding
     * {@link SearchDatabase} instances.
     */
    private final Map<FastaDb, SearchDatabase> fastaDbToSearchDatabases = new HashMap<>();
    /**
     * The MaxQuant to UNIMOD modification mappings.
     */
    private final Map<String, com.compomics.colims.core.ontology.OntologyTerm> modificationMappings;
    /**
     * The Ontology Lookup Service (OLS) service.
     */
    private final OlsService olsService;
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;
    private final FastaDbParser fastaDbParser;
    private final ProteinGroupService proteinGroupService;
    private final PeptideService peptideService;
    private final SpectrumService spectrumService;
    private final AnalyticalRunService analyticalRunService;
    private final ColimsPeptideMapper colimsPeptideMapper;
    private final ColimsSpectrumMapper colimsSpectrumMapper;

    @Autowired
    public MzIdentMlExporter(OlsService olsService, SearchAndValidationSettingsService searchAndValidationSettingsService,
                             FastaDbParser fastaDbParser,
                             ProteinGroupService proteinGroupService,
                             PeptideService peptideService,
                             SpectrumService spectrumService,
                             AnalyticalRunService analyticalRunService,
                             ColimsPeptideMapper colimsPeptideMapper,
                             ColimsSpectrumMapper colimsSpectrumMapper,
                             OntologyMapper ontologyMapper) {
        this.olsService = olsService;
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
        this.fastaDbParser = fastaDbParser;
        this.proteinGroupService = proteinGroupService;
        this.peptideService = peptideService;
        this.spectrumService = spectrumService;
        this.analyticalRunService = analyticalRunService;
        this.colimsPeptideMapper = colimsPeptideMapper;
        this.colimsSpectrumMapper = colimsSpectrumMapper;
        //get the modification mappings from the OntologyMapper
        modificationMappings = ontologyMapper.getMaxQuantMapping().getModifications();
    }

    /**
     * Read in the JSON file that contains mzIdentML related controlled
     * vocabulary terms.
     *
     * @throws IOException in case of a I/O related problem
     */
    @PostConstruct
    public void init() throws IOException {
        Resource ontologyMapping = ResourceUtils.getResourceByRelativePath("config/mzidentml-ontology-terms.json");
        ontologyTerms = objectReader.readTree(ontologyMapping.getInputStream());
    }

    /**
     * Export the given analytical runs in mzIdentML format.
     *
     * @param mzIdentMlExport the MzIdentMlExport instance
     * @throws IOException error thrown in case of a I/O related problem
     */
    public void export(MzIdentMlExport mzIdentMlExport) throws IOException {
        this.mzIdentMlExport = mzIdentMlExport;
        this.searchEngine = mzIdentMlExport.getAnalyticalRuns().get(0).getSearchAndValidationSettings().getSearchEngine();
        try (
                BufferedWriter mzIdentMlWriter = Files.newBufferedWriter(mzIdentMlExport.getMzIdentMlPath())) {
            OutputStream mgfOutputStream = null;
            if (mzIdentMlExport.getMgfPath() != null) {
                mgfOutputStream = Files.newOutputStream(mzIdentMlExport.getMgfPath());
            }
            //populate the mzIdentML object model
            populate(mgfOutputStream);

            if (mgfOutputStream != null) {
                mgfOutputStream.flush();
                mgfOutputStream.close();
            }

            MzIdentMLMarshaller mzIdentMLMarshaller = new MzIdentMLMarshaller();

            mzIdentMlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            mzIdentMlWriter.newLine();
            mzIdentMLMarshaller.marshal(mzIdentML, mzIdentMlWriter);

            clear();
        }
    }

    /**
     * Clear the resources of the exporter.
     */
    private void clear() {
        mzIdentML = null;
        proteinAccessions.clear();
        fastaDbToSearchDatabases.clear();
        cvs.clear();
        searchEngine = null;
        inputs = null;
        mzIdentMlExport = null;
    }

    /**
     * Assemble necessary data into an mzIdentML object and it's many
     * properties.
     *
     * @param mgfOutputStream the output stream for writing the spectra to.
     */
    private void populate(OutputStream mgfOutputStream) throws IOException {
        mzIdentML = new MzIdentML();

        mzIdentML.setId("Colims-" + COLIMS_VERSION);
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        CvList cvList = new CvList();
        mzIdentML.setCvList(cvList);

        AuditCollection auditCollection = populateAuditCollection();
        mzIdentML.setAuditCollection(auditCollection);

        Provider provider = populateProvider();
        mzIdentML.setProvider(provider);

        AnalysisSoftwareList analysisSoftwareList = populateAnalysisSoftwareList(auditCollection);
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList);

        DataCollection dataCollection = populateDataCollection();
        mzIdentML.setDataCollection(dataCollection);

        AnalysisSoftware analysisSoftware = analysisSoftwareList.getAnalysisSoftware().get(0);
        AnalysisProtocolCollection analysisProtocolCollection = populateAnalysisProtocolCollection(analysisSoftware);
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection);

        populateIdentificationData(mgfOutputStream);

        //add all the used CVs
        mzIdentML.getCvList().getCv().addAll(cvs.values());
    }

    /**
     * Populate the audit collection for associated entities, add the owner of
     * the run's project.
     */
    private AuditCollection populateAuditCollection() throws IOException {
        AuditCollection auditCollection = new AuditCollection();

        User owner = mzIdentMlExport.getUser();

        //create the owner person
        Person person = new Person();
        person.setId("PERSON_DOC_OWNER");
        person.setFirstName(owner.getFirstName());
        person.setLastName(owner.getLastName());

        CvParam email = getMzIdentMlElement("/Person/Email", CvParam.class);
        email.setValue(owner.getEmail());

        person.getCvParam().add(email);

        auditCollection.getPerson().add(person);

        if (owner.getInstitution() != null) {
            //create the owner organisation from the associated institution
            Institution institution = owner.getInstitution();

            Organization organization = new Organization();
            //getContact("LAB_PLACEHOLDER", Organization.class);
            organization.setId(institution.getName());

            CvParam address = getMzIdentMlElement("/Organization/Template/Address", CvParam.class);
            address.setValue(Arrays.stream(institution.getAddress()).collect(Collectors.joining(", ")));
            organization.getCvParam().add(address);

            CvParam name = getMzIdentMlElement("/Organization/Template/Name", CvParam.class);
            name.setValue(institution.getName());
            organization.getCvParam().add(name);

            if (institution.getEmail() != null && !institution.getEmail().isEmpty()) {
                CvParam institutionEmail = getMzIdentMlElement("/Organization/Template/Email", CvParam.class);
                institutionEmail.setValue(institution.getEmail());
                organization.getCvParam().add(institutionEmail);
            }

            if (institution.getUrl() != null && !institution.getUrl().isEmpty()) {
                CvParam url = getMzIdentMlElement("/Organization/Template/Url", CvParam.class);
                url.setValue(institution.getUrl());
                organization.getCvParam().add(url);
            }

            auditCollection.getOrganization().add(organization);

            //set the person's organisation affiliation
            Affiliation organisationAffiliation = new Affiliation();
            organisationAffiliation.setOrganization(organization);

            person.getAffiliation().add(organisationAffiliation);
        }

        return auditCollection;
    }

    /**
     * Populate the provider element.
     *
     * @return the provider MzIdentML element
     */
    private Provider populateProvider() throws IOException {
        Provider provider = new Provider();
        provider.setId("PROVIDER");

        provider.setContactRole(new ContactRole());

        provider.getContactRole().setContact(mzIdentML.getAuditCollection().getPerson().get(0));

        //set the researcher role
        Role role = new Role();
        role.setCvParam(getMzIdentMlElement("/Role/Researcher", CvParam.class));

        provider.getContactRole().setRole(role);

        return provider;
    }

    /**
     * Construct the list of analysis software.
     *
     * @param auditCollection the {@link AuditCollection} to add the software organization to
     * @return the populated {@link AnalysisSoftware} instance
     * @throws IOException in case of an JSON parsing related problem
     */
    private AnalysisSoftwareList populateAnalysisSoftwareList(AuditCollection auditCollection) throws IOException {
        AnalysisSoftwareList analysisSoftwareList = new AnalysisSoftwareList();

        AnalysisSoftware analysisSoftware = getMzIdentMlElement("/AnalysisSoftware/" + searchEngine.getName(), AnalysisSoftware.class);
        analysisSoftware.setVersion(searchEngine.getVersion());

        analysisSoftware.setSoftwareName(new Param());
        CvParam softwareName = getMzIdentMlElement("/AnalysisSoftwareCV/" + searchEngine.getName(), CvParam.class);
        analysisSoftware.getSoftwareName().setParam(softwareName);

        //create the software organisation
        Organization organization = new Organization();
        organization.setId(searchEngine.getName());
        List<CvParam> organizationCvParams = getChildMzIdentMlElements("/Organization/" + searchEngine.getName(), CvParam.class);
        organization.getCvParam().addAll(organizationCvParams);
        //and add it to the audit collection
        auditCollection.getOrganization().add(organization);

        ContactRole contactRole = new ContactRole();
        contactRole.setContact(organization);
        contactRole.setRole(new Role());
        contactRole.getRole().setCvParam(getMzIdentMlElement("/Role/Software vendor", CvParam.class));
        analysisSoftware.setContactRole(contactRole);

        analysisSoftwareList.getAnalysisSoftware().add(analysisSoftware);

        return analysisSoftwareList;
    }

    /**
     * Details of the data source for the experiment.
     *
     * @return the populated {@link DataCollection} object
     * @throws IOException in case of a JSON parsing related problem
     */
    private DataCollection populateDataCollection() throws IOException {
        DataCollection dataCollection = new DataCollection();

        inputs = new Inputs();
        dataCollection.setInputs(inputs);

        SearchAndValidationSettings searchAndValidationSettings = mzIdentMlExport.getAnalyticalRuns().get(0).getSearchAndValidationSettings();
        searchAndValidationSettingsService.fetchSearchSettingsHasFastaDb(searchAndValidationSettings);

        //parse the protein accessions from the FASTA DB files
        //order the used FASTA DB files by type, check their existence and pass them to the accession parser
        LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
        Arrays.stream(FastaDbType.values()).forEach(fastaDbType -> {
            List<FastaDb> fastaDbsByType = getFastaDbsByType(searchAndValidationSettings.getSearchSettingsHasFastaDbs(), fastaDbType);
            fastaDbsByType.forEach(fastaDb -> {
                //resolve the relative path against the FASTA DB directory and check if it exists
                Path fullFastaPath = mzIdentMlExport.getFastasDirectory().resolve(fastaDb.getFilePath());
                if (!Files.exists(fullFastaPath)) {
                    throw new IllegalArgumentException("The FASTA DB file with path " + fullFastaPath.toString() + " cannot be found.");
                }
                //
                fastaDbs.put(fastaDb, fullFastaPath);
            });
        });
        proteinAccessions = fastaDbParser.parseAccessions(fastaDbs, searchEngine.getSearchEngineType());

        //iterate over the different FASTA databases used for the searches and map them to SearchDatabase instances
        for (int i = 0; i < searchAndValidationSettings.getSearchSettingsHasFastaDbs().size(); i++) {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = searchAndValidationSettings.getSearchSettingsHasFastaDbs().get(i);
            FastaDb fastaDb = searchSettingsHasFastaDb.getFastaDb();

            SearchDatabase searchDatabase = populateSearchDatabase(fastaDb, i);

            //NOTE: if decoy database used then cv param should be child of MS:1001450 here
            if (fastaDb.getDatabaseName() != null && !fastaDb.getDatabaseName().isEmpty()) {
                searchDatabase.setDatabaseName(new Param());
                UserParam databaseName = new UserParam();
                databaseName.setName(fastaDb.getDatabaseName());
                searchDatabase.getDatabaseName().setParam(databaseName);
            }

            if (fastaDb.getTaxonomy() != null) {
                Cv cv = new Cv();
                cv.setId(fastaDb.getTaxonomy().getLabel());
                boolean updated = updateCvList(cv.getId());

                if (updated) {
                    CvParam taxonomy = new CvParam();
                    taxonomy.setCv(cv);
                    taxonomy.setAccession(fastaDb.getTaxonomy().getAccession());
                    taxonomy.setName(fastaDb.getTaxonomy().getName());

                    //add the taxonomy to the search database
                    searchDatabase.getCvParam().add(taxonomy);
                }
            }

            fastaDbToSearchDatabases.put(fastaDb, searchDatabase);
            inputs.getSearchDatabase().add(searchDatabase);
        }

        dataCollection.setAnalysisData(new AnalysisData());

        return dataCollection;
    }

    /**
     * Update the CV list if the given CV reference is not present.
     *
     * @param cvRef the CV reference String
     * @return true if the CV was added or already present, false if the CV was not found by it's reference
     */
    private boolean updateCvList(String cvRef) throws IOException {
        boolean present = true;

        if (!cvs.containsKey(cvRef)) {
            Cv cv = getMzIdentMlElement("/CvList/" + cvRef, Cv.class);

            if (cv == null) {
                //look up the ontology with the OLS service
                List<String> namespaces = new ArrayList<>();
                namespaces.add(cvRef.toLowerCase());
                List<Ontology> ontologies = olsService.getOntologiesByNamespace(namespaces);
                if (!ontologies.isEmpty()) {
                    Ontology ontology = ontologies.get(0);
                    cv = new Cv();
                    cv.setId(ontology.getPrefix());
                    cv.setFullName(ontology.getTitle());
                    cv.setUri(ontology.getIdUrl());
                }
            }

            if (cv != null) {
                //add it to the used CVs
                cvs.put(cvRef, cv);
            } else {
                present = false;
            }
        }

        return present;
    }

    /**
     * Populate a {@link SearchDatabase} instance
     *
     * @param fastaDb the {@link FastaDb} instance
     * @param id      the search database ID
     * @return the populated search database instance
     * @throws IOException in case of a JSON parsing related problem
     */
    private SearchDatabase populateSearchDatabase(FastaDb fastaDb, int id) throws IOException {
        SearchDatabase searchDatabase = new SearchDatabase();

        searchDatabase.setId(String.format(SEARCH_DB_ID, id));
        searchDatabase.setLocation(fastaDb.getFilePath());
        searchDatabase.setNumDatabaseSequences((long) proteinAccessions.get(fastaDb).size());

        Param databaseNameParam = new Param();
        UserParam databaseName = new UserParam();
        if (fastaDb.getDatabaseName() != null) {
            databaseName.setName(fastaDb.getDatabaseName());
        } else {
            databaseName.setName(fastaDb.getName());
        }
        databaseNameParam.setParam(databaseName);
        searchDatabase.setDatabaseName(databaseNameParam);

        if (fastaDb.getVersion() != null) {
            searchDatabase.setVersion(fastaDb.getVersion());
        }
        searchDatabase.setFileFormat(new FileFormat());
        searchDatabase.getFileFormat().setCvParam(getMzIdentMlElement("/FileFormat/FASTA", CvParam.class));
        searchDatabase.getCvParam().add(getMzIdentMlElement("/SearchDatabase/Type", CvParam.class));

        return searchDatabase;
    }

    /**
     * Get the {@link FastaDb} instances by type from the list of
     * {@link SearchSettingsHasFastaDb} instances.
     *
     * @param searchSettingsHasFastaDbs the list of searchSettingsHasFastaDbs instances
     * @param fastaDbType               the FASTA DB type
     * @return the list of {@link FastaDb} instances with the given type
     */
    private List<FastaDb> getFastaDbsByType(List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs, FastaDbType fastaDbType) {
        List<FastaDb> fastaDbs = new ArrayList<>();

        for (SearchSettingsHasFastaDb searchSettingsHasFastaDb : searchSettingsHasFastaDbs) {
            if (searchSettingsHasFastaDb.getFastaDbType() == fastaDbType) {
                fastaDbs.add(searchSettingsHasFastaDb.getFastaDb());
            }
        }

        return fastaDbs;
    }

    /**
     * Gather all data relating to search and protein protocol settings.
     *
     * @param analysisSoftware the analysis software
     * @return the populated {@link AnalysisProtocolCollection} object
     */
    private AnalysisProtocolCollection populateAnalysisProtocolCollection(AnalysisSoftware analysisSoftware) throws IOException {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        SearchAndValidationSettings settings = mzIdentMlExport.getAnalyticalRuns().get(0).getSearchAndValidationSettings();
        SearchParameters searchParameters = settings.getSearchParameters();

        SpectrumIdentificationProtocol spectrumIdentificationProtocol = new SpectrumIdentificationProtocol();

        //set analysis software and search type
        spectrumIdentificationProtocol.setId("SP-1");
        spectrumIdentificationProtocol.setAnalysisSoftware(analysisSoftware);
        spectrumIdentificationProtocol.setSearchType(new Param());
        spectrumIdentificationProtocol.getSearchType().setParam(getMzIdentMlElement("/SearchType/MS-MS", CvParam.class));

        //set the threshold values for the PSM and peptide level (if provided)
        spectrumIdentificationProtocol.setThreshold(new ParamList());
        if (searchParameters.getScoreType() == ScoreType.FDR && searchParameters.getPsmThreshold() != null) {
            CvParam threshold = getMzIdentMlElement("/Threshold/PSM FDR", CvParam.class);
            threshold.setValue(String.valueOf(MathUtils.round(searchParameters.getPsmThreshold(), 2)));
            spectrumIdentificationProtocol.getThreshold().getCvParam().add(threshold);
        }
        if (searchParameters.getScoreType() == ScoreType.FDR && searchParameters.getPeptideThreshold() != null) {
            CvParam threshold = getMzIdentMlElement("/Threshold/Peptide FDR", CvParam.class);
            threshold.setValue(String.valueOf(MathUtils.round(searchParameters.getPeptideThreshold(), 2)));
            spectrumIdentificationProtocol.getThreshold().getCvParam().add(threshold);
        }

        //populate the search modification parameters
        if (searchParameters.getSearchParametersHasModifications().size() > 0) {
            spectrumIdentificationProtocol.setModificationParams(new ModificationParams());

            for (SearchParametersHasModification searchParametersHasModification : searchParameters.getSearchParametersHasModifications()) {
                SearchModification colimsSearchModification = searchParametersHasModification.getSearchModification();

                uk.ac.ebi.jmzidml.model.mzidml.SearchModification mzSearchModification = new uk.ac.ebi.jmzidml.model.mzidml.SearchModification();
                mzSearchModification.setFixedMod(searchParametersHasModification.getModificationType() == ModificationType.FIXED);
                mzSearchModification.setMassDelta(colimsSearchModification.getAverageMassShift().floatValue());

                CvParam modificationParam = modificationToCvParam(colimsSearchModification);
                mzSearchModification.getCvParam().add(modificationParam);

                if (colimsSearchModification.getUtilitiesName() != null) {
                    PTM ptm = PTMFactory.getInstance().getPTM(colimsSearchModification.getUtilitiesName());
                    AminoAcidPattern pattern = ptm.getPattern();
                    if (pattern != null) {
                        mzSearchModification.getResidues().add(pattern.toString());
                    } else {
                        mzSearchModification.getResidues().add(RESIDUES_ANY);
                    }
                } else {
                    //check for residues in the MaxQuant modification mappings
                    //by filtering on accession
                    if (searchEngine.getSearchEngineType().equals(SearchEngineType.MAXQUANT)) {
                        Optional<com.compomics.colims.core.ontology.OntologyTerm> foundModification = modificationMappings.values().stream().filter(ontologyTerm -> ontologyTerm.getOboId().equals(colimsSearchModification.getAccession())).findAny();
                        if (foundModification.isPresent()) {
                            mzSearchModification.getResidues().add(((ModificationOntologyTerm) foundModification.get()).getAffectedAminoAcids());
                        } else {
                            mzSearchModification.getResidues().add(RESIDUES_UNKNOWN);
                        }
                    } else {
                        mzSearchModification.getResidues().add(RESIDUES_UNKNOWN);
                    }
                }

                spectrumIdentificationProtocol.getModificationParams().getSearchModification().add(mzSearchModification);
            }
        }

        //populate the enzyme section
        spectrumIdentificationProtocol.setEnzymes(new Enzymes());
        String colimsEnzymes = searchParameters.getEnzymes();
        if (colimsEnzymes != null) {
            //split the enzymes
            String[] enzymes = colimsEnzymes.split(SearchParameters.DELIMITER);
            //split the number of missedCleavages
            String[] missedCleavages = searchParameters.getNumberOfMissedCleavages().split(";");
            for (int i = 0; i < enzymes.length; i++) {
                CvParam cvEnzyme = getEnzymeByName(enzymes[i]);
                if (cvEnzyme == null) {
                    //set the enzyme as "unknown"
                    cvEnzyme = getMzIdentMlElement("/Unknown param", CvParam.class);
                }
                Enzyme mzEnzyme = new Enzyme();
                mzEnzyme.setId("ENZYME-" + enzymes[i]);
                mzEnzyme.setEnzymeName(new ParamList());
                mzEnzyme.setMissedCleavages(Integer.parseInt(missedCleavages[i]));
                mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);

                spectrumIdentificationProtocol.getEnzymes().getEnzyme().add(mzEnzyme);
            }
        } else {
            CvParam cvEnzyme = getMzIdentMlElement("/Enzyme/No cleavage", CvParam.class);

            Enzyme mzEnzyme = new Enzyme();
            mzEnzyme.setId("ENZYME-" + cvEnzyme.getName());
            mzEnzyme.setEnzymeName(new ParamList());
            mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);

            spectrumIdentificationProtocol.getEnzymes().getEnzyme().add(mzEnzyme);
        }

        //add the mass unit ontology
        updateCvList("UO");
        //populate the fragment tolerance elements
        CvParam fragmentMinus = getMzIdentMlElement("/Tolerance/Minus", CvParam.class);
        fragmentMinus.setValue(searchParameters.getFragMassTolerance().toString());
        addMassUnit(fragmentMinus, searchParameters.getFragMassToleranceUnit());
        fragmentMinus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        CvParam fragmentPlus = getMzIdentMlElement("/Tolerance/Plus", CvParam.class);
        fragmentPlus.setValue(searchParameters.getFragMassTolerance().toString());
        addMassUnit(fragmentPlus, searchParameters.getFragMassToleranceUnit());
        fragmentPlus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        spectrumIdentificationProtocol.setFragmentTolerance(new Tolerance());

        spectrumIdentificationProtocol.getFragmentTolerance().getCvParam().add(fragmentMinus);
        spectrumIdentificationProtocol.getFragmentTolerance().getCvParam().add(fragmentPlus);

        //populate the precursor tolerance elements
        CvParam parentMinus = getMzIdentMlElement("/Tolerance/Minus", CvParam.class);
        parentMinus.setValue(searchParameters.getPrecMassTolerance().toString());
        addMassUnit(parentMinus, searchParameters.getPrecMassToleranceUnit());
        parentMinus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString());

        CvParam parentPlus = getMzIdentMlElement("/Tolerance/Plus", CvParam.class);
        parentPlus.setValue(searchParameters.getPrecMassTolerance().toString());
        addMassUnit(fragmentPlus, searchParameters.getPrecMassToleranceUnit());
        fragmentPlus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString());
        spectrumIdentificationProtocol.setParentTolerance(new Tolerance());

        spectrumIdentificationProtocol.getParentTolerance().getCvParam().add(parentMinus);
        spectrumIdentificationProtocol.getParentTolerance().getCvParam().add(parentPlus);

        collection.getSpectrumIdentificationProtocol().add(spectrumIdentificationProtocol);

        //populate the protein detection protocol
        ProteinDetectionProtocol proteinDetectionProtocol = new ProteinDetectionProtocol();
        proteinDetectionProtocol.setAnalysisSoftware(analysisSoftware);
        proteinDetectionProtocol.setId("PDP-1");
        CvParam proteinThreshold;
        switch (searchParameters.getScoreType()) {
            case FDR:
                proteinThreshold = getMzIdentMlElement("/Threshold/Protein FDR", CvParam.class);
                break;
            case CONFIDENCE:
                proteinThreshold = getMzIdentMlElement("/Threshold/Protein confidence", CvParam.class);
                break;
            case FNR:
                proteinThreshold = getMzIdentMlElement("/Threshold/Protein FNR", CvParam.class);
                break;
            default:
                proteinThreshold = getMzIdentMlElement("/Threshold/No threshold", CvParam.class);
                break;
        }
        proteinDetectionProtocol.setThreshold(new ParamList());
        if (searchParameters.getProteinThreshold() != null) {
            proteinThreshold.setValue(String.valueOf(MathUtils.round(searchParameters.getProteinThreshold(), 2)));
        }
        proteinDetectionProtocol.getThreshold().getCvParam().add(proteinThreshold);
        collection.setProteinDetectionProtocol(proteinDetectionProtocol);

        //add additional search parameters for the instrument('s) type and name
        ParamList additionalSearchParams = new ParamList();
        spectrumIdentificationProtocol.setAdditionalSearchParams(additionalSearchParams);
        //fetch the instrument(s)
        mzIdentMlExport.getAnalyticalRuns().forEach(analyticalRun -> analyticalRunService.fetchInstrument(analyticalRun));
        //filter out duplicate IDs
        Map<Long, Instrument> instruments = mzIdentMlExport.getAnalyticalRuns()
                .stream()
                .map(analyticalRun -> analyticalRun.getInstrument())
                .collect(Collectors.toMap(Instrument::getId, Function.identity(), (instrument1, instrument2) -> instrument1));
        int instrumentIndex = 1;
        for (Instrument instrument : instruments.values()) {
            AdditionalSearchParamsUserParam instrumentNameUserParam = new AdditionalSearchParamsUserParam();
            instrumentNameUserParam.setName("Instrument " + instrumentIndex + " name");
            instrumentNameUserParam.setValue(instrument.getName());

            additionalSearchParams.getUserParam().add(instrumentNameUserParam);

            instrumentIndex++;

            //set the instrument type as a an additional search CV param
            InstrumentCvParam instrumentType = instrument.getType();
            AdditionalSearchParamsCvParam instrumentTypeCvParam = new AdditionalSearchParamsCvParam();
            instrumentTypeCvParam.setAccession(instrumentType.getAccession());
            instrumentTypeCvParam.setName(instrumentType.getName());
            updateCvList(instrumentType.getLabel());
            instrumentTypeCvParam.setCv(cvs.get(instrumentType.getLabel()));

            additionalSearchParams.getCvParam().add(instrumentTypeCvParam);
        }

        return collection;
    }

    /**
     * Add the given {@link MassAccuracyType} to the {@link CvParam}.
     *
     * @param cvParam          the CV param instance
     * @param massAccuracyType the mass accuracy
     * @throws IOException in case of an JSON parsing related problem
     */
    private void addMassUnit(CvParam cvParam, MassAccuracyType massAccuracyType) throws IOException {
        switch (massAccuracyType) {
            case DA:
                cvParam.setUnitName("dalton");
                cvParam.setUnitAccession("UO:0000221");
                cvParam.setUnitCv(getMzIdentMlElement("/CvList/UO", Cv.class));
                break;
            case PPM:
                cvParam.setUnitName("parts per million");
                cvParam.setUnitAccession("UO:0000169");
                cvParam.setUnitCv(getMzIdentMlElement("/CvList/UO", Cv.class));
                break;
            default:
                break;
        }
    }

    /**
     * Add a seconds time uint the the given {@link CvParam}.
     *
     * @param cvParam the CV param instance
     * @throws IOException in case of an JSON parsing related problem
     */
    private void addTimeUnit(CvParam cvParam) throws IOException {
        cvParam.setUnitName("second");
        cvParam.setUnitAccession("UO:0000010");
        cvParam.setUnitCv(getMzIdentMlElement("/CvList/UO", Cv.class));
    }

    /**
     * Get the enzyme CvParam by it's name.
     *
     * @param colimsEnzymeName the Colims enzyme name
     * @return the populated CvParam instance, null if nothing could be found
     * @throws IOException in case of a JSON parsing related problem
     */
    private CvParam getEnzymeByName(String colimsEnzymeName) throws IOException {
        CvParam enzyme;

        //safety check for enzymes name with a slash in them
        colimsEnzymeName = colimsEnzymeName.replace("/", "~1");
        enzyme = getMzIdentMlElement("/Enzyme/" + colimsEnzymeName, CvParam.class);
        if (enzyme == null) {
            //look for the enzyme with the OLS service
            OntologyTerm olsEnzyme = olsService.findEnzymeByName(colimsEnzymeName);
            if (olsEnzyme != null) {
                Cv cv = new Cv();
                cv.setId(olsEnzyme.getOntologyPrefix());
                enzyme.setCv(cv);
                enzyme.setName(olsEnzyme.getLabel());
                enzyme.setAccession(olsEnzyme.getOboId());
            }
        }

        return enzyme;
    }

    /**
     * Iterate over the identification data associated with the runs and
     * populate the necessary objects.
     *
     * @param mgfOutputStream the output stream for writing the spectra to.
     * @throws IOException in case of a JSON parsing related problem
     */
    private void populateIdentificationData(OutputStream mgfOutputStream) throws IOException {
        //set up the spectrum identification list
        SpectrumIdentificationList spectrumIdentificationList = setupSpectrumIdentificationList();

        //set up the spectra data
        SpectraData spectraData = setupSpectraData();

        //set up the spectrum identification
        SpectrumIdentification spectrumIdentification = setupSpectrumIdentification(spectraData, spectrumIdentificationList);

        //map the keep track of spectrum ID-SpectrumIdentificationResult associations (to keep track of chimeric spectra)
        Map<Long, SpectrumIdentificationResult> spectrumIdentificationResults = new HashMap<>();

        //set up the protein detection list
        ProteinDetectionList proteinDetectionList = new ProteinDetectionList();
        proteinDetectionList.setId(String.format(PROTEIN_DETECTION_LIST_ID, 1));
        mzIdentML.getDataCollection().getAnalysisData().setProteinDetectionList(proteinDetectionList);

        //set up the sequence collection
        SequenceCollection sequenceCollection = new SequenceCollection();
        mzIdentML.setSequenceCollection(sequenceCollection);

        //set up the protein detection
        ProteinDetection proteinDetection = setupProteinDetection(proteinDetectionList, spectrumIdentificationList);

        //set up the analysis collection
        AnalysisCollection analysisCollection = new AnalysisCollection();
        analysisCollection.getSpectrumIdentification().add(spectrumIdentification);
        analysisCollection.setProteinDetection(proteinDetection);
        mzIdentML.setAnalysisCollection(analysisCollection);

        //map to keep track of protein ID - DB sequence pairs
        Map<String, DBSequence> dbSequencess = new HashMap<>();

        //keep track of the spectrum index for the MGF file writing
        Long spectrumIndex = 0L;

        //keep track of the peptide DTOs - mzIdentML peptide pairs that represent the "unique" peptides;
        //same sequence, same modifications
        Map<PeptideDTO, uk.ac.ebi.jmzidml.model.mzidml.Peptide> uniquePeptides = new HashMap<>();
        //get the protein groups associated with the analytical runs
        List<Long> runIds = mzIdentMlExport.getAnalyticalRuns().stream().map(AnalyticalRun::getId).collect(Collectors.toList());
        List<ProteinGroup> proteinGroups = proteinGroupService.getProteinGroupsForRuns(runIds);
        //iterate over the protein groups
        for (ProteinGroup proteinGroup : proteinGroups) {
            //create an ambiguity group
            ProteinAmbiguityGroup proteinAmbiguityGroup = new ProteinAmbiguityGroup();
            proteinAmbiguityGroup.setId(String.format(PROTEIN_AMBIGUITY_GROUP_ID, proteinGroup.getId()));

            //iterate over the proteins in the protein group,
            //add the protein sequence to the sequence collection and
            //add a protein hypothesis to the protein ambiguity group
            ProteinDetectionHypothesis mainProteinDetectionHypothesis = null;
            String mainSequence = null;
            for (ProteinGroupHasProtein proteinGroupHasProtein : proteinGroup.getProteinGroupHasProteins()) {
                Protein protein = proteinGroupHasProtein.getProtein();

                DBSequence dbSequence;
                if (!dbSequencess.containsKey(proteinGroupHasProtein.getProteinAccession())) {
                    dbSequence = populateDBSequence(proteinGroupHasProtein.getProteinAccession(), protein.getSequence(), (long) dbSequencess.size());
                    sequenceCollection.getDBSequence().add(dbSequence);

                    dbSequencess.put(proteinGroupHasProtein.getProteinAccession(), dbSequence);
                } else {
                    dbSequence = dbSequencess.get(proteinGroupHasProtein.getProteinAccession());
                }

                //create a protein detection hypothesis
                ProteinDetectionHypothesis proteinDetectionHypothesis = new ProteinDetectionHypothesis();
                proteinDetectionHypothesis.setDBSequence(dbSequence);
                //TODO: 5/01/17 what to do with this threshold
                proteinDetectionHypothesis.setPassThreshold(true);
                proteinDetectionHypothesis.setId(String.format(PROTEIN_DETECTION_HYPOTHESIS_ID, proteinGroupHasProtein.getId()));

                //add some CV params for the main protein only
                if (proteinGroupHasProtein.getIsMainGroupProtein()) {
                    mainProteinDetectionHypothesis = proteinDetectionHypothesis;
                    mainSequence = dbSequence.getSeq();
                    CvParam groupRepresentative = getMzIdentMlElement("/Protein/Group representative", CvParam.class);
                    proteinDetectionHypothesis.getCvParam().add(groupRepresentative);
                    CvParam leadingProtein = getMzIdentMlElement("/Protein/Leading protein", CvParam.class);
                    proteinDetectionHypothesis.getCvParam().add(leadingProtein);
                }

                //add to the protein ambiguity group
                proteinAmbiguityGroup.getProteinDetectionHypothesis().add(proteinDetectionHypothesis);
            }
            //add the protein scores
            addProteinScores(proteinGroup, proteinAmbiguityGroup);

            //add the protein ambiguity group to the protein detection list
            proteinDetectionList.getProteinAmbiguityGroup().add(proteinAmbiguityGroup);

            //get the peptide DTOs associated with this protein group
            List<PeptideDTO> peptideDTOs = peptideService.getPeptideDTOs(proteinGroup.getId(), runIds);
            List<UniqueEvidence> uniqueEvidences = new ArrayList<>();
            Set<String> peptideSequences = new HashSet<>();
            //iterate over the protein group peptide DTOs
            for (PeptideDTO peptideDTO : peptideDTOs) {
                Peptide colimsPeptide = peptideDTO.getPeptide();
                uk.ac.ebi.jmzidml.model.mzidml.Peptide mzPeptide;

                //check if a similar peptide (same sequence, modifications) is already present
                if (!uniquePeptides.containsKey(peptideDTO)) {
                    mzPeptide = new uk.ac.ebi.jmzidml.model.mzidml.Peptide();
                    mzPeptide.setId(String.format(PEPTIDE_ID, colimsPeptide.getId()));
                    mzPeptide.setPeptideSequence(colimsPeptide.getSequence());

                    //add the peptide modifications
                    for (PeptideHasModification peptideHasMod : colimsPeptide.getPeptideHasModifications()) {
                        mzPeptide.getModification().add(populateModification(peptideHasMod));
                    }

                    //add to the sequence collection
                    sequenceCollection.getPeptide().add(mzPeptide);

                    //add to the "unique" peptides
                    uniquePeptides.put(peptideDTO, mzPeptide);

                    peptideSequences.add(mzPeptide.getPeptideSequence());
                } else {
                    mzPeptide = uniquePeptides.get(peptideDTO);
                }

                //get the peptide's spectrum
                Spectrum spectrum = colimsPeptide.getSpectrum();

                SpectrumIdentificationItem spectrumIdentificationItem;
                //check if the spectrum is a matching-between-runs one (MaxQuant only)
                if (searchEngine.getSearchEngineType().equals(SearchEngineType.MAXQUANT) && spectrum.getAccession().equals(Spectrum.MBR_SPECTRUM_ACCESSION)) {
                    spectrumIdentificationItem = populateMbrSpectrumIdentificationItem(colimsPeptide);
                } else {
                    if (mgfOutputStream != null) {
                        spectrumService.fetchSpectrumFiles(spectrum);
                        //iterate over the spectrum files
                        for (SpectrumFile spectrumFile : spectrum.getSpectrumFiles()) {
                            byte[] unzippedBytes = IOUtils.unzip(spectrumFile.getContent());
                            mgfOutputStream.write(unzippedBytes);
                            spectrumIndex++;
                        }
                    }

                    spectrumIdentificationItem = populateSpectrumIdentificationItem(spectrum, colimsPeptide);
                }
                spectrumIdentificationItem.setPeptide(mzPeptide);

                //calculate peptide location values
                //more than one position is possible
                //iterate over the proteins in the protein group
                for (int i = 0; i < proteinGroup.getProteinGroupHasProteins().size(); i++) {
                    Protein protein = proteinGroup.getProteinGroupHasProteins().get(i).getProtein();
                    try {
                        List<PeptidePosition> peptidePositions = SequenceUtils.getPeptidePositions(protein.getSequence(), colimsPeptide.getSequence());

                        for (PeptidePosition peptidePosition : peptidePositions) {
                            PeptideEvidence peptideEvidence;
                            PeptideHypothesis peptideHypothesis;
                            UniqueEvidence uniqueEvidence = new UniqueEvidence(proteinGroup.getProteinGroupHasProteins().get(i).getProteinAccession(), peptideDTO, peptidePosition);
                            int index = uniqueEvidences.indexOf(uniqueEvidence);
                            if (index == -1) {
                                DBSequence dbSequence = dbSequencess.get(proteinGroup.getProteinGroupHasProteins().get(i).getProteinAccession());

                                peptideEvidence = populatePeptideEvidence(dbSequence, sequenceCollection.getPeptideEvidence().size(), mzPeptide, peptidePosition);

                                sequenceCollection.getPeptideEvidence().add(peptideEvidence);

                                peptideHypothesis = new PeptideHypothesis();
                                peptideHypothesis.setPeptideEvidence(peptideEvidence);

                                proteinAmbiguityGroup.getProteinDetectionHypothesis().get(i).getPeptideHypothesis().add(peptideHypothesis);

                                uniqueEvidence.setPeptideEvidence(peptideEvidence);
                                uniqueEvidence.setPeptideHypothesis(peptideHypothesis);

                                //add to the unique evidences
                                uniqueEvidences.add(uniqueEvidence);
                            } else {
                                peptideEvidence = uniqueEvidences.get(index).getPeptideEvidence();
                                peptideHypothesis = uniqueEvidences.get(index).getPeptideHypothesis();
                            }
                            //add the peptide evidence ref to the spectrum identification item
                            PeptideEvidenceRef evidenceRef = new PeptideEvidenceRef();
                            evidenceRef.setPeptideEvidence(peptideEvidence);
                            spectrumIdentificationItem.getPeptideEvidenceRef().add(evidenceRef);

                            //add the spectrum identification item ref to the peptide hypothesis
                            SpectrumIdentificationItemRef spectrumIdentificationItemRef = new SpectrumIdentificationItemRef();
                            spectrumIdentificationItemRef.setSpectrumIdentificationItem(spectrumIdentificationItem);
                            peptideHypothesis.getSpectrumIdentificationItemRef().add(spectrumIdentificationItemRef);
                        }
                    } catch (IllegalStateException ex) {
                        LOGGER.warn(ex.getMessage(), ex);
                    }
                }

                SpectrumIdentificationResult spectrumIdentificationResult;
                //check whether the spectrum already has an SpectrumIdentificationResult associated with it
                if (!spectrumIdentificationResults.containsKey(spectrum.getId())) {
                    spectrumIdentificationResult = populateSpectrumIdentificationResult(spectrum, spectraData, spectrumIndex);
                    spectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
                    spectrumIdentificationList.getSpectrumIdentificationResult().add(spectrumIdentificationResult);

                    //add to the map
                    spectrumIdentificationResults.put(spectrum.getId(), spectrumIdentificationResult);
                }
                //otherwise it's a peptide linked to multiple protein groups or a chimeric spectrum
                else {
                    SpectrumIdentificationResult existingSpectrumIdentificationResult = spectrumIdentificationResults.get(spectrum.getId());
                    //check if an existing spectrum identification item has the same ID
                    Optional<SpectrumIdentificationItem> foundSpectrumIdentificationItem = existingSpectrumIdentificationResult.getSpectrumIdentificationItem()
                            .stream()
                            .filter(existingSpectrumIdentificationItem -> existingSpectrumIdentificationItem.getId().equals(spectrumIdentificationItem.getId()))
                            .findAny();
                    if (foundSpectrumIdentificationItem.isPresent()) {
                        foundSpectrumIdentificationItem.get().getPeptideEvidenceRef().addAll(spectrumIdentificationItem.getPeptideEvidenceRef());
                    } else {
                        existingSpectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
                    }
                }
            }

            //calculate the protein sequence coverage and add it to the main group protein
            CvParam sequenceCoverage = getMzIdentMlElement("/Protein/Sequence coverage", CvParam.class);
            //calculate the sequence coverage for the main group protein
            double coverage = SequenceUtils.calculateProteinCoverage(mainSequence, peptideSequences);
            sequenceCoverage.setValue(Double.toString(MathUtils.round(coverage, 2)));
            mainProteinDetectionHypothesis.getCvParam().add(sequenceCoverage);
        }
    }

    /**
     * Populate a peptide evidence instance.
     *
     * @param dbSequence      the {@link DBSequence} instance
     * @param id              the peptide evidence ID
     * @param mzPeptide       the {@link uk.ac.ebi.jmzidml.model.mzidml.Peptide} instance
     * @param peptidePosition the {@link PeptidePosition} instance
     * @return the populated {@link PeptideEvidence} instance
     */
    private PeptideEvidence populatePeptideEvidence(DBSequence dbSequence, int id, uk.ac.ebi.jmzidml.model.mzidml.Peptide mzPeptide, PeptidePosition peptidePosition) {
        PeptideEvidence peptideEvidence = new PeptideEvidence();

        peptideEvidence.setDBSequence(dbSequence);
        peptideEvidence.setPeptide(mzPeptide);
        peptideEvidence.setId(String.format(PEPTIDE_EVIDENCE_ID, id));

        peptideEvidence.setStart(peptidePosition.getStartPosition());
        peptideEvidence.setEnd(peptidePosition.getEndPosition());
        if (peptidePosition.getPreAA() != null) {
            peptideEvidence.setPre(peptidePosition.getPreAA().toString());
        } else {
            peptideEvidence.setPre(NO_POST_PRE_AA);
        }
        if (peptidePosition.getPostAA() != null) {
            peptideEvidence.setPost(peptidePosition.getPostAA().toString());
        } else {
            peptideEvidence.setPost(NO_POST_PRE_AA);
        }

        return peptideEvidence;
    }

    /**
     * Set up the spectrum identification list.
     *
     * @return the {@link SpectrumIdentificationList} instance
     */
    private SpectrumIdentificationList setupSpectrumIdentificationList() {
        SpectrumIdentificationList spectrumIdentificationList = new SpectrumIdentificationList();
        spectrumIdentificationList.setId(String.format(SPECTRUM_IDENTIFICATION_LIST_ID, 1));

        mzIdentML.getDataCollection().getAnalysisData().getSpectrumIdentificationList().add(spectrumIdentificationList);

        //calculate the number of sequences searched
        long numberOfSequencesSearched = 0;
        for (Map.Entry<FastaDb, SearchDatabase> entry : fastaDbToSearchDatabases.entrySet()) {
            numberOfSequencesSearched += proteinAccessions.get(entry.getKey()).size();
        }
        spectrumIdentificationList.setNumSequencesSearched(numberOfSequencesSearched);

        return spectrumIdentificationList;
    }

    /**
     * Set up the spectrum identification.
     *
     * @param spectraData                the spectra data
     * @param spectrumIdentificationList the spectrum identification list
     * @return the {@link SpectrumIdentification} instance
     */
    private SpectrumIdentification setupSpectrumIdentification(SpectraData spectraData, SpectrumIdentificationList spectrumIdentificationList) {
        SpectrumIdentification spectrumIdentification = new SpectrumIdentification();
        spectrumIdentification.setId(String.format(SPECTRUM_IDENTIFICATION_ID, 1));

        spectrumIdentification.setSpectrumIdentificationProtocol(mzIdentML.getAnalysisProtocolCollection().getSpectrumIdentificationProtocol().get(0));

        //add the SearchDatabaseRef instances to the spectrum identification
        for (Map.Entry<FastaDb, SearchDatabase> entry : fastaDbToSearchDatabases.entrySet()) {
            SearchDatabaseRef searchDatabaseRef = new SearchDatabaseRef();
            searchDatabaseRef.setSearchDatabase(entry.getValue());

            //add the search database ref to the spectrum identification
            spectrumIdentification.getSearchDatabaseRef().add(searchDatabaseRef);
        }

        //add the input spectra to the spectrum identification
        InputSpectra inputSpectra = new InputSpectra();
        inputSpectra.setSpectraData(spectraData);
        spectrumIdentification.getInputSpectra().add(inputSpectra);

        spectrumIdentification.setSpectrumIdentificationList(spectrumIdentificationList);

        return spectrumIdentification;
    }

    /**
     * Set up the spectra data.
     *
     * @return the {@link SpectraData} instance
     * @throws IOException in case of an JSON parsing related problem
     */
    private SpectraData setupSpectraData() throws IOException {
        SpectraData spectraData = new SpectraData();

        FileFormat spectrumFileFormat = new FileFormat();
        spectrumFileFormat.setCvParam(getMzIdentMlElement("/FileFormat/Mascot MGF", CvParam.class));

        SpectrumIDFormat spectrumIDFormat = new SpectrumIDFormat();
        spectrumIDFormat.setCvParam(getMzIdentMlElement("/SpectrumIDFormat/Multiple peak list", CvParam.class));

        spectraData.setId(String.format(SPECTRUM_DATA_ID, 1));
        String spectraDataLocation = mzIdentMlExport.getMgfPath() == null ? "data.mgf" : mzIdentMlExport.getMgfPath().toString();
        spectraData.setLocation(spectraDataLocation);                        // NOTE: may not be accurate
        spectraData.setFileFormat(spectrumFileFormat);
        spectraData.setSpectrumIDFormat(spectrumIDFormat);
        mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

        return spectraData;
    }

    /**
     * Set up the protein detection.
     *
     * @param proteinDetectionList       the protein detection list
     * @param spectrumIdentificationList the spectrum identification list
     * @return the {@link ProteinDetection} instance
     */
    private ProteinDetection setupProteinDetection(ProteinDetectionList proteinDetectionList, SpectrumIdentificationList spectrumIdentificationList) {
        ProteinDetection proteinDetection = new ProteinDetection();

        //set up the spectrum identifications
        InputSpectrumIdentifications inputSpectrumIdentifications = new InputSpectrumIdentifications();
        inputSpectrumIdentifications.setSpectrumIdentificationList(spectrumIdentificationList);

        proteinDetection.setId(String.format(PROTEIN_DETECTION_ID, 1));
        proteinDetection.setProteinDetectionList(proteinDetectionList);
        proteinDetection.setProteinDetectionProtocol(mzIdentML.getAnalysisProtocolCollection().getProteinDetectionProtocol());
        proteinDetection.getInputSpectrumIdentifications().add(inputSpectrumIdentifications);

        return proteinDetection;
    }

    /**
     * Create a spectrum identification result from a Colims spectrum.
     *
     * @param spectrum      the Colims {@link Spectrum} instance
     * @param spectraData   the spectra data instance
     * @param spectrumIndex the spectrum index
     * @return Spectrum identification result
     */
    private SpectrumIdentificationResult populateSpectrumIdentificationResult(Spectrum spectrum, SpectraData spectraData, Long spectrumIndex) throws IOException {
        SpectrumIdentificationResult spectrumIdentificationResult = new SpectrumIdentificationResult();

        spectrumIdentificationResult.setId(String.format(SPECTRUM_IDENTIFICATION_RESULT_ID, spectrum.getId()));
        spectrumIdentificationResult.setSpectraData(spectraData);

        //handle matching-between-runs spectra
        if (!spectrum.getAccession().equals(Spectrum.MBR_SPECTRUM_ACCESSION)) {
            spectrumIdentificationResult.setSpectrumID(String.format(MGF_SPECTRUM_INDEX, spectrumIndex));

            //add the spectrum title as a CV param
            CvParam spectrumTitle = getMzIdentMlElement("/Spectrum/Spectrum title", CvParam.class);
            spectrumTitle.setValue(spectrum.getTitle());
            spectrumIdentificationResult.getCvParam().add(spectrumTitle);
        } else {
            spectrumIdentificationResult.setSpectrumID(Spectrum.MBR_SPECTRUM_ACCESSION);
//            spectrumIdentificationResult.setSpectrumID(String.format(MGF_SPECTRUM_INDEX, 1L));
        }

        if (spectrum.getRetentionTime() != null) {
            CvParam retentionTime = getMzIdentMlElement("/Spectrum/Retention time", CvParam.class);
            retentionTime.setValue(Double.toString(spectrum.getRetentionTime()));
            addTimeUnit(retentionTime);
            spectrumIdentificationResult.getCvParam().add(retentionTime);
        }

        return spectrumIdentificationResult;
    }

    /**
     * Add the protein scores to the {@link ProteinAmbiguityGroup} instance.
     *
     * @param proteinGroup          the protein group instance
     * @param proteinAmbiguityGroup the protein ambiguity group instance
     */
    private void addProteinScores(ProteinGroup proteinGroup, ProteinAmbiguityGroup proteinAmbiguityGroup) throws IOException {
        //add the scores
        switch (searchEngine.getSearchEngineType()) {
            case PEPTIDESHAKER:
                if (proteinGroup.getProteinProbability() != null) {
                    CvParam proteinScore = getMzIdentMlElement("/Scores/Protein/PS_score", CvParam.class);
                    proteinScore.setValue(Double.toString(getScore(proteinGroup.getProteinProbability())));
                    proteinAmbiguityGroup.getCvParam().add(proteinScore);
                }
                if (proteinGroup.getProteinPostErrorProbability() != null) {
                    CvParam proteinConfidence = getMzIdentMlElement("/Scores/Protein/PS_confidence", CvParam.class);
                    double confidence = 100.0 * (1 - proteinGroup.getProteinPostErrorProbability());
                    if (confidence <= 0) {
                        confidence = 0;
                    }
                    proteinConfidence.setValue(Double.toString(confidence));
                    proteinAmbiguityGroup.getCvParam().add(proteinConfidence);
                }
                break;
            case MAXQUANT:
                if (proteinGroup.getProteinPostErrorProbability() != null) {
                    CvParam proteinPep = getMzIdentMlElement("/Scores/Protein/MQ_PEP", CvParam.class);
                    proteinPep.setValue(Double.toString(proteinGroup.getProteinPostErrorProbability()));
                    proteinAmbiguityGroup.getCvParam().add(proteinPep);
                }
                break;
            default:
                throw new IllegalStateException("Should not get here");
        }
    }

    /**
     * Create a spectrum identification item from a Colims spectrum.
     *
     * @param spectrum the Colims {@link Spectrum} instance
     * @param peptide  the Colims {@link Peptide} instance
     * @return the populated {@link SpectrumIdentification} instance
     */
    private SpectrumIdentificationItem populateSpectrumIdentificationItem(Spectrum spectrum, Peptide peptide) throws IOException {
        SpectrumIdentificationItem spectrumIdentificationItem = new SpectrumIdentificationItem();

        spectrumIdentificationItem.setId(String.format(SPECTRUM_IDENTIFICATION_ITEM_ID, peptide.getId()));
        if (spectrum.getCharge() != null) {
            spectrumIdentificationItem.setChargeState(spectrum.getCharge());
        }
        spectrumIdentificationItem.setExperimentalMassToCharge(spectrum.getMzRatio());
        spectrumIdentificationItem.setCalculatedMassToCharge(peptide.getTheoreticalMass() / peptide.getCharge());
        //TODO what to do with the rank and threshold
        spectrumIdentificationItem.setPassThreshold(true);
        spectrumIdentificationItem.setRank(1);

        //add the scores and get the fragement ion annotations
        ArrayList<IonMatch> annotations = new ArrayList<>();
        switch (searchEngine.getSearchEngineType()) {
            case PEPTIDESHAKER:
                if (peptide.getPsmProbability() != null) {
                    CvParam psmScore = getMzIdentMlElement("/Scores/PSM/PS_score", CvParam.class);
                    psmScore.setValue(Double.toString(getScore(peptide.getPsmProbability())));
                    spectrumIdentificationItem.getCvParam().add(psmScore);
                }
                if (peptide.getPsmPostErrorProbability() != null) {
                    CvParam psmConfidence = getMzIdentMlElement("/Scores/PSM/PS_confidence", CvParam.class);
                    double confidence = 100.0 * (1 - peptide.getPsmPostErrorProbability());
                    if (confidence <= 0) {
                        confidence = 0;
                    }
                    psmConfidence.setValue(Double.toString(confidence));
                    spectrumIdentificationItem.getCvParam().add(psmConfidence);
                }

                AnnotationSettings annotationSettings;

                PeptideSpectrumAnnotator peptideSpectrumAnnotator = new PeptideSpectrumAnnotator();

                //map the Colims Peptide instance onto the PeptideAssumption
                PeptideAssumption peptideAssumption = colimsPeptideMapper.map(peptide);

                //map the Colims Spectrum instance onto the Utilities MSnSpectrum instance
                MSnSpectrum msnSpectrum = colimsSpectrumMapper.map(spectrum);

                SpecificAnnotationSettings specificAnnotationSettings = annotationSettings.getSpecificAnnotationPreferences(
                        msnSpectrum.getSpectrumTitle(),
                        peptideAssumption,
                        new SequenceMatchingPreferences(),
                        new SequenceMatchingPreferences()
                );

                annotations = peptideSpectrumAnnotator.getSpectrumAnnotation(
                        annotationSettings,
                        specificAnnotationSettings,
                        msnSpectrum,
                        peptideAssumption.getPeptide()
                );
                break;
            case MAXQUANT:
                if (peptide.getPsmProbability() != null) {
                    CvParam psmScore = getMzIdentMlElement("/Scores/PSM/MQ_score", CvParam.class);
                    psmScore.setValue(Double.toString(peptide.getPsmProbability()));
                    spectrumIdentificationItem.getCvParam().add(psmScore);
                }
                if (peptide.getPsmPostErrorProbability() != null) {
                    CvParam psmPep = getMzIdentMlElement("/Scores/PSM/MQ_PEP", CvParam.class);
                    psmPep.setValue(Double.toString(peptide.getPsmPostErrorProbability()));
                    spectrumIdentificationItem.getCvParam().add(psmPep);
                }
                annotations = colimsPeptideMapper.mapFragmentAnnotations(peptide);
                break;
            default:
                throw new IllegalStateException("Should not get here");
        }

        //add the theoretical mass
        if (peptide.getTheoreticalMass() != null) {
            CvParam theoreticalMass = getMzIdentMlElement("/PSM/Theoretical mass", CvParam.class);
            theoreticalMass.setValue(Double.toString(peptide.getTheoreticalMass()));
            addMassUnit(theoreticalMass, MassAccuracyType.DA);
            spectrumIdentificationItem.getCvParam().add(theoreticalMass);
        }

        return spectrumIdentificationItem;
    }

    /**
     * Create a spectrum identification item from an MaxQuant MBR dummy spectrum.
     *
     * @param peptide the Colims {@link Peptide} instance
     * @return the populated {@link SpectrumIdentification} instance
     */
    private SpectrumIdentificationItem populateMbrSpectrumIdentificationItem(Peptide peptide) throws IOException {
        SpectrumIdentificationItem spectrumIdentificationItem = new SpectrumIdentificationItem();

        spectrumIdentificationItem.setId(String.format(SPECTRUM_IDENTIFICATION_ITEM_ID, peptide.getId()));

        //TODO what to do with the rank and threshold
        spectrumIdentificationItem.setPassThreshold(true);
        spectrumIdentificationItem.setRank(1);

        //add the theoretical mass
        if (peptide.getTheoreticalMass() != null) {
            CvParam theoreticalMass = getMzIdentMlElement("/PSM/Theoretical mass", CvParam.class);
            theoreticalMass.setValue(Double.toString(peptide.getTheoreticalMass()));
            addMassUnit(theoreticalMass, MassAccuracyType.DA);
            spectrumIdentificationItem.getCvParam().add(theoreticalMass);
        }

        return spectrumIdentificationItem;
    }

    /**
     * Returns a score from a raw score where the score = -10*log(rawScore). The
     * maximum score is 100 and raw scores smaller or equal to zero have a score
     * of 100.
     *
     * @param rawScore the raw score
     * @return the score
     */
    private Double getScore(Double rawScore) {
        double score;
        if (rawScore <= 0) {
            score = 100;
        } else {
            score = -10 * FastMath.log10(rawScore);
            if (score > 100) {
                score = 100;
            }
        }
        return score;
    }

    /**
     * Create an mzIdentML modification from a Colims equivalent.
     *
     * @param peptideHasModification Peptide to modification representation
     * @return the populated {@link Modification} instance
     */
    private Modification populateModification(PeptideHasModification peptideHasModification) throws IOException {
        Modification modification = new Modification();

        modification.setMonoisotopicMassDelta(peptideHasModification.getModification().getMonoIsotopicMassShift());
        modification.setLocation(peptideHasModification.getLocation());
        modification.getCvParam().add(modificationToCvParam(peptideHasModification.getModification()));

        return modification;
    }

    /**
     * Populate a {@link DBSequence} instance for the given protein accession
     * and sequence.
     *
     * @param accession the protein accession
     * @param sequence  the protein sequence
     * @param id        the db sequence ID
     * @return representative DBSequence
     */
    private DBSequence populateDBSequence(String accession, String sequence, Long id) throws IOException {
        DBSequence dbSequence = new DBSequence();
        dbSequence.setAccession(accession);
        dbSequence.setLength(sequence.length());
        dbSequence.setSeq(sequence);
        dbSequence.setId(String.format(DB_SEQUENCE_ID, id));

        SearchDatabase searchDatabase = getSearchDatabaseForAccession(accession);
        dbSequence.setSearchDatabase(searchDatabase);

        CvParam cvParam = getMzIdentMlElement("/DB Sequence/Description", CvParam.class);
        cvParam.setValue(accession);

        dbSequence.getCvParam().add(cvParam);

        return dbSequence;
    }

    /**
     * Get the {@link SearchDatabase} instance associated with the given protein
     * accession.
     *
     * @param proteinAccession the protein accession
     * @return the search database that contains the accession
     */
    private SearchDatabase getSearchDatabaseForAccession(String proteinAccession) {
        for (Map.Entry<FastaDb, Set<String>> entry : proteinAccessions.entrySet()) {
            if (proteinAccession.startsWith(ProteinGroupHasProtein.CONTAMINANT_PREFIX)) {
                proteinAccession = proteinAccession.substring(ProteinGroupHasProtein.CONTAMINANT_PREFIX.length());
            }
            if (entry.getValue().contains(proteinAccession)) {
                return fastaDbToSearchDatabases.get(entry.getKey());
            }
        }
        throw new IllegalArgumentException("The protein accession " + proteinAccession + " was not found in the FASTA DB files.");
    }

    /**
     * Get the CV representation of a Colims modification.
     *
     * @param modification a colims modification
     * @param <T>          subclass of {@link AbstractModification}
     * @return the modification in CvParam form
     * @throws IOException in case of an JSON parsing related problem
     */
    private <T extends AbstractModification> CvParam modificationToCvParam(T modification) throws IOException {
        CvParam modParam = null;

        //try to get the modification ontology by it's prefix
        if (modification.getAccession().contains(MODIFICATION_ACCESSION_DELIMITER)) {
            modParam = getMzIdentMlElement("/ModCV/" + modification.getAccession().substring(0, modification.getAccession().indexOf(MODIFICATION_ACCESSION_DELIMITER)), CvParam.class);
        }
        if (modParam == null) {
            modParam = getMzIdentMlElement("/ModCV/UNKNOWN", CvParam.class);
        }
        modParam.setName(modification.getName());
        modParam.setAccession(modification.getAccession());

        return modParam;
    }

    /**
     * Get a list of child MzIdentML elements by their class and parent name.
     *
     * @param parentName name of key or dot notation path to key
     * @param type       type of objects to return
     * @param <T>        subclass of MzIdentMLObject
     * @return list of objects of type T
     * @throws IOException in case of a JSON parsing related problem
     */
    public <T extends MzIdentMLObject> List<T> getChildMzIdentMlElements(String parentName, Class<T> type) throws IOException {
        JsonNode parentNode = getNodeByPath(parentName);

        List<T> mzIdentMlElements = new ArrayList<>();
        try {
            for (JsonNode childNode : parentNode) {
                mzIdentMlElements.add(objectReader.treeToValue(childNode, type));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
            throw e;
        }

        return mzIdentMlElements;
    }

    /**
     * Get an MzIdentML element by name and class. Returns null if nothing was
     * found.
     *
     * @param name Name of key or dot notation path to key
     * @param type Type of object to be returned
     * @param <T>  Subclass of MzIdentMLObject
     * @return Object of type T, null if nothing was found
     * @throws java.io.IOException in case of an I/O related problem
     */
    public <T extends MzIdentMLObject> T getMzIdentMlElement(String name, Class<T> type) throws IOException {
        T mzIdentMlElement = null;

        JsonNode node = getNodeByPath(name);
        if (!node.isMissingNode()) {
            try {
                //check if a node has a "cvRef" value
                if (node.has("cvRef")) {
                    String cvRef = node.get("cvRef").asText();
                    boolean updated = updateCvList(cvRef);
                    if (!updated) {
                        LOGGER.warn("Couldn't find a CV with reference " + cvRef);
                    }
                }

                mzIdentMlElement = objectReader.treeToValue(node, type);
            } catch (IOException e) {
                LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
                throw e;
            }
        }

        return mzIdentMlElement;
    }

    /**
     * Find a node by name or dot notation path.
     *
     * @param path name or path
     * @return the found JsonNode instance
     */
    private JsonNode getNodeByPath(String path) {
        JsonNode node;

        node = ontologyTerms.at(path);

        return node;
    }
}

/**
 * Convenience class for keeping track of the "unique" peptide evidences when
 * iterating over the peptides associated with a protein group.
 */
class UniqueEvidence {

    /**
     * The protein accession.
     */
    private final String proteinAccession;
    /**
     * The {@link PeptideDTO} instance.
     */
    private final PeptideDTO peptideDTO;
    /**
     * The {@link PeptidePosition} instance.
     */
    private final PeptidePosition peptidePosition;
    /**
     * The {@link PeptideEvidence} instance.
     */
    private PeptideEvidence peptideEvidence;
    /**
     * The {@link PeptideHypothesis} instance.
     */
    private PeptideHypothesis peptideHypothesis;

    /**
     * Constructor.
     *
     * @param proteinAccession the protein accession
     * @param peptideDTO       the {@link PeptideDTO} instance
     * @param peptidePosition  the {@link PeptidePosition} instance
     */
    public UniqueEvidence(String proteinAccession, PeptideDTO peptideDTO, PeptidePosition peptidePosition) {
        this.proteinAccession = proteinAccession;
        this.peptideDTO = peptideDTO;
        this.peptidePosition = peptidePosition;
    }

    public PeptideEvidence getPeptideEvidence() {
        return peptideEvidence;
    }

    public void setPeptideEvidence(PeptideEvidence peptideEvidence) {
        this.peptideEvidence = peptideEvidence;
    }

    public PeptideHypothesis getPeptideHypothesis() {
        return peptideHypothesis;
    }

    public void setPeptideHypothesis(PeptideHypothesis peptideHypothesis) {
        this.peptideHypothesis = peptideHypothesis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UniqueEvidence that = (UniqueEvidence) o;

        if (!this.proteinAccession.equals(that.proteinAccession)) {
            return false;
        }
        if (!peptideDTO.equals(that.peptideDTO)) {
            return false;
        }
        if (!Objects.equals(this.peptidePosition.getStartPosition(), that.peptidePosition.getStartPosition())) {
            return false;
        }
        return Objects.equals(this.peptidePosition.getEndPosition(), that.peptidePosition.getEndPosition()) && this.peptidePosition.getPreAA() == that.peptidePosition.getPreAA() && this.peptidePosition.getPostAA() == that.peptidePosition.getPostAA();
    }

    @Override
    public int hashCode() {
        int result = proteinAccession.hashCode();
        result = 31 * result + peptideDTO.hashCode();
        return result;
    }
}
