package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.core.io.fasta.FastaDbAccessionParser;
import com.compomics.colims.core.ontology.ols.Ontology;
import com.compomics.colims.core.ontology.ols.OntologyTerm;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.core.util.ResourceUtils;
import com.compomics.colims.core.util.SequenceUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.enums.*;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.math.util.MathUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Role;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MzIdentML exporter class, populates models from the jmzidml library then uses
 * the MzIdentMLMarshaller to marshal them into valid XML.
 *
 * @author Iain
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
    private static final String SPECTRUM_ID = "SPEC-%d";
    private static final String SPECTRUM_DATA_ID = "SPECDAT-%d";
    private static final String SPECTRUM_IDENTIFICATION_ID = "SI-%d";
    private static final String SPECTRUM_IDENTIFICATION_ITEM_ID = "SII-%d";
    private static final String SPECTRUM_IDENTIFICATION_RESULT_ID = "SIR-%d";
    private static final String SPECTRUM_IDENTIFICATION_LIST_ID = "SIL-%d";
    private static final String PROTEIN_DETECTION_LIST_ID = "PDL-%d";
    private static final String PROTEIN_DETECTION_ID = "PD-%d";
    private static final String PROTEIN_DETECTION_HYPOTHESIS_ID = "PDH-%d";

    @Value("${mzidentml.version}")
    private final String MZIDENTML_VERSION = "1.1.0";
    @Value("${colims-core.version}")
    private final String COLIMS_VERSION = "latest";
    /**
     * The FASTAs location as provided in the client properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectReader objectReader = objectMapper.reader();
    private JsonNode ontologyTerms;
    /**
     * The list is analytical runs to export.
     */
    private List<AnalyticalRun> analyticalRuns;
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
     * The CVs used in the MzIdentML file (key: CV reference; value: {@link Cv} instance).
     */
    private final Map<String, Cv> cvs = new HashMap<>();
    /**
     * The map of protein accessions parsed from the used FASTA DB files.
     */
    private LinkedHashMap<FastaDb, Set<String>> proteinAccessions;
    /**
     * This map links the used {@link FastaDb} instances to their corresponding {@link SearchDatabase} instances.
     */
    private Map<FastaDb, SearchDatabase> fastaDbToSearchDatabases = new HashMap<>();
    /**
     * This map holds the link between {@link SearchDatabase} and {@link SearchDatabaseRef} instances.
     */
    private Map<SearchDatabase, SearchDatabaseRef> searchDatabases = new HashMap<>();
    /**
     * The Ontology Lookup Service (OLS) service.
     */
    private final OlsService olsService;
    private final SearchAndValidationSettingsService searchAndValidationSettingsService;
    private final FastaDbAccessionParser fastaDbAccessionParser;
    private final ProteinGroupService proteinGroupService;
    private final PeptideService peptideService;

    @Autowired
    public MzIdentMlExporter(OlsService olsService, SearchAndValidationSettingsService searchAndValidationSettingsService, FastaDbAccessionParser fastaDbAccessionParser, ProteinGroupService proteinGroupService, PeptideService peptideService) {
        this.olsService = olsService;
        this.searchAndValidationSettingsService = searchAndValidationSettingsService;
        this.fastaDbAccessionParser = fastaDbAccessionParser;
        this.proteinGroupService = proteinGroupService;
        this.peptideService = peptideService;
    }

    public void setFastasPath(String fastasPath) {
        this.fastasPath = fastasPath;
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
     * @param writer         the {@link Writer} instance
     * @param analyticalRuns the analytical runs to export.
     * @return the mzIdentML String
     * @throws IOException error thrown in case of a I/O related problem
     */
    public void export(Writer writer, List<AnalyticalRun> analyticalRuns) throws IOException {
        this.analyticalRuns = analyticalRuns;
        this.searchEngine = analyticalRuns.get(0).getSearchAndValidationSettings().getSearchEngine();

        populate();

        MzIdentMLMarshaller mzIdentMLMarshaller = new MzIdentMLMarshaller();

        mzIdentMLMarshaller.marshal(mzIdentML, writer);

        clear();
    }

    /**
     * Clear the resources of the exporter.
     */
    private void clear() {
        mzIdentML = null;
        analyticalRuns.clear();
        proteinAccessions.clear();
        searchDatabases.clear();
        fastaDbToSearchDatabases.clear();
        cvs.clear();
        searchEngine = null;
        cvs.clear();
        inputs = null;
    }

    /**
     * Assemble necessary data into an mzIdentML object and it's many
     * properties.
     */
    private void populate() throws IOException {
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

        populateSpectrumData();

        //add all the used CVs
        mzIdentML.getCvList().getCv().addAll(cvs.values());
    }

    /**
     * Update the CV list if the given CV reference is not present.
     *
     * @param cvRef the CV reference String
     * @return true if the CV was added or already present
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
     * Populate the audit collection for associated entities, add the owner of
     * the run's project.
     */
    private AuditCollection populateAuditCollection() throws IOException {
        AuditCollection auditCollection = new AuditCollection();

        //get the owner of the project by the first run
        //@TODO provide an option to choose the user during the export process?
        User owner = analyticalRuns.get(0).getSample().getExperiment().getProject().getOwner();

        //create the owner person
        Person person = new Person();
        person.setId("PERSON_DOC_OWNER");
        person.setFirstName(owner.getFirstName());
        person.setLastName(owner.getLastName());

        CvParam email = getMzIdentMlElement("/Person/Email", CvParam.class);
        email.setValue(owner.getEmail());

        person.getCvParam().add(email);

        auditCollection.getPerson().add(person);

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

        SearchAndValidationSettings searchAndValidationSettings = analyticalRuns.get(0).getSearchAndValidationSettings();
        searchAndValidationSettingsService.fetchSearchSettingsHasFastaDb(searchAndValidationSettings);

        //parse the protein accessions from the FASTA DB files
        //first check if the FASTA DB files directory exists
        Path fastasDirectory = Paths.get(fastasPath);
        if (!Files.exists(fastasDirectory)) {
            throw new IllegalArgumentException("The FASTA DB files directory defined in the client properties file " + fastasPath.toString() + " doesn't exist.");
        }
        //order the used FASTA DB files by type, check their existence and pass them to the accession parser
        LinkedHashMap<FastaDb, Path> fastaDbs = new LinkedHashMap<>();
        Arrays.stream(FastaDbType.values()).forEach(fastaDbType -> {
            List<FastaDb> fastaDbsByType = getFastaDbsByType(searchAndValidationSettings.getSearchSettingsHasFastaDbs(), fastaDbType);
            fastaDbsByType.forEach(fastaDb -> {
                //resolve the relative path against the FASTA DB directory and check if it exists
                Path fullFastaPath = fastasDirectory.resolve(fastaDb.getFilePath());
                if (!Files.exists(fullFastaPath)) {
                    throw new IllegalArgumentException("The FASTA DB file with path " + fullFastaPath.toString() + " cannot be found.");
                }
                //
                fastaDbs.put(fastaDb, fullFastaPath);
            });
        });
        proteinAccessions = fastaDbAccessionParser.parseFastas(fastaDbs);

        //iterate over the different FASTA databases used for the searches and map them to SearchDatabase instances
        for (int i = 0; i < searchAndValidationSettings.getSearchSettingsHasFastaDbs().size(); i++) {
            SearchSettingsHasFastaDb searchSettingsHasFastaDb = searchAndValidationSettings.getSearchSettingsHasFastaDbs().get(i);
            FastaDb fastaDb = searchSettingsHasFastaDb.getFastaDb();

            SearchDatabase searchDatabase = new SearchDatabase();
            searchDatabase.setId(String.format(SEARCH_DB_ID, i));
            searchDatabase.setLocation(fastaDb.getFilePath());
            searchDatabase.setName(fastaDb.getName());
            searchDatabase.setVersion(fastaDb.getVersion());
            searchDatabase.setFileFormat(new FileFormat());
            searchDatabase.getFileFormat().setCvParam(getMzIdentMlElement("/FileFormat/FASTA", CvParam.class));
            searchDatabase.getCvParam().add(getMzIdentMlElement("/SearchDatabase/Type", CvParam.class));

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
                }
            }

            fastaDbToSearchDatabases.put(fastaDb, searchDatabase);
            SearchDatabaseRef searchDatabaseRef = new SearchDatabaseRef();
            searchDatabaseRef.setSearchDatabase(searchDatabase);
            searchDatabases.put(searchDatabase, searchDatabaseRef);
            inputs.getSearchDatabase().add(searchDatabase);
        }

        dataCollection.setAnalysisData(new AnalysisData());

        return dataCollection;
    }

    /**
     * Get the {@link FastaDb} instances by type from the list of {@link SearchSettingsHasFastaDb} instances.
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

        SearchAndValidationSettings settings = analyticalRuns.get(0).getSearchAndValidationSettings();
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
     * Iterate the spectrum data for this run and populate the necessary objects
     * with it.
     */
    private void populateSpectrumData() throws IOException {
        //set up the spectrum identification list
        SpectrumIdentificationList spectrumIdentificationList = new SpectrumIdentificationList();
        spectrumIdentificationList.setId(String.format(SPECTRUM_IDENTIFICATION_LIST_ID, 1));

        //set up the spectrum identification
        SpectrumIdentification spectrumIdentification = new SpectrumIdentification();
        spectrumIdentification.setId(String.format(SPECTRUM_IDENTIFICATION_ID, 1));
        spectrumIdentification.setSpectrumIdentificationList(spectrumIdentificationList);

        //calculate the number of sequences searched and compose a list of SearchDatabaseRef instances
        long numberOfSequencesSearched = 0;
        List<SearchDatabaseRef> searchDatabaseRefs = new ArrayList<>();
        for (Map.Entry<FastaDb, SearchDatabase> entry : fastaDbToSearchDatabases.entrySet()) {
            SearchDatabaseRef searchDatabaseRef = new SearchDatabaseRef();
            searchDatabaseRef.setSearchDatabase(entry.getValue());
            searchDatabaseRefs.add(searchDatabaseRef);

            //add the search database ref to the spectrum identification
            spectrumIdentification.getSearchDatabaseRef().add(searchDatabaseRef);

            numberOfSequencesSearched += proteinAccessions.get(entry.getKey()).size();
        }
        spectrumIdentificationList.setNumSequencesSearched(numberOfSequencesSearched);
        spectrumIdentification.setSpectrumIdentificationProtocol(mzIdentML.getAnalysisProtocolCollection().getSpectrumIdentificationProtocol().get(0));
        mzIdentML.getDataCollection().getAnalysisData().getSpectrumIdentificationList().add(spectrumIdentificationList);

        //set up spectra data
        FileFormat spectrumFileFormat = new FileFormat();
        spectrumFileFormat.setCvParam(getMzIdentMlElement("/FileFormat/Mascot MGF", CvParam.class));

        SpectrumIDFormat spectrumIDFormat = new SpectrumIDFormat();
        spectrumIDFormat.setCvParam(getMzIdentMlElement("/SpectrumIDFormat/Mascot query number", CvParam.class));

        SpectraData spectraData = new SpectraData();
        spectraData.setId(String.format(SPECTRUM_DATA_ID, 1));
        spectraData.setLocation("data.mgf");                        // NOTE: may not be accurate
        spectraData.setFileFormat(spectrumFileFormat);
        spectraData.setSpectrumIDFormat(spectrumIDFormat);
        mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

        //add the input spectra to the spectrum identification
        InputSpectra inputSpectra = new InputSpectra();
        inputSpectra.setSpectraData(spectraData);
        spectrumIdentification.getInputSpectra().add(inputSpectra);

        //map the keep track of spectra with more than one identification (chimeric spectra)
        Map<Long, SpectrumIdentificationResult> spectrumIdentificationResults = new HashMap<>();

        //set up the protein detection list
        ProteinDetectionList proteinDetectionList = new ProteinDetectionList();
        proteinDetectionList.setId(String.format(PROTEIN_DETECTION_LIST_ID, 1));
        mzIdentML.getDataCollection().getAnalysisData().setProteinDetectionList(proteinDetectionList);

        //set up the sequence collection
        SequenceCollection sequenceCollection = new SequenceCollection();
        mzIdentML.setSequenceCollection(sequenceCollection);

        //
        InputSpectrumIdentifications inputSpectrumIdentifications = new InputSpectrumIdentifications();
        inputSpectrumIdentifications.setSpectrumIdentificationList(spectrumIdentificationList);

        //set up the protein detection
        ProteinDetection proteinDetection = new ProteinDetection();
        proteinDetection.setId(String.format(PROTEIN_DETECTION_ID, 1));
        proteinDetection.setProteinDetectionList(proteinDetectionList);
        proteinDetection.setProteinDetectionProtocol(mzIdentML.getAnalysisProtocolCollection().getProteinDetectionProtocol());
        proteinDetection.getInputSpectrumIdentifications().add(inputSpectrumIdentifications);

        //set up the analysis collection
        AnalysisCollection analysisCollection = new AnalysisCollection();
        analysisCollection.getSpectrumIdentification().add(spectrumIdentification);
        analysisCollection.setProteinDetection(proteinDetection);
        mzIdentML.setAnalysisCollection(analysisCollection);

        //map to keep track of protein ID - DB sequence pairs
        Map<Long, DBSequence> dbSequences = new HashMap<>();

        //get the protein groups associated with the analytical runs
        List<Long> runIds = analyticalRuns.stream().map(AnalyticalRun::getId).collect(Collectors.toList());
        List<ProteinGroup> proteinGroups = proteinGroupService.getProteinGroupsForRuns(runIds);
        //iterate over the protein groups
        for (ProteinGroup proteinGroup : proteinGroups) {
            //create an ambiguity group
            ProteinAmbiguityGroup proteinAmbiguityGroup = new ProteinAmbiguityGroup();

            //look for the main group protein
            Protein mainGroupProtein = null;
            Optional<ProteinGroupHasProtein> first = proteinGroup.getProteinGroupHasProteins().stream().filter(ProteinGroupHasProtein::getIsMainGroupProtein).findFirst();
            if (first.isPresent()) {
                mainGroupProtein = first.get().getProtein();
            } else {
                throw new IllegalArgumentException("The protein group with ID " + proteinGroup.getId() + " doesn't have a main protein.");
            }

            //iterate over the proteins in the protein group
            for (ProteinGroupHasProtein proteinGroupHasProtein : proteinGroup.getProteinGroupHasProteins()) {
                Protein protein = proteinGroupHasProtein.getProtein();

                DBSequence dbSequence;
                if (!dbSequences.containsKey(protein.getId())) {
                    dbSequence = populateDBSequence(proteinGroupHasProtein.getProteinAccession(), protein.getSequence());
                    dbSequence.setId(String.format(DB_SEQUENCE_ID, protein.getId()));
                    sequenceCollection.getDBSequence().add(dbSequence);

                    dbSequences.put(protein.getId(), dbSequence);
                } else {
                    dbSequence = dbSequences.get(protein.getId());
                }

                //create a protein detection hypothesis
                ProteinDetectionHypothesis proteinDetectionHypothesis = new ProteinDetectionHypothesis();
                proteinDetectionHypothesis.setDBSequence(dbSequence);
                //TODO: 5/01/17 what to do with this threshold
                proteinDetectionHypothesis.setPassThreshold(true);
                proteinDetectionHypothesis.setId(String.format(PROTEIN_DETECTION_HYPOTHESIS_ID, proteinGroupHasProtein.getId()));
                //TODO: set the main protein as leading protein

                //add to the protein ambiguity group
                proteinAmbiguityGroup.getProteinDetectionHypothesis().add(proteinDetectionHypothesis);
            }

            //add the protein ambiguity group to the protein detection list
            proteinDetectionList.getProteinAmbiguityGroup().add(proteinAmbiguityGroup);

            //get the peptide DTOs associated with this protein group
            List<PeptideDTO> peptideDTOs = peptideService.getPeptideDTOs(proteinGroup.getId(), runIds);
            //keep track of the peptide DTOs - mzIdentML peptide pairs that represent the "unique" peptides;
            //same sequence, same modifications
            Map<PeptideDTO, uk.ac.ebi.jmzidml.model.mzidml.Peptide> uniquePeptides = new HashMap<>();
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
                } else {
                    mzPeptide = uniquePeptides.get(peptideDTO);
                }

                //get the peptide's spectrum
                Spectrum spectrum = colimsPeptide.getSpectrum();

                //iterate over the spectrum files
                for (SpectrumFile spectrumFile : spectrum.getSpectrumFiles()) {
                    //do nothing for the moment
                }

                SpectrumIdentificationItem spectrumIdentificationItem = populateSpectrumIdentificationItem(spectrum, colimsPeptide);
                spectrumIdentificationItem.setPeptide(mzPeptide);

                //calculate peptide location values
                //more than one position is possible
                //iterate over the proteins in the protein group
                for (ProteinGroupHasProtein proteinGroupHasProtein : proteinGroup.getProteinGroupHasProteins()) {
                    Protein protein = proteinGroupHasProtein.getProtein();
                    List<PeptidePosition> peptidePositions = SequenceUtils.getPeptidePositions(protein.getSequence(), colimsPeptide.getSequence());
                    for (PeptidePosition peptidePosition : peptidePositions) {
                        PeptideEvidence peptideEvidence = new PeptideEvidence();

                        DBSequence dbSequence = dbSequences.get(protein.getId());
                        peptideEvidence.setDBSequence(dbSequence);
                        peptideEvidence.setPeptide(mzPeptide);
                        peptideEvidence.setId("PE-" + sequenceCollection.getPeptideEvidence().size());

                        peptideEvidence.setStart(peptidePosition.getStartPosition());
                        peptideEvidence.setEnd(peptidePosition.getEndPosition());
                        peptideEvidence.setPre(peptidePosition.getPreAA().toString());
                        peptideEvidence.setPost(peptidePosition.getPostAA().toString());

                        sequenceCollection.getPeptideEvidence().add(peptideEvidence);

                        PeptideEvidenceRef evidenceRef = new PeptideEvidenceRef();
                        evidenceRef.setPeptideEvidence(peptideEvidence);
                        spectrumIdentificationItem.getPeptideEvidenceRef().add(evidenceRef);

                        PeptideHypothesis peptideHypothesis = new PeptideHypothesis();
                        peptideHypothesis.setPeptideEvidence(peptideEvidence);
                        SpectrumIdentificationItemRef spectrumIdentificationItemRef = new SpectrumIdentificationItemRef();
                        spectrumIdentificationItemRef.setSpectrumIdentificationItem(spectrumIdentificationItem);
                        peptideHypothesis.getSpectrumIdentificationItemRef().add(spectrumIdentificationItemRef);

                        proteinAmbiguityGroup.getProteinDetectionHypothesis().forEach(proteinDetectionHypothesis -> proteinDetectionHypothesis.getPeptideHypothesis().add(peptideHypothesis));
                    }
                }

                SpectrumIdentificationResult spectrumIdentificationResult;
                //handle chimeric spectra
                //check whether the spectrum has only one identification
                if (spectrum.getPeptides().size() == 1) {
                    spectrumIdentificationResult = populateSpectrumIdentificationResult(spectrum, spectraData);
                    spectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
                    spectrumIdentificationList.getSpectrumIdentificationResult().add(spectrumIdentificationResult);
                }
                //otherwise it's a chimeric spectrum
                else {
                    //check if the spectrum identification result is already present in the map
                    if (!spectrumIdentificationResults.containsKey(spectrum.getId())) {
                        spectrumIdentificationResult = populateSpectrumIdentificationResult(spectrum, spectraData);
                        spectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
                        spectrumIdentificationList.getSpectrumIdentificationResult().add(spectrumIdentificationResult);

                        //add to the map
                        spectrumIdentificationResults.put(spectrum.getId(), spectrumIdentificationResult);
                    } else {
                        spectrumIdentificationResults.get(spectrum.getId()).getSpectrumIdentificationItem().add(spectrumIdentificationItem);
                    }
                    //update the ID
                    int idSuffix = spectrumIdentificationResults.get(spectrum.getId()).getSpectrumIdentificationItem().size();
                    spectrumIdentificationItem.setId(spectrumIdentificationItem.getId() + "-" + idSuffix);
                }
            }
        }
    }

    /**
     * Create a spectrum identification result from a Colims spectrum.
     *
     * @param spectrum    the Colims {@link Spectrum} instance
     * @param spectraData the spectra data instance
     * @return Spectrum identification result
     */
    private SpectrumIdentificationResult populateSpectrumIdentificationResult(Spectrum spectrum, SpectraData spectraData) {
        SpectrumIdentificationResult spectrumIdentificationResult = new SpectrumIdentificationResult();

        spectrumIdentificationResult.setId(String.format(SPECTRUM_IDENTIFICATION_RESULT_ID, spectrum.getId()));
        spectrumIdentificationResult.setSpectraData(spectraData);
        spectrumIdentificationResult.setSpectrumID(spectrum.getId().toString());

        //TODO add the spectrum title as a CV Param

        return spectrumIdentificationResult;
    }

    private ProteinAmbiguityGroup populateProteinAmbiguityGroup(ProteinGroupDTO proteinGroupDTO, List<ProteinGroupHasProtein> ambiguityMembers) {
        ProteinAmbiguityGroup proteinAmbiguityGroup = new ProteinAmbiguityGroup();

        ProteinDetectionHypothesis proteinDetectionHypothesis = new ProteinDetectionHypothesis();
//        proteinDetectionHypothesis
        PeptideHypothesis peptideHypothesis = new PeptideHypothesis();
//        peptideHypothesis.g

        return proteinAmbiguityGroup;
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
        spectrumIdentificationItem.setChargeState(spectrum.getCharge());
        spectrumIdentificationItem.setExperimentalMassToCharge(spectrum.getMzRatio());
        //TODO what to do with the rank and threshold
        spectrumIdentificationItem.setPassThreshold(true);
        spectrumIdentificationItem.setRank(0);

        //add the scores
        if (searchEngine.getSearchEngineType() == SearchEngineType.PEPTIDESHAKER) {
            if (peptide.getPsmProbability() != null) {
                CvParam psmScore = getMzIdentMlElement("/Scores/PSM/PS_score", CvParam.class);
                psmScore.setValue(Double.toString(peptide.getPsmProbability()));
                spectrumIdentificationItem.getCvParam().add(psmScore);
            }
            if (peptide.getPsmPostErrorProbability() != null) {
                CvParam psmConfidence = getMzIdentMlElement("/Scores/PSM/PS_confidence", CvParam.class);
                psmConfidence.setValue(Double.toString(peptide.getPsmPostErrorProbability()));
                spectrumIdentificationItem.getCvParam().add(psmConfidence);
            }
        } else {
            //TODO add maxquant scores here
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
     * Create an mzIdentML modification from a Colims equivalent.
     *
     * @param peptideHasMod Peptide to modification representation
     * @return Equivalent modification
     */
    private Modification populateModification(PeptideHasModification peptideHasMod) throws IOException {
        Modification modification = new Modification();

        modification.setMonoisotopicMassDelta(peptideHasMod.getModification().getMonoIsotopicMassShift());
        modification.setLocation(peptideHasMod.getLocation());
        modification.getCvParam().add(modificationToCvParam(peptideHasMod.getModification()));

        return modification;
    }

    /**
     * Populate a {@link DBSequence} instance for the given protein accession and sequence.
     *
     * @param accession the protein accession
     * @param sequence  the protein sequence
     * @return representative DBSequence
     */
    private DBSequence populateDBSequence(String accession, String sequence) throws IOException {
        DBSequence dbSequence = new DBSequence();
        dbSequence.setAccession(accession);
        dbSequence.setLength(sequence.length());
        dbSequence.setSeq(sequence);
        SearchDatabase searchDatabase = getSearchDatabaseForAccession(accession);
        dbSequence.setSearchDatabase(searchDatabase);

        CvParam cvParam = getMzIdentMlElement("/DB Sequence/Description", CvParam.class);
        cvParam.setValue(accession);

        dbSequence.getCvParam().add(cvParam);

        return dbSequence;
    }

    /**
     * Get the {@link SearchDatabase} instance associated with the given protein accession.
     *
     * @param proteinAccession the protein accession
     * @return the search database that contains the accession
     */
    private SearchDatabase getSearchDatabaseForAccession(String proteinAccession) {
        for (Map.Entry<FastaDb, Set<String>> entry : proteinAccessions.entrySet()) {
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
            modParam = getMzIdentMlElement("/GenericCV/" + modification.getAccession().substring(0, modification.getAccession().indexOf(MODIFICATION_ACCESSION_DELIMITER)), CvParam.class);
        }
        if (modParam == null) {
            modParam = getMzIdentMlElement("/GenericCV/UNKNOWN", CvParam.class);
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
     * Get an MzIdentML element by name and class. Returns null if nothing was found.
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
