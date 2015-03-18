package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.*;
import com.compomics.colims.repository.ExperimentRepository;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.jmzidml.model.mzidml.Role;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Created by Iain on 13/01/2015.
 */
@Component
public class MzIdentMLExporter {
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzIdentMLExporter.class);

    private static final String MZIDENTML_VERSION = "1.1.0"; // TODO: version switch

    private MzIdentMLMarshaller marshaller;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode mzIdentMLParamList;
    private AnalyticalRun analyticalRun;
    private Experiment experiment;

    @Autowired
    private ExperimentRepository experimentRepository;

    @PostConstruct
    public void init() {
        Resource mzIdentMlJson = new ClassPathResource("/config/mzidentml.json");

        try {
            mzIdentMLParamList = mapper.readTree(mzIdentMlJson.getFile());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Export a run in MzIdentML format.
     */
    public String export(AnalyticalRun run) throws IOException {
        init();

        marshaller = new MzIdentMLMarshaller();
        analyticalRun = run;
        experiment = experimentRepository.findById(run.getId());

        return marshaller.marshal(base());
    }

    /**
     * Assemble necessary data into an MZIdentML object and it's many properties.
     *
     * @return MZIdentML A fully furnished (hopefully) object
     */
    private MzIdentML base() throws IOException {
        MzIdentML mzIdentML = new MzIdentML();

        mzIdentML.setId("colims_1.3.2");                                    // TODO: from where
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        mzIdentML.setCvList(cvList());                                      // done
        mzIdentML.setAuditCollection(auditCollection());
        mzIdentML.setProvider(provider());                                  // done
        mzIdentML.setDataCollection(dataCollection());
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList());
        mzIdentML.setSequenceCollection(sequenceCollection());
        mzIdentML.setAnalysisCollection(analysisCollection());
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection());

        return mzIdentML;
    }

    /**
     * Construct a list of CV sources used in the file.
     *
     * @return CvList List of CV sources
     */
    private CvList cvList() {
        CvList cvList = new CvList();

        cvList.getCv().addAll(getDataList("CvList", Cv.class));

        return cvList;
    }

    /**
     * Where any people or orgs referenced elsewhere in the file must go.
     *
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

        Organization organization = new Organization();
        organization.setId("1");            // TODO
        organization.setName("VIB");
        // optional parent

        auditCollection.getPerson().add(person);
        auditCollection.getOrganization().add(organization);

        return auditCollection;
    }

    /**
     * Create the contact and software provider element.
     *
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
        // TODO: expecting search engine to be on a run basis, correct to get all for experiment?
        SearchAndValidationSettings settings = analyticalRun.getSearchAndValidationSettings();
        AnalysisSoftware software = getDataItem("AnalysisSoftware." + settings.getSearchEngine().getName(), AnalysisSoftware.class);

        ContactRole contactRole = new ContactRole();
        //TODO: contactRole.setContact(); (from audit collection)
        contactRole.setRole(new Role());
        contactRole.getRole().setCvParam(getDataItem("Role.software vendor", CvParam.class));

        software.setContactRole(contactRole);

        list.getAnalysisSoftware().add(software);

        return list;
    }

    private DataCollection dataCollection() {
        DataCollection dataCollection = new DataCollection();

        Inputs inputs = new Inputs();

        // TODO: search result files
        // TODO: search database(s)
        // TODO: spectrum files

        return dataCollection;
    }

    /**
     * Assemble protein and peptide data into a sequence collection.
     *
     * @return A sequence collection
     */
    private SequenceCollection sequenceCollection() {
        SequenceCollection collection = new SequenceCollection();

        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
            for (com.compomics.colims.model.Peptide colimsPeptide : spectrum.getPeptides()) {
                uk.ac.ebi.jmzidml.model.mzidml.Peptide mzPeptide = new uk.ac.ebi.jmzidml.model.mzidml.Peptide(); // urgh

                mzPeptide.setId(colimsPeptide.getId().toString());
                mzPeptide.setPeptideSequence(colimsPeptide.getSequence());

                for (PeptideHasModification peptideHasMod : colimsPeptide.getPeptideHasModifications()) {
                    Modification modification = new Modification();
                    modification.setMonoisotopicMassDelta(peptideHasMod.getModification().getMonoIsotopicMassShift());
                    modification.setAvgMassDelta(peptideHasMod.getDeltaScore());    // TODO: correct value?
                    modification.setLocation(peptideHasMod.getLocation());
                    // TODO: residues?
                    // TODO: cv param for modification?
                    //modification.getCvParam().add(getDataItem(peptideHasMod.getModification().getAccession(), CvParam.class));

                    mzPeptide.getModification().add(modification);
                }

                collection.getPeptide().add(mzPeptide);

                for (PeptideHasProtein peptideHasProtein : colimsPeptide.getPeptideHasProteins()) {
                    Protein protein = peptideHasProtein.getProtein();

                    DBSequence dbSequence = new DBSequence();
                    dbSequence.setId(protein.getId().toString());
                    // TODO: accession requires single value
                    dbSequence.setLength(protein.getSequence().length());
                    dbSequence.setSeq(protein.getSequence());
                    // TODO: dbSequence.setSearchDatabase();

                    collection.getDBSequence().add(dbSequence);

                    PeptideEvidence evidence = new PeptideEvidence();
                    evidence.setDBSequence(dbSequence);
                    evidence.setPeptide(mzPeptide);
                    evidence.setId(peptideHasProtein.getId().toString());
                    // TODO: a lot of missing fields here

                    collection.getPeptideEvidence().add(evidence);
                }
            }
        }

        return collection;
    }

    private AnalysisCollection analysisCollection() {
        AnalysisCollection collection = new AnalysisCollection();

        SpectrumIdentification identification = new SpectrumIdentification();

        // TODO: todo todo

        return collection;
    }

    private AnalysisProtocolCollection analysisProtocolCollection() {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        // TODO: this one has a whole lotta settings

        return collection;
    }

    /**
     * This needs a better name ASAP.
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    public <T extends MzIdentMLObject> List<T> getDataList(String name, Class<T> type) {
        // TODO: WHAT IF IT IS A MAP ARGH

        JsonNode listNode = mzIdentMLParamList.get(name);
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
     * Get a single data item in the specified object type.
     *
     * @param name Name of key or dot notation path to key
     * @param type Type of object to be returned
     * @param <T>  It's a T.
     * @return Object of type T
     */
    public <T extends MzIdentMLObject> T getDataItem(String name, Class<T> type) {
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
    
}
