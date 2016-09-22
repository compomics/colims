package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.core.util.PeptidePosition;
import com.compomics.colims.core.util.SequenceUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Role;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * MzIdentML exporter class, populates models from the jmzidml library then uses
 * the MzIdentMLMarshaller to marshal them into valid XML.
 *
 * @author Iain
 */
@Component
public class MzIdentMLExporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzIdentMLExporter.class);

    private static final String PSI_MOD_PREFIX = "MOD";
    private static final String UNIMOD_PREFIX = "UNIMOD";
    /**
     * The JSON file that contains the MzIdentML controlled vocabulary terms.
     */
    private static final String DATA_FILE = "config/mzidentml.json";   // TODO: a better name

    // TODO: are these only filled in on production build?
    @Value("${mzidentml.version}")
    private final String MZIDENTML_VERSION = "1.1.0";
    @Value("${colims-core.version}")
    private final String COLIMS_VERSION = "latest";

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode mzIdentMLParamList;
    /**
     * The analytical run to export.
     */
    private AnalyticalRun analyticalRun;
    /**
     * The MzIdentML instance from the MzIdentML object model.
     */
    private MzIdentML mzIdentML;
    /**
     * The Inputs instance from the MzIdentML object model.
     */
    private Inputs inputs;

    @Autowired
    private UserRepository userRepository;

    /**
     * Read in the JSON file that contains controlled vocabulary terms.
     *
     * @throws IOException error thrown in case of a I/O related problem
     */
    @PostConstruct
    public void init() throws IOException {
        ClassPathResource jsonFile = new ClassPathResource(DATA_FILE);
        mzIdentMLParamList = mapper.readTree(jsonFile.getURL());
    }

    /**
     * Export a run in MzIdentML format.
     *
     * @param analyticalRun the analytical run to export.
     * @return the MzIdentML String
     * @throws IOException error thrown in case of a I/O related problem
     */
    public String export(AnalyticalRun analyticalRun) throws IOException {
        this.analyticalRun = analyticalRun;

        MzIdentMLMarshaller marshaller = new MzIdentMLMarshaller();
        return marshaller.marshal(base());
    }

    /**
     * Assemble necessary data into an MZIdentML object and it's many
     * properties.
     *
     * @return MZIdentML a fully furnished (hopefully) object
     */
    private MzIdentML base() throws IOException {
        mzIdentML = new MzIdentML();
        mzIdentML.setAuditCollection(new AuditCollection());

        mzIdentML.setId("colims-" + COLIMS_VERSION);
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());
        mzIdentML.setCvList(cvList());
        auditCollection();
        mzIdentML.setProvider(provider());
        mzIdentML.setDataCollection(dataCollection());
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList());
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection());

        assembleSpectrumData();

        return mzIdentML;
    }

    /**
     * Construct a list of CV sources used in the file.
     *
     * @return CvList List of CV sources
     */
    private CvList cvList() throws IOException {
        CvList cvList = new CvList();

        cvList.getCv().addAll(getDataList("CvList", Cv.class));

        return cvList;
    }

    /**
     * Create collection for associated entities, add the owner of this run.
     */
    private void auditCollection() throws IOException {
        // TODO: currently all users are associated with a placeholder org
        // but surely this needs to be the place they do the science
        // which they would have to input manually, somewhere

        AuditCollection auditCollection = mzIdentML.getAuditCollection();

        User user = userRepository.findByName(analyticalRun.getUserName());

        Person person = new Person();
        person.setId(user.getId().toString());
        person.setFirstName(user.getFirstName());
        person.setLastName(user.getLastName());

        CvParam email = getDataItem("Person.email", CvParam.class);
        email.setValue(user.getEmail());

        person.getCvParam().add(email);

        Institution institution = user.getInstitution();

        // TODO: if instiution does not have at least name or address then throw error (will be invalid)
        Organization org = new Organization(); //getContact("LAB_PLACEHOLDER", Organization.class);
        org.setId(institution.getName());

        CvParam cvParam = getDataItem("Organization.TEMPLATE.address", CvParam.class);
        // TODO: replace with stream
        cvParam.setValue(StringUtils.join(institution.getAddress(), ", "));
        org.getCvParam().add(cvParam);

        cvParam = getDataItem("Organization.TEMPLATE.name", CvParam.class);
        cvParam.setValue(institution.getName());
        org.getCvParam().add(cvParam);

        org.getCvParam().add(getDataItem("Organization.TEMPLATE.affiliation", CvParam.class));

        auditCollection.getOrganization().add(org);

        Affiliation affiliation = new Affiliation();
        affiliation.setOrganization(org);

        person.getAffiliation().add(affiliation);

        auditCollection.getPerson().add(person);
    }

    /**
     * Create the contact and software provider element.
     *
     * @return Provider element
     */
    private Provider provider() throws IOException {
        Provider provider = new Provider();
        provider.setId("PROVIDER");
        provider.setContactRole(new ContactRole());

        Role role = new Role();
        role.setCvParam(getDataItem("Role.researcher", CvParam.class));

        provider.getContactRole().setRole(role);
        provider.getContactRole().setContact(mzIdentML.getAuditCollection().getPerson().get(0));

        return provider;
    }

    /**
     * Construct the list of analysis software.
     *
     * @return AnalysisSoftwareList the list
     * @throws IOException
     */
    private AnalysisSoftwareList analysisSoftwareList() throws IOException {
        AnalysisSoftwareList list = new AnalysisSoftwareList();

        SearchAndValidationSettings settings = analyticalRun.getSearchAndValidationSettings();

        AnalysisSoftware software = getDataItem("AnalysisSoftware." + settings.getSearchEngine().getName(), AnalysisSoftware.class);
        software.setVersion(settings.getSearchEngine().getVersion());
        software.setSoftwareName(new Param());
        software.getSoftwareName().setParam(getDataItem("AnalysisSoftwareCV." + settings.getSearchEngine().getName(), CvParam.class));

        ContactRole contactRole = new ContactRole();
        contactRole.setContact(getContact(settings.getSearchEngine().getName(), Organization.class));
        contactRole.setRole(new Role());
        contactRole.getRole().setCvParam(getDataItem("Role.software vendor", CvParam.class));

        software.setContactRole(contactRole);

        list.getAnalysisSoftware().add(software);

        return list;
    }

    /**
     * Details of the data source for the experiment.
     *
     * @return Populated DataCollection object
     * @throws IOException if something is missing from the JSON
     */
    private DataCollection dataCollection() throws IOException {
        DataCollection dataCollection = new DataCollection();
        dataCollection.setInputs(new Inputs());

        inputs = dataCollection.getInputs();

        //iterate over the different FASTA databases used for the searches
        for (SearchSettingsHasFastaDb searchSettingsHasFastaDb : analyticalRun.getSearchAndValidationSettings().getSearchSettingsHasFastaDbs()) {
            FastaDb fasta = searchSettingsHasFastaDb.getFastaDb();

            SearchDatabase searchDatabase = new SearchDatabase();
            searchDatabase.setId(fasta.getId().toString());
            searchDatabase.setLocation(fasta.getFilePath());
            searchDatabase.setName(fasta.getName());
            searchDatabase.setVersion(fasta.getVersion());
            searchDatabase.setFileFormat(new FileFormat());
            searchDatabase.setDatabaseName(new Param());
            searchDatabase.getFileFormat().setCvParam(getDataItem("FileFormat.FASTA", CvParam.class));
            searchDatabase.getCvParam().add(getDataItem("SearchDatabase.type", CvParam.class));

            // NOTE: if decoy database used then cv param should be child of MS:1001450 here
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
     * @return Analysis Protocol object
     */
    private AnalysisProtocolCollection analysisProtocolCollection() throws IOException {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        SearchAndValidationSettings settings = analyticalRun.getSearchAndValidationSettings();
        SearchParameters searchParameters = settings.getSearchParameters();

        // Spectrum Identification Protocol
        SpectrumIdentificationProtocol spectrumProtocol = new SpectrumIdentificationProtocol();

        spectrumProtocol.setId("SP-1");
        spectrumProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0)); // bad
        spectrumProtocol.setSearchType(new Param());
        spectrumProtocol.getSearchType().setParam(getDataItem("SearchType.ms-ms", CvParam.class));

        // Threshold
        spectrumProtocol.setThreshold(new ParamList());
        spectrumProtocol.getThreshold().getCvParam().add(getDataItem("Threshold.no", CvParam.class));

        // TODO: threshold value and type as cv param
        if (searchParameters.getSearchParametersHasModifications().size() > 0) {
            spectrumProtocol.setModificationParams(new ModificationParams());

            for (SearchParametersHasModification searchHasMod : searchParameters.getSearchParametersHasModifications()) {
                SearchModification colimsSearchMod = searchHasMod.getSearchModification();

                uk.ac.ebi.jmzidml.model.mzidml.SearchModification mzSearchMod = new uk.ac.ebi.jmzidml.model.mzidml.SearchModification();
                mzSearchMod.setFixedMod(searchHasMod.getModificationType() == ModificationType.FIXED);
                mzSearchMod.setMassDelta(colimsSearchMod.getAverageMassShift().floatValue());

                mzSearchMod.getResidues().addAll(Arrays.asList(searchHasMod.getResidues().split("")));

                mzSearchMod.getCvParam().add(modificationToCvParam(colimsSearchMod));

                spectrumProtocol.getModificationParams().getSearchModification().add(mzSearchMod);
            }
        }

        // Enzyme
        spectrumProtocol.setEnzymes(new Enzymes());

        Enzyme mzEnzyme = new Enzyme();
        SearchCvParam colimsEnzyme = searchParameters.getEnzyme();

        CvParam cvEnzyme;

        if (colimsEnzyme == null) {
            cvEnzyme = getDataItem("GenericCV.PSI-MS", CvParam.class);
            cvEnzyme.setName("no enzyme");
            cvEnzyme.setAccession("MS:1001091");

            mzEnzyme.setId("ENZYME-1");
            mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);
        } else {
            cvEnzyme = getDataItem("GenericCV." + colimsEnzyme.getLabel(), CvParam.class);
            cvEnzyme.setName(colimsEnzyme.getName());
            cvEnzyme.setAccession(colimsEnzyme.getAccession());

            mzEnzyme.setId("ENZYME-" + colimsEnzyme.getId().toString());
            mzEnzyme.setEnzymeName(new ParamList());
            mzEnzyme.setMissedCleavages(searchParameters.getNumberOfMissedCleavages() == null ? 0 : searchParameters.getNumberOfMissedCleavages());
            mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);
        }

        spectrumProtocol.getEnzymes().getEnzyme().add(mzEnzyme);

        // Fragment Tolerance
        CvParam fragmentMinus = getDataItem("Tolerance.minus", CvParam.class);
        fragmentMinus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentMinus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        CvParam fragmentPlus = getDataItem("Tolerance.plus", CvParam.class);
        fragmentPlus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentPlus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        switch (searchParameters.getFragMassToleranceUnit()) {
            case DA:
                fragmentMinus.setUnitName("dalton");
                fragmentMinus.setUnitAccession("UO:0000221");
                fragmentMinus.setUnitCv(getDataItem("CvList.UO", Cv.class));
                break;
            case PPM:
                fragmentMinus.setUnitName("parts per million");
                fragmentMinus.setUnitAccession("UO:0000169");
                fragmentMinus.setUnitCv(getDataItem("CvList.UO", Cv.class));
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
        proteinProtocol.getThreshold().getCvParam().add(getDataItem("Threshold.no", CvParam.class));

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
        spectrumFileFormat.setCvParam(getDataItem("FileFormat.Mascot MGF", CvParam.class));

        SpectrumIDFormat spectrumIDFormat = new SpectrumIDFormat();
        spectrumIDFormat.setCvParam(getDataItem("SpectrumIDFormat.mascot query number", CvParam.class));

        SearchDatabaseRef dbRef = new SearchDatabaseRef();
        dbRef.setSearchDatabase(inputs.getSearchDatabase().get(0));

        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
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

        CvParam cvParam = getDataItem("DBSequence.description", CvParam.class);
        cvParam.setValue(protein.getProteinAccessions().get(0).toString());

        dbSequence.getCvParam().add(cvParam);

        return dbSequence;
    }

    /**
     * Get the CV representation of a Colims modification.
     *
     * @param modification A colims modification
     * @param <T> Subclass of AbstractModification
     * @return Modification in CvParam form
     * @throws IOException
     */
    private <T extends AbstractModification> CvParam modificationToCvParam(T modification) throws IOException {
        CvParam modParam;

        if (modification.getAccession().startsWith(UNIMOD_PREFIX)) {
            modParam = getDataItem("GenericCV.UNIMOD", CvParam.class);
        } else {
            modParam = getDataItem("GenericCV.PSI-MS", CvParam.class);
        }
        modParam.setName(modification.getName());
        modParam.setAccession(modification.getAccession());

        return modParam;
    }

    /**
     * Create a new contact detail object.
     *
     * @param name Contact name
     * @param type Desired return type
     * @param <T> Subclass of AbstractContact
     * @return Contact as subclass of AbstractContact
     */
    private <T extends AbstractContact> T getContact(String name, Class<T> type) throws IOException {
        T contact = null;

        try {
            Constructor ctor = type.getDeclaredConstructor();
            contact = (T) ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
        }

        if (contact != null) {
            contact.setId(name);
            contact.getCvParam().addAll(getDataList(type.getSimpleName() + "." + name, CvParam.class));

            mzIdentML.getAuditCollection().getPersonOrOrganization().add(contact);
        }

        return contact;
    }

    /**
     * Get a list of data items mapped to the specified object type.
     *
     * @param name Name of key or dot notation path to key
     * @param type Type of objects to return
     * @param <T> Subclass of MzIdentMLObject
     * @return List of objects of type T
     * @throws java.io.IOException in case of an I/O related problem
     */
    public <T extends MzIdentMLObject> List<T> getDataList(String name, Class<T> type) throws IOException {
        JsonNode listNode = getTargetNode(name);
        List<T> data = new ArrayList<>();

        if (listNode == null) {
            LOGGER.warn("Contact details not found for contact name: " + name);
        } else {
            try {
                for (JsonNode node : listNode) {
                    data.add(mapper.treeToValue(node, type));
                }
            } catch (IOException e) {
                LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
                throw e;
            }
        }

        return data;
    }

    /**
     * Get a single data item in the specified object type.
     *
     * @param name Name of key or dot notation path to key
     * @param type Type of object to be returned
     * @param <T> Subclass of MzIdentMLObject
     * @return Object of type T
     * @throws java.io.IOException in case of an I/O related problem
     */
    public <T extends MzIdentMLObject> T getDataItem(String name, Class<T> type) throws IOException {
        JsonNode node = getTargetNode(name);

        List<T> item = new ArrayList<>();

        try {
            item.add(mapper.treeToValue(node, type));
        } catch (IOException e) {
            LOGGER.error("Unable to instantiate contact object of type " + type.getName(), e);
        }

        return item.get(0);
    }

    /**
     * Find a node by name or dot notation path.
     *
     * @param name Name or path
     * @return The node
     */
    private JsonNode getTargetNode(String name) throws IOException {
        JsonNode node;

        if (name.contains(".")) {
            String[] path = name.split("\\.");

            node = mzIdentMLParamList.get(path[0]);

            for (int i = 1; i < path.length; ++i) {
                node = node.get(path[i]);

                if (node == null) {
                    throw new IOException("Node " + name + " not found in data file.");
                }
            }
        } else {
            node = mzIdentMLParamList.get(name);
        }

        return node;
    }
}
