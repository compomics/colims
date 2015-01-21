package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.*;
import com.compomics.colims.repository.ExperimentRepository;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String MZIDENTML_VERSION = "1.1.0"; // TODO: version switch

    private Logger logger = Logger.getLogger(MzIdentMLExporter.class);

    private ObjectMapper mapper;
    private JsonNode mzIdentMLParamList;
    private AnalyticalRun analyticalRun;
    private Experiment experiment;
    private MzIdentML mzIdentML;
    private SearchDatabase searchDatabase;

    @Autowired
    private ExperimentRepository experimentRepository;

    /**
     * Set up the JSON mapper and map the data file
     */
    public void init() {
        if (mzIdentMLParamList == null || mapper == null) {
            mapper = new ObjectMapper();

            try {
                mzIdentMLParamList = mapper.readTree(this.getClass().getResource("/config/mzidentml.json"));
            } catch (IOException e) {
                logger.error("Unable to parse mzidentml.json, please ensure file is valid JSON.");
            }
        }
    }

    /**
     * Export a run in MzIdentML format
     */
    public String export(AnalyticalRun run) {
        init();

        MzIdentMLMarshaller marshaller = new MzIdentMLMarshaller();
        analyticalRun = run;
        experiment = experimentRepository.findById(run.getId());

        return marshaller.marshal(base());
    }

    /**
     * Assemble necessary data into an MZIdentML object and it's many properties
     * @return MZIdentML A fully furnished (hopefully) object
     */
    private MzIdentML base() {
        mzIdentML = new MzIdentML();

        mzIdentML.setId("colims_1.3.2");                                    // TODO: from where
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        mzIdentML.setCvList(cvList());                                      // done
        mzIdentML.setAuditCollection(auditCollection());
        mzIdentML.setProvider(provider());                                  // done
        mzIdentML.setDataCollection(dataCollection());
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList());
        //mzIdentML.setSequenceCollection(sequenceCollection());
        //mzIdentML.setAnalysisCollection(analysisCollection());
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
     * Where any people or orgs referenced elsewhere in the file must go
     * @return A collection of the above entities
     */
    private AuditCollection auditCollection() {
        AuditCollection auditCollection = new AuditCollection();

        // can be multiple of both of the below

        Person person = new Person();
        person.setId("1");                  // TODO
        person.setFirstName("Niels");
        person.setLastName("Hulstaert");
        // optional cv, user params
        // optional affiliation

        // TODO: either we iterate here or it gets created when the search engine is chosen
        Organization organization = new Organization();
        organization.setId("1");

        for (CvParam orgCv : getDataList("Organisation.PeptideShaker", CvParam.class)) {
            organization.getCvParam().add(orgCv);
        }

        auditCollection.getPerson().add(person);
        auditCollection.getOrganization().add(organization);

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

        // Search engine(s)
        // TODO: change to run basis, then it is not a foreach
        for (SearchAndValidationSettings settings : experiment.getSearchAndValidationSettingses()) {
            AnalysisSoftware software = getDataItem("AnalysisSoftware." + settings.getSearchEngine().getName(), AnalysisSoftware.class);

            ContactRole contactRole = new ContactRole();
            //TODO: contactRole.setContact(); (from audit collection)
            contactRole.setRole(new Role());
            contactRole.getRole().setCvParam(getDataItem("Role.software vendor", CvParam.class));

            software.setContactRole(contactRole);

            list.getAnalysisSoftware().add(software);
        }

        return list;
    }

    private DataCollection dataCollection() {
        DataCollection dataCollection = new DataCollection();

        Inputs inputs = new Inputs();

        // TODO: source file (in which table?)
        SourceFile sourceFile = new SourceFile();

        inputs.getSourceFile().add(sourceFile);

        FastaDb fasta = new FastaDb();  // TODO: replace

        searchDatabase = new SearchDatabase();
        searchDatabase.setId(fasta.getId().toString());
        searchDatabase.setLocation(fasta.getFilePath());
        searchDatabase.setName(fasta.getName());
        searchDatabase.setVersion(fasta.getVersion());
        searchDatabase.setFileFormat(new FileFormat());
        searchDatabase.setDatabaseName(new Param());
        searchDatabase.getFileFormat().setCvParam(getDataItem("FileFormat.FASTA", CvParam.class));

        UserParam databaseName = new UserParam();
        databaseName.setName(fasta.getName());

        searchDatabase.getDatabaseName().setParam(databaseName);

        inputs.getSearchDatabase().add(searchDatabase);

        AnalysisData analysisData = new AnalysisData();

        dataCollection.setInputs(inputs);

        return dataCollection;
    }

    /**
     * Gather all data relating to search and protein protocol settings
     * @return Analysis Protocol object
     */
    private AnalysisProtocolCollection analysisProtocolCollection() {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        SearchAndValidationSettings settings = new SearchAndValidationSettings(); // TODO: get this from run
        SearchParameters searchParameters = settings.getSearchParameters();

        // Spectrum Identification Protocol
        SpectrumIdentificationProtocol spectrumProtocol = new SpectrumIdentificationProtocol();
        // TODO: confirm one search engine per run
        spectrumProtocol.setId("1");
        spectrumProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0));
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
        spectrumProtocol.setAdditionalSearchParams(new ParamList());
        // TODO (optional)

        if (searchParameters.getSearchParametersHasModifications() != null) {   // TODO: is this valid?
            spectrumProtocol.setModificationParams(new ModificationParams());

            for (SearchParametersHasModification searchHasMod : searchParameters.getSearchParametersHasModifications()) {
                // TODO: from db to cv params
            }
        }

        // Enzyme
        spectrumProtocol.setEnzymes(new Enzymes());

        Enzyme mzEnzyme = new Enzyme();
        SearchCvParam colimsEnzyme = searchParameters.getEnzyme();
        CvParam cvEnzyme = new CvParam();

        cvEnzyme.setName(colimsEnzyme.getName());
        cvEnzyme.setAccession(colimsEnzyme.getAccession());
        // TODO: how to set cvref?

        mzEnzyme.setId(colimsEnzyme.getId().toString());
        mzEnzyme.setEnzymeName(new ParamList());
        mzEnzyme.getEnzymeName().getCvParam().add(cvEnzyme);

        spectrumProtocol.getEnzymes().getEnzyme().add(mzEnzyme);    // so messy

        // Fragment Tolerance
        spectrumProtocol.setFragmentTolerance(new Tolerance());

        CvParam fragmentMinus = getDataItem("Tolerance.minus", CvParam.class);
        fragmentMinus.setValue(searchParameters.getFragMassTolerance().toString());
        fragmentMinus.setUnitName(searchParameters.getFragMassToleranceUnit().toString()); // TODO: is this "dalton" or w/e?

        CvParam fragmentPlus = getDataItem("Tolerance.plus", CvParam.class);
        fragmentPlus.setValue(searchParameters.getFragMassTolerance().toString()); // TODO: are these values the same
        fragmentPlus.setUnitName(searchParameters.getFragMassToleranceUnit().toString());
        // TODO: where to get other cv terms (unitCvRef, unitAccession)?

        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentMinus);
        spectrumProtocol.getFragmentTolerance().getCvParam().add(fragmentPlus);

        // Parent Tolerance
        spectrumProtocol.setParentTolerance(new Tolerance());

        // can we reuse objects? seems unlikely
        CvParam parentMinus = getDataItem("Tolerance.minus", CvParam.class);
        parentMinus.setValue(searchParameters.getPrecMassTolerance().toString());
        parentMinus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString()); // TODO: as above

        CvParam parentPlus = getDataItem("Tolerance.plus", CvParam.class);
        parentPlus.setValue(searchParameters.getPrecMassTolerance().toString());
        parentPlus.setUnitName(searchParameters.getPrecMassToleranceUnit().toString()); // TODO: as above

        spectrumProtocol.getParentTolerance().getCvParam().add(parentMinus);
        spectrumProtocol.getParentTolerance().getCvParam().add(parentPlus);

        collection.getSpectrumIdentificationProtocol().add(spectrumProtocol);

        // Protein Detection Protocol
        ProteinDetectionProtocol proteinProtocol = new ProteinDetectionProtocol();
        proteinProtocol.setId("1");
        proteinProtocol.setAnalysisSoftware(mzIdentML.getAnalysisSoftwareList().getAnalysisSoftware().get(0));

        // Threshold #2
        proteinProtocol.setThreshold(new ParamList());
        // TODO: where is this stored?

        // Analysis Params
        // TODO

        collection.setProteinDetectionProtocol(proteinProtocol);

        return collection;
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

    private void assembleSpectrumData() {
        SpectrumIdentificationList spectrumIdentificationList = new SpectrumIdentificationList();
        ProteinDetectionList proteinDetectionList = new ProteinDetectionList(); // TODO: this is a collection of literal garbage
        SequenceCollection sequenceCollection = new SequenceCollection();
        AnalysisCollection analysisCollection = new AnalysisCollection();

        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
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

            SpectrumIdentification spectrumIdentification = new SpectrumIdentification();
            spectrumIdentification.setId(spectrum.getId().toString());
            spectrumIdentification.setSpectrumIdentificationList(spectrumIdentificationList);
            spectrumIdentification.setSpectrumIdentificationProtocol(mzIdentML.getAnalysisProtocolCollection().getSpectrumIdentificationProtocol().get(0)); // hmm 3

            // TODO: make this once
            SearchDatabaseRef dbRef = new SearchDatabaseRef();
            dbRef.setSearchDatabase(mzIdentML.getDataCollection().getInputs().getSearchDatabase().get(0)); // hmm 4

            spectrumIdentification.getSearchDatabaseRef().add(dbRef);

            for (SpectrumFile spectrumFile : spectrum.getSpectrumFiles()) {
                SpectraData spectraData = new SpectraData();
                spectraData.setId(spectrumFile.getId().toString());

                // TODO: need spectrum file location
                // TODO: need format of spectrum ids within file (CV term)

                mzIdentML.getDataCollection().getInputs().getSpectraData().add(spectraData);

                InputSpectra inputSpectra = new InputSpectra();
                inputSpectra.setSpectraData(spectraData);

                spectrumIdentification.getInputSpectra().add(inputSpectra);
            }

            analysisCollection.getSpectrumIdentification().add(spectrumIdentification);
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
        // TODO: residues?
        // TODO: cv param for modification?
        //modification.getCvParam().add(getDataItem(peptideHasMod.getModification().getAccession(), CvParam.class));

        return modification;
    }

    private DBSequence createDBSequence(PeptideHasProtein peptideHasProtein) {
        Protein protein = peptideHasProtein.getProtein();

        DBSequence dbSequence = new DBSequence();
        dbSequence.setId(protein.getId().toString());
        // TODO: accession requires single value
        dbSequence.setLength(protein.getSequence().length());
        dbSequence.setSeq(protein.getSequence());
        dbSequence.setSearchDatabase(mzIdentML.getDataCollection().getInputs().getSearchDatabase().get(0)); // hmm 2

        return dbSequence;
    }
}
