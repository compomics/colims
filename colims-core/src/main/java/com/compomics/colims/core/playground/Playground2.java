package com.compomics.colims.core.playground;

import com.compomics.colims.core.bean.PtmFactoryWrapper;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImporter;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesModificationMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveException;
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

    public static void main(String[] args) throws XmlPullParserException, IOException, MappingException, SQLException, FileNotFoundException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, ArchiveException {
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
        PeptideShakerImporter peptideShakerImporter = applicationContext.getBean("peptideShakerImporter", PeptideShakerImporter.class);
        UserService userService = applicationContext.getBean("userService", UserService.class);
        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
        PtmFactoryWrapper ptmFactoryWrapper = applicationContext.getBean("ptmFactoryWrapper", PtmFactoryWrapper.class);
        AuthenticationBean authenticationBean = applicationContext.getBean("authenticationBean", AuthenticationBean.class);
        UtilitiesProteinMapper utilitiesProteinMapper = applicationContext.getBean("utilitiesProteinMapper", UtilitiesProteinMapper.class);
        UtilitiesPeptideMapper utilitiesPeptideMapper = applicationContext.getBean("utilitiesPeptideMapper", UtilitiesPeptideMapper.class);
        PeptideService peptideService = applicationContext.getBean("peptideService", PeptideService.class);
        
        //load mods from test resources instead of user folder
        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
        ptmFactoryWrapper.getPtmFactory().clearFactory();
        ptmFactoryWrapper.getPtmFactory().importModifications(utilitiesMods.getFile(), false);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(adminUser);
        authenticationBean.setCurrentUser(adminUser);

        //import PeptideShaker .cps file
        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
        //set mgf files and fasta file
        List<File> mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("data/peptideshaker/input_spectra.mgf").getFile());
        unpackedPsDataImport.setMgfFiles(mgfFiles);
        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
        FastaDb fastaDb = new FastaDb();
        fastaDb.setName(fastaFile.getName());
        fastaDb.setFileName(fastaFile.getName());
        fastaDb.setFilePath(fastaFile.getAbsolutePath());
        unpackedPsDataImport.setFastaDb(fastaDb);

//        List<AnalyticalRun> analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        //get sample from db
        Sample sample = sampleService.findAll().get(0);

        //set sample and persist
        for (AnalyticalRun analyticalRun : analyticalRuns) {
            //set modification and creation date
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            analyticalRun.setUserName(authenticationBean.getCurrentUser().getName());
            analyticalRun.setSample(sample);
            analyticalRunService.saveOrUpdate(analyticalRun);
        }
        
        //import PeptideShaker .cps file
        unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
        //set mgf files and fasta file
        mgfFiles = new ArrayList<>();
        mgfFiles.add(new ClassPathResource("small_scale/example.mgf").getFile());
        unpackedPsDataImport.setMgfFiles(mgfFiles);
        unpackedPsDataImport.setFastaDb(fastaDb);

//        analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);

        //get sample from db
        sample = sampleService.findAll().get(0);

        //set sample and persist
        for (AnalyticalRun analyticalRun : analyticalRuns) {
            //set modification and creation date
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            analyticalRun.setUserName(authenticationBean.getCurrentUser().getName());
            analyticalRun.setSample(sample);
            analyticalRunService.saveOrUpdate(analyticalRun);
        }
        
//        //load SequenceFactory for testing
//        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
//        SequenceFactory.getInstance().loadFastaFile(fastaFile);
//        
//        //set admin user in authentication bean
//        User adminUser = userService.findByName("admin1");
//        userService.fetchAuthenticationRelations(adminUser);
//        authenticationBean.setCurrentUser(adminUser);
//        
//        //create ModificationMatches       
//        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
//        ModificationMatch oxidationMatch = new ModificationMatch("methionine oxidation with neutral loss of 64 Da", false, 7);
//        modificationMatches.add(oxidationMatch);
//
//        //create new utilities peptide
//        ArrayList<String> parentProteins = new ArrayList<>();
//        parentProteins.add("Q8IWA5");
//        parentProteins.add("Q13233");
//        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", parentProteins, modificationMatches);
//
//        Peptide targetPeptide = new Peptide();
//
//        //create utilities protein matches
//        List<ProteinMatch> proteinMatches = new ArrayList();
//        ProteinMatch proteinMatch = new ProteinMatch("Q8IWA5");
//        proteinMatches.add(proteinMatch);
//        proteinMatch = new ProteinMatch("Q13233");
//        proteinMatches.add(proteinMatch);
//
//        //create peptide scores
//        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);                
//
//        //create PSPtmScores
//        PSPtmScores ptmScores = new PSPtmScores();
//
//        PtmScoring ptmScoring = new PtmScoring(oxidationMatch.getTheoreticPtm());
//        ArrayList<Integer> locations = new ArrayList();
//        locations.add(oxidationMatch.getModificationSite());
//        double oxidationScore = 100.0;
//        ptmScoring.addAScore(locations, oxidationScore);
//        ptmScoring.addDeltaScore(locations, oxidationScore);
//        ptmScores.addPtmScoring(oxidationMatch.getTheoreticPtm(), ptmScoring);
//
//        utilitiesProteinMapper.mapAnalyticalRuns(proteinMatches, peptideMatchScore, targetPeptide);
//        utilitiesPeptideMapper.mapAnalyticalRuns(sourcePeptide, peptideMatchScore, ptmScores, targetPeptide);
//
//        peptideService.saveOrUpdate(targetPeptide);
    }

}
