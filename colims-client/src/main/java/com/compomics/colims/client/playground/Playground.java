package com.compomics.colims.client.playground;

import com.compomics.colims.core.io.MappingException;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) throws IOException, MappingException, SQLException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException {
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");
//
//        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
//        PeptideShakerImporter peptideShakerImportMapper = applicationContext.getBean("peptideShakerImportMapper", PeptideShakerImporter.class);
//        UserService userService = applicationContext.getBean("userService", UserService.class);
//        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
//        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
//        PtmFactoryWrapper ptmFactoryWrapper = applicationContext.getBean("ptmFactoryWrapper", PtmFactoryWrapper.class);
//        AuthenticationBean authenticationBean = applicationContext.getBean("authenticationBean", AuthenticationBean.class);
//
//        //load mods from test resources instead of user folder
//        Resource utilitiesMods = new ClassPathResource("searchGUI_mods.xml");
//        ptmFactoryWrapper.getPtmFactory().clearFactory();
//        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);
//
//        //set admin user in authentication bean
//        User adminUser = userService.findByName("admin1");
//        userService.fetchAuthenticationRelations(adminUser);
//        authenticationBean.setCurrentUser(adminUser);
//
//        //import PeptideShaker .cps file
//        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project.cps").getFile());
//        //set mgf files and fasta file
//        List<File> mgfFiles = new ArrayList<>();
//        mgfFiles.add(new ClassPathResource("input_spectra.mgf").getFile());
//        unpackedPsDataImport.setMgfFiles(mgfFiles);
//        unpackedPsDataImport.setFastaFile(new ClassPathResource("uniprot_sprot_101104_human_concat.fasta").getFile());
//
//        List<AnalyticalRun> analyticalRuns = peptideShakerImportMapper.map(unpackedPsDataImport);
//
//        //get sample from db
//        Sample sample = sampleService.findAll().get(0);
//
//        //set sample and persist
//        for (AnalyticalRun analyticalRun : analyticalRuns) {
//            analyticalRun.setSample(sample);
//            analyticalRunService.save(analyticalRun);
//        }

    }
}
