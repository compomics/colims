package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.*;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.repository.UserRepository;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Role;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import java.io.IOException;
import java.util.*;

/**
 * mzIdentML exporter, populates models from the jmzidml library then uses the MzIdentMLMarshaller to marshal them
 * into valid XML
 * @author Iain
 */
@Component
public class MzIdentMLExporter {
    // TODO: are these only filled in on production build?

    @Value("${mzidentml.version}")
    private static final String MZIDENTML_VERSION = "1.1.0";
    @Value("${colims-core.version}")
    private static final String COLIMS_VERSION = "latest";

    private Logger logger = Logger.getLogger(MzIdentMLExporter.class);

    private ObjectMapper mapper;
    private JsonNode mzIdentMLParamList;
    private AnalyticalRun analyticalRun;
    private MzIdentML mzIdentML;
    private SearchDatabase searchDatabase;

    @Autowired
    private UserRepository userRepository;

    /**
     * Set up JSON mapper and export a run in MzIdentML format
     */
    public String export(AnalyticalRun run) {
        mapper = new ObjectMapper();

        try {
            mzIdentMLParamList = mapper.readTree(this.getClass().getResource("/config/mzidentml.json"));
        } catch (IOException e) {
            logger.error("Unable to parse mzidentml.json, please ensure file is valid JSON.");
        }

        MzIdentMLMarshaller marshaller = new MzIdentMLMarshaller();
        analyticalRun = run;

        // TODO: searchandvalidation as an instance var?

        return marshaller.marshal(base());
    }

    /**
     * Assemble necessary data into an MZIdentML object and it's many properties
     * @return MZIdentML A fully furnished (hopefully) object
     */
    private MzIdentML base() {
        mzIdentML = new MzIdentML();

        mzIdentML.setId("colims-" + COLIMS_VERSION);
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        mzIdentML.setCvList(cvList());
        mzIdentML.setAuditCollection(auditCollection());
        mzIdentML.setProvider(provider());
        mzIdentML.setDataCollection(dataCollection());
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList());
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection());
        assembleSpectrumData();

        return mzIdentML;
    }

    /**
     * Construct a list of CV sources used in the file
     * @return CvList List of CV sources
     */
    private CvList cvList() {
        CvList cvList = new CvList();

        cvList.getCv().addAll(getDataList("CvList", Cv.class));

        return cvList;
    }

    /**
     * Create collection for associated entities, add the owner of this run
     * @return An audit collection
     */
    private AuditCollection auditCollection() {
        AuditCollection auditCollection = new AuditCollection();

        User user = userRepository.findByName(analyticalRun.getUserName());

        Person person = new Person();
        person.setId(user.getId().toString());
        person.setFirstName(user.getFirstName());
        person.setLastName(user.getLastName());

        CvParam email = getDataItem("Person.email", CvParam.class);
        email.setValue(user.getEmail());

        person.getCvParam().add(email);

        auditCollection.getPerson().add(person);

        return auditCollection;
    }

    /**
     * Create the contact and software provider element
     * @return Provider element
     */
    private Provider provider() {
        Provider provider = new Provider();
        provider.setId("PROVIDER");
        provider.setContactRole(new ContactRole());     // no method to set contact_ref on ContactRole

        Role role = new Role();
        role.setCvParam(getDataItem("Role.researcher", CvParam.class));

        provider.getContactRole().setRole(role);

        return provider;
    }

    private AnalysisSoftwareList analysisSoftwareList() {
        AnalysisSoftwareList list = new AnalysisSoftwareList();

        SearchAndValidationSettings settings = analyticalRun.getSearchAndValidationSettings();

        AnalysisSoftware software = getDataItem("AnalysisSoftware." + settings.getSearchEngine().getName(), AnalysisSoftware.class);

        ContactRole contactRole = new ContactRole();
        contactRole.setContact(getContact(settings.getSearchEngine().getName(), Organization.class));
        contactRole.setRole(new Role());
        contactRole.getRole().setCvParam(getDataItem("Role.software vendor", CvParam.class));

        software.setContactRole(contactRole);

        // TODO: other software?

        list.getAnalysisSoftware().add(software);

        return list;
    }

    private DataCollection dataCollection() {
        DataCollection dataCollection = new DataCollection();

        Inputs inputs = new Inputs();

        // TODO: source file (in which table?)
        SourceFile sourceFile = new SourceFile();

        inputs.getSourceFile().add(sourceFile);

        FastaDb fasta = analyticalRun.getSearchAndValidationSettings().getFastaDb();

        searchDatabase = new SearchDatabase();
        searchDatabase.setId(fasta.getId().toString());
        searchDatabase.setLocation(fasta.getFilePath());
        searchDatabase.setName(fasta.getName());
        searchDatabase.setVersion(fasta.getVersion());
        searchDatabase.setFileFormat(new FileFormat());
        searchDatabase.setDatabaseName(new Param());
        searchDatabase.getFileFormat().setCvParam(getDataItem("FileFormat.FASTA", CvParam.class));
        searchDatabase.getCvParam().add(getDataItem("SearchDatabase.type", CvParam.class));

        UserParam databaseName = new UserParam();
        databaseName.setName(fasta.getName());

        searchDatabase.getDatabaseName().setParam(databaseName);

        inputs.getSearchDatabase().add(searchDatabase);

        dataCollection.setAnalysisData(new AnalysisData());

        dataCollection.setInputs(inputs);

        return dataCollection;
    }

    /**
     * Gather all data relating to search and protein protocol settings
     * @return Analysis Protocol object
     */
    private AnalysisProtocolCollection analysisProtocolCollection() {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        SearchAndValidationSettings settings = analyticalRun.getSearchAndValidationSettings();
        SearchParameters searchParameters = settings.getSearchParameters();

        // Spectrum Identification Protocol
        SpectrumIdentificationProtocol spectrumProtocol = new SpectrumIdentificationProtocol();

        spectrumProtocol.setId("1");
        spectrumProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0)); // bad
        spectrumProtocol.setSearchType(new Param());
        spectrumProtocol.getSearchType().setParam(getDataItem("SearchType.ms-ms", CvParam.class));

        // Threshold
        spectrumProtocol.setThreshold(new ParamList());

        if (searchParameters.getThreshold() == null) {
            spectrumProtocol.getThreshold().getCvParam().add(getDataItem("Threshold.no", CvParam.class));
        } else {
            // TODO: need threshold type
        }

        // Additional Params
        //spectrumProtocol.setAdditionalSearchParams(new ParamList());
        // TODO (optional)

        if (searchParameters.getSearchParametersHasModifications() != null) {   // TODO: is this valid?
            spectrumProtocol.setModificationParams(new ModificationParams());

            for (SearchParametersHasModification searchHasMod : searchParameters.getSearchParametersHasModifications()) {
                uk.ac.ebi.jmzidml.model.mzidml.SearchModification searchMod = new uk.ac.ebi.jmzidml.model.mzidml.SearchModification();
                searchMod.setFixedMod(searchHasMod.getModificationType() == ModificationType.FIXED);
                // TODO: searchMod.getResidues
                // TODO: searchMod.setMassDelta
                // TODO: accession as cv param

                spectrumProtocol.getModificationParams().getSearchModification().add(searchMod);
            }
        }

        // Enzyme
        spectrumProtocol.setEnzymes(new Enzymes());

        Enzyme mzEnzyme = new Enzyme();
        SearchCvParam colimsEnzyme = searchParameters.getEnzyme();
        CvParam cvEnzyme = new CvParam();

        cvEnzyme.setName(colimsEnzyme.getName());
        cvEnzyme.setAccession(colimsEnzyme.getAccession());
        // TODO: cvref missing from cv term

        mzEnzyme.setId(colimsEnzyme.getId().toString());
        mzEnzyme.setEnzymeName(new ParamList());
        mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);

        spectrumProtocol.getEnzymes().getEnzyme().add(mzEnzyme);    // so messy

        // Fragment Tolerance
        spectrumProtocol.setFragmentTolerance(new Tolerance());

        CvParam fragmentMinus = getDataItem("Tolerance.minus", CvParam.class);
        fragmentMinus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentMinus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());

        CvParam fragmentPlus = getDataItem("Tolerance.plus", CvParam.class);
        fragmentPlus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentPlus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());
        // TODO: where to get other cv terms (unitCvRef, unitAccession)?

        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentMinus);
        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentPlus);

        // Parent Tolerance
        /*spectrumProtocol.setParentTolerance(new Tolerance());

        CvParam parentMinus = getDataItem("Tolerance.minus", CvParam.class);
        parentMinus.setValue(searchParameters.getPrecMassTolerance().toString());
        parentMinus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString()); // TODO: as above

        CvParam parentPlus = getDataItem("Tolerance.plus", CvParam.class);
        parentPlus.setValue(searchParameters.getPrecMassTolerance().toString());
        parentPlus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString()); // TODO: as above

        spectrumProtocol.getParentTolerance().getCvParam().add(parentMinus);
        spectrumProtocol.getParentTolerance().getCvParam().add(parentPlus);*/

        collection.getSpectrumIdentificationProtocol().add(spectrumProtocol);

        // Protein Detection Protocol
        ProteinDetectionProtocol proteinProtocol = new ProteinDetectionProtocol();
        proteinProtocol.setId("1");
        proteinProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0));

        proteinProtocol.setThreshold(new ParamList());

        if (searchParameters.getThreshold() == null) {
            proteinProtocol.getThreshold().getCvParam().add(getDataItem("Threshold.no", CvParam.class));
        } else {
            // TODO: need threshold type
        }

        collection.setProteinDetectionProtocol(proteinProtocol);

        return collection;
    }

    private void assembleSpectrumData() {
        SpectrumIdentificationList spectrumIdentificationList = new SpectrumIdentificationList();
        spectrumIdentificationList.setId("1");

        ProteinDetectionList proteinDetectionList = new ProteinDetectionList();
        proteinDetectionList.setId("1");

        SequenceCollection sequenceCollection = new SequenceCollection();
        AnalysisCollection analysisCollection = new AnalysisCollection();

        FileFormat spectrumFileFormat = new FileFormat();
        spectrumFileFormat.setCvParam(getDataItem("FileFormat.Mascot MGF", CvParam.class));

        SpectrumIDFormat spectrumIDFormat = new SpectrumIDFormat();
        spectrumIDFormat.setCvParam(getDataItem("SpectrumIDFormat.mascot query number", CvParam.class));

        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
            SpectrumIdentification spectrumIdentification = new SpectrumIdentification();
            spectrumIdentification.setId(spectrum.getId().toString());

            for (SpectrumFile spectrumFile : spectrum.getSpectrumFiles()) {
                SpectraData spectraData = new SpectraData();
                spectraData.setId(spectrumFile.getId().toString());
                spectraData.setFileFormat(spectrumFileFormat);
                spectraData.setSpectrumIDFormat(spectrumIDFormat);

                mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

                InputSpectra inputSpectra = new InputSpectra();
                inputSpectra.setSpectraData(spectraData);

                spectrumIdentification.getInputSpectra().add(inputSpectra);
            }

            SpectrumIdentificationResult spectrumIdentificationResult = createSpectrumIdentificationResult(spectrum);
            SpectrumIdentificationItem spectrumIdentificationItem = createSpectrumIdentificationItem(spectrum);

            for (com.compomics.colims.model.Peptide colimsPeptide : spectrum.getPeptides()) {
                uk.ac.ebi.jmzidml.model.mzidml.Peptide mzPeptide = new uk.ac.ebi.jmzidml.model.mzidml.Peptide(); // urgh

                mzPeptide.setId(colimsPeptide.getId().toString());
                mzPeptide.setPeptideSequence(colimsPeptide.getSequence());

                for (PeptideHasModification peptideHasMod : colimsPeptide.getPeptideHasModifications()) {
                    mzPeptide.getModification().add(createModification(peptideHasMod));
                }

                sequenceCollection.getPeptide().add(mzPeptide);
                spectrumIdentificationItem.setPeptide(mzPeptide);

                for (PeptideHasProtein peptideHasProtein : colimsPeptide.getPeptideHasProteins()) {
                    DBSequence dbSequence = createDBSequence(peptideHasProtein);

                    sequenceCollection.getDBSequence().add(dbSequence);

                    PeptideEvidence evidence = new PeptideEvidence();
                    evidence.setDBSequence(dbSequence);
                    evidence.setPeptide(mzPeptide);
                    evidence.setId(peptideHasProtein.getId().toString());
                    // TODO: a lot of missing fields here

                    sequenceCollection.getPeptideEvidence().add(evidence);

                    PeptideEvidenceRef evidenceRef = new PeptideEvidenceRef();
                    evidenceRef.setPeptideEvidence(evidence);
                    spectrumIdentificationItem.getPeptideEvidenceRef().add(evidenceRef);
                }
            }

            spectrumIdentificationResult.getSpectrumIdentificationItem().add(spectrumIdentificationItem);
            spectrumIdentificationList.getSpectrumIdentificationResult().add(spectrumIdentificationResult);

            spectrumIdentification.setSpectrumIdentificationList(spectrumIdentificationList);
            spectrumIdentification.setSpectrumIdentificationProtocol(mzIdentML.getAnalysisProtocolCollection().getSpectrumIdentificationProtocol().get(0)); // hmm 3

            // TODO: make this once
            SearchDatabaseRef dbRef = new SearchDatabaseRef();
            dbRef.setSearchDatabase(mzIdentML.getDataCollection().getInputs().getSearchDatabase().get(0)); // hmm 4

            spectrumIdentification.getSearchDatabaseRef().add(dbRef);

            for (SpectrumFile spectrumFile : spectrum.getSpectrumFiles()) {
                SpectraData spectraData = new SpectraData();
                spectraData.setId(spectrumFile.getId().toString());
                spectraData.setLocation(spectrum.getTitle());   // TODO: is it a filename


                // TODO: need spectrum file location
                // TODO: need format of spectrum ids within file (CV term)

                mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

                InputSpectra inputSpectra = new InputSpectra();
                inputSpectra.setSpectraData(spectraData);

                spectrumIdentification.getInputSpectra().add(inputSpectra);
            }

            analysisCollection.getSpectrumIdentification().add(spectrumIdentification);

            ProteinDetection proteinDetection = new ProteinDetection();
            proteinDetection.setId("1");
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

    private SpectrumIdentificationResult createSpectrumIdentificationResult(Spectrum spectrum) {
        SpectrumIdentificationResult spectrumIdentificationResult = new SpectrumIdentificationResult();

        spectrumIdentificationResult.setId(spectrum.getId().toString());
        spectrumIdentificationResult.setSpectraData(mzIdentML.getDataCollection().getInputs().getSpectraData().get(0));  // TODO: ??
        spectrumIdentificationResult.setSpectrumID(spectrum.getId().toString());

        return spectrumIdentificationResult;
    }

    private SpectrumIdentificationItem createSpectrumIdentificationItem(Spectrum spectrum) {
        SpectrumIdentificationItem spectrumIdentificationItem = new SpectrumIdentificationItem();

        spectrumIdentificationItem.setId(spectrum.getId().toString());    // this id is being used a lot!
        spectrumIdentificationItem.setChargeState(spectrum.getCharge());
        spectrumIdentificationItem.setExperimentalMassToCharge(spectrum.getMzRatio());
        spectrumIdentificationItem.setPassThreshold(true);    // TODO: confirm
        spectrumIdentificationItem.setRank(0);                // TODO: confirm

        // TODO: fragmentation

        return spectrumIdentificationItem;
    }

    private Modification createModification(PeptideHasModification peptideHasMod) {
        Modification modification = new Modification();

        modification.setMonoisotopicMassDelta(peptideHasMod.getModification().getMonoIsotopicMassShift());
        modification.setAvgMassDelta(peptideHasMod.getDeltaScore());    // TODO: correct value?
        modification.setLocation(peptideHasMod.getLocation());
        // TODO: cv param for modification
        //modification.getCvParam().add(getDataItem(peptideHasMod.getModification().getAccession(), CvParam.class));

        return modification;
    }

    private DBSequence createDBSequence(PeptideHasProtein peptideHasProtein) {
        Protein protein = peptideHasProtein.getProtein();

        DBSequence dbSequence = new DBSequence();
        dbSequence.setId(protein.getId().toString());
        dbSequence.setAccession(peptideHasProtein.getMainGroupProtein().getProteinAccessions().get(0).getAccession());
        dbSequence.setLength(protein.getSequence().length());
        dbSequence.setSeq(protein.getSequence());
        dbSequence.setSearchDatabase(mzIdentML.getDataCollection().getInputs().getSearchDatabase().get(0)); // hmm 2
        // optional cv param to describe protein

        return dbSequence;
    }

    // TODO: not sure it is necessary to be so abstract
    private <T extends AbstractContact> T getContact(String name, Class<T> type) {
        T contact = null;
        try {
            contact = type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        contact.setId(name);
        contact.getCvParam().addAll(getDataList(type.getSimpleName() + "." + name, CvParam.class));

        mzIdentML.getAuditCollection().getPersonOrOrganization().add(contact);

        return contact;
    }

    /**
     * Get a list of data items mapped to the specified object type
     * @param name Name of key or dot notation path to key
     * @param type Type of objects to return
     * @param <T> Mister
     * @return List of objects of type T
     */
    public <T extends MzIdentMLObject> List<T> getDataList(String name, Class<T> type) {
        JsonNode listNode = getTargetNode(name);

        List<T> data = new ArrayList<>();

        if (!listNode.isArray()) {
            // TODO: some kind of exception
        }

        try {
            for (JsonNode node : listNode) {
                data.add(mapper.readValue(node, type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Get a single data item in the specified object type
     * @param name Name of key or dot notation path to key
     * @param type Type of object to be returned
     * @param <T> Cup of
     * @return Object of type T
     */
    public <T extends MzIdentMLObject> T getDataItem(String name, Class<T> type) {
        JsonNode node = getTargetNode(name);

        if (node.isArray()) { // or a map?
            // TODO: some kind of exception
        }

        // suspicious list wrapping
        List<T> item = new ArrayList<>();

        try {
            item.add(mapper.readValue(node, type));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return item.get(0);
    }

    /**
     * Find a node by name or dot notation path
     * @param name Name or path
     * @return The node
     */
    private JsonNode getTargetNode(String name) {
        JsonNode node;

        if (name.contains(".")) {
            String[] path = name.split("\\.");

            node = mzIdentMLParamList.get(path[0]);

            for (int i = 1; i < path.length; ++i) {
                node = node.get(path[i]);
            }
        } else {
            node = mzIdentMLParamList.get(name);
        }

        return node;
    }
}
