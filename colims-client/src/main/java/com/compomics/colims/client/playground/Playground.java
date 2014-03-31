package com.compomics.colims.client.playground;

import com.compomics.colims.core.bean.PtmFactoryWrapper;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import org.apache.commons.compress.archivers.ArchiveException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground {

    public static void main(String[] args) throws IOException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, XmlPullParserException, ArchiveException {
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-client-context.xml");
//
//        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
//        PeptideShakerImportMapper peptideShakerImportMapper = applicationContext.getBean("peptideShakerImportMapper", PeptideShakerImportMapper.class);
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
//        UnpackedPsDataImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project.cps").getFile());
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

        List<String> colors = new ArrayList<String>();
        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            if (entry.getValue() instanceof Color) {
                colors.add((String) entry.getKey()); // all the keys are strings
            }
        }
        Collections.sort(colors);
        for (String name : colors) {
            System.out.println(name);
        }
    }
}
