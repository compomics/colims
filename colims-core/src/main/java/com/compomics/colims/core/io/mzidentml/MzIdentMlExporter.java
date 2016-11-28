package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.core.util.ResourceUtils;
import com.compomics.colims.core.util.SequenceUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MzIdentML exporter class, populates models from the jmzidml library then uses
 * the MzIdentMLMarshaller to marshal them into valid XML.
 *
 * @author Iain
 */
@Component
public class MzIdentMlExporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzIdentMlExporter.class);

    private static final String PSI_MOD_PREFIX = "MOD";
    private static final String UNIMOD_PREFIX = "UNIMOD";

    @Value("${mzidentml.version}")
    private final String MZIDENTML_VERSION = "1.1.0";
    @Value("${colims-core.version}")
    private final String COLIMS_VERSION = "latest";
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

    private final UserRepository userRepository;

    @Autowired
    public MzIdentMlExporter(UserRepository userRepository) {
        this.userRepository = userRepository;
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
     * @param analyticalRuns the analytical runs to export.
     * @return the mzIdentML String
     * @throws IOException error thrown in case of a I/O related problem
     */
    public String export(List<AnalyticalRun> analyticalRuns) throws IOException {
        this.analyticalRuns = analyticalRuns;
        this.searchEngine = analyticalRuns.get(0).getSearchAndValidationSettings().getSearchEngine();

        MzIdentMLMarshaller marshaller = new MzIdentMLMarshaller();

        return marshaller.marshal(populate());
    }

    /**
     * Assemble necessary data into an mzIdentML object and it's many
     * properties.
     *
     * @return a fully furnished {@link MzIdentML} object
     */
    private MzIdentML populate() throws IOException {
        mzIdentML = new MzIdentML();

        mzIdentML.setId("colims-" + COLIMS_VERSION);
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        CvList cvList = populateCvList();
        mzIdentML.setCvList(cvList);

        AuditCollection auditCollection = populateAuditCollection();
        mzIdentML.setAuditCollection(auditCollection);

        Provider provider = populateProvider(auditCollection.getPerson().get(0));
        mzIdentML.setProvider(provider);

        AnalysisSoftwareList analysisSoftwareList = populateAnalysisSoftwareList(auditCollection);
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList);

        DataCollection dataCollection = populateDataCollection();
        mzIdentML.setDataCollection(dataCollection);

        AnalysisSoftware analysisSoftware = analysisSoftwareList.getAnalysisSoftware().get(0);
        AnalysisProtocolCollection analysisProtocolCollection = populateAnalysisProtocolCollection(analysisSoftware);
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection);

//        assembleSpectrumData();
        return mzIdentML;
    }

    /**
     * Construct a list of CV sources used in the file.
     *
     * @return CvList List of CV sources
     */
    private CvList populateCvList() throws IOException {
        CvList cvList = new CvList();

        cvList.getCv().addAll(getChildMzIdentMlElements("/CvList", Cv.class));

        return cvList;
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

        CvParam email = getMzIdentMlElement("/Person/email", CvParam.class);
        email.setValue(owner.getEmail());

        person.getCvParam().add(email);

        auditCollection.getPerson().add(person);

        //create the owner organisation from the associated institution
        Institution institution = owner.getInstitution();

        Organization organization = new Organization();
        //getContact("LAB_PLACEHOLDER", Organization.class);
        organization.setId(institution.getName());

        CvParam address = getMzIdentMlElement("/Organization/TEMPLATE/address", CvParam.class);
        address.setValue(Arrays.stream(institution.getAddress()).collect(Collectors.joining(", ")));
        organization.getCvParam().add(address);

        CvParam name = getMzIdentMlElement("/Organization/TEMPLATE/name", CvParam.class);
        name.setValue(institution.getName());
        organization.getCvParam().add(name);

        if (institution.getEmail() != null && !institution.getEmail().isEmpty()) {
            CvParam institutionEmail = getMzIdentMlElement("/Organization/TEMPLATE/email", CvParam.class);
            institutionEmail.setValue(institution.getEmail());
            organization.getCvParam().add(institutionEmail);
        }

        if (institution.getUrl() != null && !institution.getUrl().isEmpty()) {
            CvParam url = getMzIdentMlElement("/Organization/TEMPLATE/url", CvParam.class);
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
     * @param person the contact person
     * @return the provider MzIdentML element
     */
    private Provider populateProvider(Person person) throws IOException {
        Provider provider = new Provider();
        provider.setId("PROVIDER");

        provider.setContactRole(new ContactRole());

        provider.getContactRole().setContact(mzIdentML.getAuditCollection().getPerson().get(0));

        //set the researcher role
        Role role = new Role();
        role.setCvParam(getMzIdentMlElement("/Role/researcher", CvParam.class));

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
        contactRole.getRole().setCvParam(getMzIdentMlElement("/Role/software vendor", CvParam.class));
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

        //iterate over the different FASTA databases used for the searches
        for (SearchSettingsHasFastaDb searchSettingsHasFastaDb : analyticalRuns.get(0).getSearchAndValidationSettings().getSearchSettingsHasFastaDbs()) {
            FastaDb fasta = searchSettingsHasFastaDb.getFastaDb();

            SearchDatabase searchDatabase = new SearchDatabase();
            searchDatabase.setId(fasta.getId().toString());
            searchDatabase.setLocation(fasta.getFilePath());
            searchDatabase.setName(fasta.getName());
            searchDatabase.setVersion(fasta.getVersion());
            searchDatabase.setFileFormat(new FileFormat());
            searchDatabase.getFileFormat().setCvParam(getMzIdentMlElement("/FileFormat/FASTA", CvParam.class));
            searchDatabase.setDatabaseName(new Param());
            searchDatabase.getCvParam().add(getMzIdentMlElement("/SearchDatabase/type", CvParam.class));

            //NOTE: if decoy database used then cv param should be child of MS:1001450 here
            UserParam databaseName = new UserParam();
            databaseName.setName(fasta.getName());
            searchDatabase.getDatabaseName().setParam(databaseName);

            inputs.getSearchDatabase().add(searchDatabase);
        }

        dataCollection.setAnalysisData(new AnalysisData());

        return dataCollection;
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

        SpectrumIdentificationProtocol spectrumProtocol = new SpectrumIdentificationProtocol();

        //set analysis software and search type
        spectrumProtocol.setId("SP-1");
        spectrumProtocol.setAnalysisSoftware(analysisSoftware);
        spectrumProtocol.setSearchType(new Param());
        spectrumProtocol.getSearchType().setParam(getMzIdentMlElement("/SearchType/ms-ms", CvParam.class));

        //@// TODO: 28/11/16 check the other CV params from PeptideShaker
        //set the threshold values
        spectrumProtocol.setThreshold(new ParamList());
        spectrumProtocol.getThreshold().getCvParam().add(getMzIdentMlElement("/Threshold/no", CvParam.class));


        // TODO: threshold value and type as cv param
        if (searchParameters.getSearchParametersHasModifications().size() > 0) {
            spectrumProtocol.setModificationParams(new ModificationParams());

            for (SearchParametersHasModification searchParametersHasModification : searchParameters.getSearchParametersHasModifications()) {
                SearchModification colimsSearchModification = searchParametersHasModification.getSearchModification();

                uk.ac.ebi.jmzidml.model.mzidml.SearchModification mzSearchModification = new uk.ac.ebi.jmzidml.model.mzidml.SearchModification();
                mzSearchModification.setFixedMod(searchParametersHasModification.getModificationType() == ModificationType.FIXED);
                mzSearchModification.setMassDelta(colimsSearchModification.getAverageMassShift().floatValue());

                mzSearchModification.getCvParam().add(modificationToCvParam(colimsSearchModification));

                spectrumProtocol.getModificationParams().getSearchModification().add(mzSearchModification);
            }
        }

        // Enzyme
        spectrumProtocol.setEnzymes(new Enzymes());

        Enzyme mzEnzyme = new Enzyme();
        String colimsEnzymes = searchParameters.getEnzymes();

        CvParam cvEnzyme;

        if (colimsEnzymes != null) {
            for (String colimsEnzyme : colimsEnzymes.split(";")) {
                //@// TODO: 27/09/16 refactor this
                cvEnzyme = getMzIdentMlElement("/Enzyme/" + colimsEnzyme, CvParam.class);
                cvEnzyme.setName(colimsEnzyme);
                cvEnzyme.setAccession(colimsEnzyme);

                mzEnzyme.setId("ENZYME-" + colimsEnzyme);
                mzEnzyme.setEnzymeName(new ParamList());
                mzEnzyme.setMissedCleavages(searchParameters.getNumberOfMissedCleavages() == null ? 0 : searchParameters.getNumberOfMissedCleavages());
                mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);

                spectrumProtocol.getEnzymes().getEnzyme().add(mzEnzyme);
            }
        } else {
            cvEnzyme = getMzIdentMlElement("/GenericCV/PSI-MS", CvParam.class);
            cvEnzyme.setName("no enzyme");
            cvEnzyme.setAccession("MS:1001091");

            mzEnzyme.setId("ENZYME-1");
            mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);
        }

        // Fragment Tolerance
        CvParam fragmentMinus = getMzIdentMlElement("/Tolerance/minus", CvParam.class);
        fragmentMinus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentMinus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        CvParam fragmentPlus = getMzIdentMlElement("/Tolerance/plus", CvParam.class);
        fragmentPlus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentPlus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        switch (searchParameters.getFragMassToleranceUnit()) {
            case DA:
                fragmentMinus.setUnitName("dalton");
                fragmentMinus.setUnitAccession("UO:0000221");
                fragmentMinus.setUnitCv(getMzIdentMlElement("/CvList/UO", Cv.class));
                break;
            case PPM:
                fragmentMinus.setUnitName("parts per million");
                fragmentMinus.setUnitAccession("UO:0000169");
                fragmentMinus.setUnitCv(getMzIdentMlElement("/CvList/UO", Cv.class));
                break;
            default:
                break;
        }

        spectrumProtocol.setFragmentTolerance(new Tolerance());

        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentMinus);
        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentPlus);

        spectrumProtocol.setParentTolerance(new Tolerance());

        spectrumProtocol.getParentTolerance().getCvParam().add(fragmentMinus);
        spectrumProtocol.getParentTolerance().getCvParam().add(fragmentPlus);

        collection.getSpectrumIdentificationProtocol().add(spectrumProtocol);

        // Protein Detection Protocol
        ProteinDetectionProtocol proteinProtocol = new ProteinDetectionProtocol();
        proteinProtocol.setId("PDP-1");
        proteinProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0));

        proteinProtocol.setThreshold(new ParamList());
        proteinProtocol.getThreshold().getCvParam().add(getMzIdentMlElement("/Threshold/no", CvParam.class));

        // TODO: threshold value and type as cv param
        collection.setProteinDetectionProtocol(proteinProtocol);

        return collection;
    }

    /**
     * Iterate the spectrum data for this run and populate the necessary objects
     * with it.
     */
    private void assembleSpectrumData() throws IOException {
        SpectrumIdentificationList spectrumIdentificationList = new SpectrumIdentificationList();
        spectrumIdentificationList.setId("SIL-1");

        ProteinDetectionList proteinDetectionList = new ProteinDetectionList();
        proteinDetectionList.setId("PDL-1");

        SequenceCollection sequenceCollection = new SequenceCollection();
        AnalysisCollection analysisCollection = new AnalysisCollection();

        FileFormat spectrumFileFormat = new FileFormat();
        spectrumFileFormat.setCvParam(getMzIdentMlElement("/FileFormat/Mascot MGF", CvParam.class));

        SpectrumIDFormat spectrumIDFormat = new SpectrumIDFormat();
        spectrumIDFormat.setCvParam(getMzIdentMlElement("/SpectrumIDFormat/mascot query number", CvParam.class));

        SearchDatabaseRef dbRef = new SearchDatabaseRef();
        dbRef.setSearchDatabase(inputs.getSearchDatabase().get(0));

        for (Spectrum spectrum : analyticalRuns.get(0).getSpectrums()) {
            SpectrumIdentification spectrumIdentification = new SpectrumIdentification();
            spectrumIdentification.setId("SPECTRUM-" + spectrum.getId().toString());

            spectrum.getSpectrumFiles().stream().forEach(spectrumFile -> {
                SpectraData spectraData = new SpectraData();
                spectraData.setId("SD-" + spectrumFile.getId().toString());
                spectraData.setLocation("data.mgf");                        // NOTE: may not be accurate
                spectraData.setFileFormat(spectrumFileFormat);
                spectraData.setSpectrumIDFormat(spectrumIDFormat);

                mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

                InputSpectra inputSpectra = new InputSpectra();
                inputSpectra.setSpectraData(spectraData);

                spectrumIdentification.getInputSpectra().add(inputSpectra);
            });

            SpectrumIdentificationResult spectrumIdentificationResult = createSpectrumIdentificationResult(spectrum);
            SpectrumIdentificationItem spectrumIdentificationItem = createSpectrumIdentificationItem(spectrum);

            for (com.compomics.colims.model.Peptide colimsPeptide : spectrum.getPeptides()) {
                uk.ac.ebi.jmzidml.model.mzidml.Peptide mzPeptide = new uk.ac.ebi.jmzidml.model.mzidml.Peptide();

                mzPeptide.setId("PEPTIDE-" + colimsPeptide.getId().toString());
                mzPeptide.setPeptideSequence(colimsPeptide.getSequence());

                for (PeptideHasModification peptideHasMod : colimsPeptide.getPeptideHasModifications()) {
                    mzPeptide.getModification().add(createModification(peptideHasMod));
                }

                sequenceCollection.getPeptide().add(mzPeptide);
                spectrumIdentificationItem.setPeptide(mzPeptide);

                for (PeptideHasProteinGroup peptideHasProteinGroup : colimsPeptide.getPeptideHasProteinGroups()) {
                    for (ProteinGroupHasProtein proteinGroupHasProtein : peptideHasProteinGroup.getProteinGroup().getProteinGroupHasProteins()) {
                        DBSequence dbSequence = createDBSequence(proteinGroupHasProtein);

                        sequenceCollection.getDBSequence().add(dbSequence);

                        //calculate peptide location values
                        //more than one position is possible
                        List<PeptidePosition> peptidePositions = SequenceUtils.getPeptidePositions(proteinGroupHasProtein.getProtein().getSequence(), colimsPeptide.getSequence());
                        peptidePositions.stream().forEach(peptidePosition -> {
                            PeptideEvidence evidence = new PeptideEvidence();
                            evidence.setDBSequence(dbSequence);
                            evidence.setPeptide(mzPeptide);
                            evidence.setId("PE-" + peptideHasProteinGroup.getId().toString());

                            evidence.setStart(peptidePosition.getStartPosition());
                            evidence.setEnd(peptidePosition.getEndPosition());
                            evidence.setPre(peptidePosition.getPreAA().toString());
                            evidence.setPost(peptidePosition.getPostAA().toString());

                            sequenceCollection.getPeptideEvidence().add(evidence);

                            PeptideEvidenceRef evidenceRef = new PeptideEvidenceRef();
                            evidenceRef.setPeptideEvidence(evidence);
                            spectrumIdentificationItem.getPeptideEvidenceRef().add(evidenceRef);
                        });
                    }
                }
            }

            spectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
            spectrumIdentificationList.getSpectrumIdentificationResult().add(spectrumIdentificationResult);

            spectrumIdentification.setSpectrumIdentificationList(spectrumIdentificationList);
            spectrumIdentification.setSpectrumIdentificationProtocol(mzIdentML.getAnalysisProtocolCollection().getSpectrumIdentificationProtocol().get(0));

            spectrumIdentification.getSearchDatabaseRef().add(dbRef);

            analysisCollection.getSpectrumIdentification().add(spectrumIdentification);

            ProteinDetection proteinDetection = new ProteinDetection();
            proteinDetection.setId("PD-1");
            proteinDetection.setProteinDetectionList(proteinDetectionList);
            proteinDetection.setProteinDetectionProtocol(mzIdentML.getAnalysisProtocolCollection().getProteinDetectionProtocol());

            InputSpectrumIdentifications iSI = new InputSpectrumIdentifications();
            iSI.setSpectrumIdentificationList(spectrumIdentificationList);

            proteinDetection.getInputSpectrumIdentifications().add(iSI);

            analysisCollection.setProteinDetection(proteinDetection);
        }

        mzIdentML.getDataCollection().getAnalysisData().getSpectrumIdentificationList().add(spectrumIdentificationList);
        mzIdentML.getDataCollection().getAnalysisData().setProteinDetectionList(proteinDetectionList);
        mzIdentML.setSequenceCollection(sequenceCollection);
        mzIdentML.setAnalysisCollection(analysisCollection);
    }

    /**
     * Create a spectrum identification result from a colims spectrum.
     *
     * @param spectrum Spectrum object
     * @return Spectrum identification result
     */
    private SpectrumIdentificationResult createSpectrumIdentificationResult(Spectrum spectrum) {
        SpectrumIdentificationResult spectrumIdentificationResult = new SpectrumIdentificationResult();

        spectrumIdentificationResult.setId("SIL-" + spectrum.getId().toString());
        spectrumIdentificationResult.setSpectraData(inputs.getSpectraData().get(0));
        spectrumIdentificationResult.setSpectrumID(spectrum.getId().toString());

        return spectrumIdentificationResult;
    }

    /**
     * Create a spectrum identification item from a colims spectrum.
     *
     * @param spectrum Spectrum object
     * @return Spectrum identification item
     */
    private SpectrumIdentificationItem createSpectrumIdentificationItem(Spectrum spectrum) {
        SpectrumIdentificationItem spectrumIdentificationItem = new SpectrumIdentificationItem();

        spectrumIdentificationItem.setId("SII-" + spectrum.getId().toString());
        spectrumIdentificationItem.setChargeState(spectrum.getCharge());
        spectrumIdentificationItem.setExperimentalMassToCharge(spectrum.getMzRatio());
        spectrumIdentificationItem.setPassThreshold(true);
        spectrumIdentificationItem.setRank(0);

        return spectrumIdentificationItem;
    }

    /**
     * Create an mzIdentML modification from a colims equivalent.
     *
     * @param peptideHasMod Peptide to modification representation
     * @return Equivalent modification
     */
    private Modification createModification(PeptideHasModification peptideHasMod) throws IOException {
        Modification modification = new Modification();

        modification.setMonoisotopicMassDelta(peptideHasMod.getModification().getMonoIsotopicMassShift());
        modification.setLocation(peptideHasMod.getLocation());
        modification.getCvParam().add(modificationToCvParam(peptideHasMod.getModification()));

        return modification;
    }

    /**
     * Create a new DBSequence from a protein.
     *
     * @param proteinGroupHasProtein protein group to protein representation
     * @return representative DBSequence
     */
    private DBSequence createDBSequence(ProteinGroupHasProtein proteinGroupHasProtein) throws IOException {
        Protein protein = proteinGroupHasProtein.getProtein();

        DBSequence dbSequence = new DBSequence();
        dbSequence.setId("DBS-" + protein.getId().toString());
        dbSequence.setAccession(proteinGroupHasProtein.getProteinAccession());
        dbSequence.setLength(protein.getSequence().length());
        dbSequence.setSeq(protein.getSequence());
        dbSequence.setSearchDatabase(inputs.getSearchDatabase().get(0));

        CvParam cvParam = getMzIdentMlElement("/DBSequence/description", CvParam.class);
        cvParam.setValue(proteinGroupHasProtein.getProteinAccession());

        dbSequence.getCvParam().add(cvParam);

        return dbSequence;
    }

    /**
     * Get the CV representation of a Colims modification.
     *
     * @param modification A colims modification
     * @param <T>          Subclass of AbstractModification
     * @return Modification in CvParam form
     * @throws IOException
     */
    private <T extends AbstractModification> CvParam modificationToCvParam(T modification) throws IOException {
        CvParam modParam;

        if (modification.getAccession().startsWith(UNIMOD_PREFIX)) {
            modParam = getMzIdentMlElement("/GenericCV/UNIMOD", CvParam.class);
        } else {
            modParam = getMzIdentMlElement("/GenericCV/PSI-MS", CvParam.class);
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
     * Get an MzIdentML element by name and class.
     *
     * @param name Name of key or dot notation path to key
     * @param type Type of object to be returned
     * @param <T>  Subclass of MzIdentMLObject
     * @return Object of type T
     * @throws java.io.IOException in case of an I/O related problem
     */
    public <T extends MzIdentMLObject> T getMzIdentMlElement(String name, Class<T> type) throws IOException {
        JsonNode node = getNodeByPath(name);

        T mzIdentMlElement;
        try {
            mzIdentMlElement = objectReader.treeToValue(node, type);
        } catch (IOException e) {
            LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
            throw e;
        }

        return mzIdentMlElement;
    }

    /**
     * Find a node by name or dot notation path.
     *
     * @param path name or path
     * @return the found JsonNode instance
     * @throws IllegalArgumentException in case of an invalid path
     */
    private JsonNode getNodeByPath(String path) throws IllegalArgumentException {
        JsonNode node;

        node = ontologyTerms.at(path);
        if (node.isMissingNode()) {
            throw new IllegalArgumentException("Node " + path + " not found in the mzIdentML ontology terms file.");
        }

        return node;
    }
}
