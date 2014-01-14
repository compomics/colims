package com.compomics.colims.core.playground;

import com.compomics.colims.core.component.PtmFactoryWrapper;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.PeptideShakerImportMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.spring.ApplicationContextProvider;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Niels Hulstaert
 */
public class Playground2 {

    public static void main(String[] args) throws XmlPullParserException, IOException, PeptideShakerIOException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
        PeptideShakerImportMapper peptideShakerImportMapper = applicationContext.getBean("peptideShakerImportMapper", PeptideShakerImportMapper.class);
        UserService userService = applicationContext.getBean("userService", UserService.class);
        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
        PtmFactoryWrapper ptmFactoryWrapper = applicationContext.getBean("ptmFactoryWrapper", PtmFactoryWrapper.class);
        AuthenticationBean authenticationBean = applicationContext.getBean("authenticationBean", AuthenticationBean.class);

        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("searchGUI_mods.xml");
        ptmFactoryWrapper.getPtmFactory().clearFactory();
        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userService.fetchAuthenticationRelations(adminUser);
        authenticationBean.setCurrentUser(adminUser);

        //import PeptideShaker .cps file
        PeptideShakerImport peptideShakerImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
        //set mgf files and fasta file
        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("input_spectra.mgf").getFile());
        peptideShakerImport.setMgfFiles(mgfFiles);
        peptideShakerImport.setFastaFile(new ClassPathResource("uniprot_sprot_101104_human_concat.fasta").getFile());

        List<AnalyticalRun> analyticalRuns = peptideShakerImportMapper.map(peptideShakerImport);

        //get sample from db
        Sample sample = sampleService.findAll().get(0);

        //set sample and persist
        for (AnalyticalRun analyticalRun : analyticalRuns) {
            analyticalRun.setSample(sample);
            analyticalRunService.save(analyticalRun);
        }
    }

}
