package com.compomics.colims.core.io.mzidentml;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import com.compomics.colims.model.Peptide;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLMarshaller;

import java.io.IOException;
import java.util.*;

/**
 * Created by Iain on 13/01/2015.
 */
@Component
public class MzIdentMLExporter {
    private static final String MZIDENTML_VERSION = "1.1.0"; // TODO: version switch

    private MzIdentMLMarshaller marshaller;
    private ObjectMapper mapper;
    private JsonNode mzIdentMLParamList;
    private AnalyticalRun analyticalRun;        // TODO: just pass it through if only in the one method

    /**
     * Export a run in MzIdentML format
     */
    public void export(/*AnalyticalRun run*/) throws IOException {
        marshaller = new MzIdentMLMarshaller();
        mapper = new ObjectMapper();
        mzIdentMLParamList = mapper.readTree(this.getClass().getResource("/config/mzidentml.json"));

        System.out.println(marshaller.marshal(base()));
    }

    /**
     * Assemble necessary data into an MZIdentML object and it's many properties
     * @return MZIdentML A fully furnished (hopefully) object
     */
    private MzIdentML base() throws IOException {
        MzIdentML mzIdentML = new MzIdentML();

        mzIdentML.setId("colims_1.3.2-SNAPSHOT");                        // TODO: from where
        mzIdentML.setVersion(MZIDENTML_VERSION);
        mzIdentML.setCreationDate(new GregorianCalendar());

        mzIdentML.setCvList(cvList());
        mzIdentML.setAnalysisSoftwareList(analysisSoftwareList());
        mzIdentML.setProvider(provider());
        mzIdentML.setAuditCollection(auditCollection());
        mzIdentML.setSequenceCollection(sequenceCollection());
        mzIdentML.setAnalysisCollection(analysisCollection());
        mzIdentML.setAnalysisProtocolCollection(analysisProtocolCollection());
        mzIdentML.setDataCollection(dataCollection());

        return mzIdentML;
    }

    /**
     * Construct a list of CV sources used in the file
     * @return CvList List of CV sources
     */
    private CvList cvList() throws IOException {
        CvList cvList = new CvList();

        for (JsonNode node : mzIdentMLParamList.get("cvList")) {
            cvList.getCv().add(mapper.readValue(node, Cv.class));
        }

        return cvList;
    }

    private AnalysisSoftwareList analysisSoftwareList() {
        AnalysisSoftwareList list = new AnalysisSoftwareList();

        AnalysisSoftware software = new AnalysisSoftware();
        software.setId("1");                                // TODO
        software.setName("Colims");
        software.setVersion("1.0");                         // TODO
        software.setUri("http://colims.googlecode.com");    // TODO: confirm

        CvParam softwareName = new CvParam();               // TODO

        list.getAnalysisSoftware().add(software);

        // optional customisations
        // optional contact role

        return list;
    }

    /**
     * Create the contact and software provider element
     * @return Provider element
     */
    private Provider provider() {
        Provider provider = new Provider();
        provider.setId("PROVIDER");                 // TODO: ?
        provider.setContactRole(new ContactRole()); // TODO: does this set contact_ref automatically

        Role role = new Role();
        role.setCvParam(new CvParam());             // TODO (need method to create/return CV params)

        provider.getContactRole().setRole(role);

        return provider;
    }

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

    private SequenceCollection sequenceCollection() {
        SequenceCollection collection = new SequenceCollection();

        // split into proteins (DBSequences), peptides and peptideevidences
        // the evidence object joins the two

        // TODO: how to do this without a horrible nest of loops

//        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
//            for (Peptide peptide : spectrum.getPeptides()) {
//                peptide.getPeptideHasProteins();
//            }
//        }

        return collection;
    }

    private AnalysisCollection analysisCollection() {
        AnalysisCollection collection = new AnalysisCollection();

        // TODO: todo todo

        return collection;
    }

    private AnalysisProtocolCollection analysisProtocolCollection() {
        AnalysisProtocolCollection collection = new AnalysisProtocolCollection();

        // TODO: this one has a whole lotta settings

        return  collection;
    }

    private DataCollection dataCollection() {
        DataCollection dataCollection = new DataCollection();

        Inputs inputs = new Inputs();

        // TODO: search result files
        // TODO: search database(s)
        // TODO: spectrum files

        return dataCollection;
    }

    // get CVDataSet
    // get CVParamDataSet
    // get CVParam
    // ... etc

    private ArrayList<Object> getDataSet(String name) {
        JsonNode listNode = mzIdentMLParamList.get(name);


        if (!listNode.isArray()) {
            // TODO: some kind of exception
        }

        ArrayList<Object> paramList = new ArrayList<>();

        try {
            for (JsonNode node : listNode) {
                paramList.add(mapper.readValue(node, Object.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return paramList;
    }
}
