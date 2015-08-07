package com.compomics.colims.core.playground;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImporter;
import com.compomics.colims.core.io.peptideshaker.UnpackedPeptideShakerImport;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.core.service.*;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.biology.PTMFactory;
import org.apache.commons.compress.archivers.ArchiveException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
public class Playground2 {

    public static void main(String[] args) throws XmlPullParserException, IOException, MappingException, SQLException, ClassNotFoundException, InterruptedException, IllegalArgumentException, MzMLUnmarshallerException, ArchiveException {
        ApplicationContextProvider.getInstance().setDefaultApplicationContext();
        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();

//        PeptideShakerIO peptideShakerIO = applicationContext.getBean("peptideShakerIO", PeptideShakerIO.class);
//        PeptideShakerImporter peptideShakerImporter = applicationContext.getBean("peptideShakerImporter", PeptideShakerImporter.class);
        UserService userService = applicationContext.getBean("userService", UserService.class);
//        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
//        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
        AuthenticationBean authenticationBean = applicationContext.getBean("authenticationBean", AuthenticationBean.class);
//        UtilitiesProteinMapper utilitiesProteinMapper = applicationContext.getBean("utilitiesProteinMapper", UtilitiesProteinMapper.class);
//        UtilitiesPeptideMapper utilitiesPeptideMapper = applicationContext.getBean("utilitiesPeptideMapper", UtilitiesPeptideMapper.class);
//        PeptideService peptideService = applicationContext.getBean("peptideService", PeptideService.class);
        ExperimentService experimentService = applicationContext.getBean("experimentService", ExperimentService.class);

//        //load mods from test resources instead of user folder
//        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
//        PTMFactory.getInstance().clearFactory();
//        PTMFactory.getInstance().importModifications(utilitiesMods.getFile(), false);

        Experiment bla = experimentService.findByProjectIdAndTitle(1L, "experiment 1");

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(adminUser);
        authenticationBean.setCurrentUser(adminUser);



//        //import PeptideShaker .cps file
//        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
//        //set mgf files and fasta file
//        List<File> mgfFiles = new ArrayList<>();
//        mgfFiles.add(new ClassPathResource("data/peptideshaker/input_spectra.mgf").getFile());
//        unpackedPsDataImport.setMgfFiles(mgfFiles);
//        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
//        FastaDb fastaDb = new FastaDb();
//        fastaDb.setName(fastaFile.getName());
//        fastaDb.setFileName(fastaFile.getName());
//        fastaDb.setFilePath(fastaFile.getAbsolutePath());
//        unpackedPsDataImport.setFastaDb(fastaDb);
//
////        List<AnalyticalRun> analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);
//        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
//
//        //get sample from db
//        Sample sample = sampleService.findAll().get(0);
//
//        //set sample and persist
//        for (AnalyticalRun analyticalRun : analyticalRuns) {
//            //set modification and creation date
//            analyticalRun.setCreationDate(new Date());
//            analyticalRun.setModificationDate(new Date());
//            analyticalRun.setUserName(authenticationBean.getCurrentUser().getName());
//            analyticalRun.setSample(sample);
//            analyticalRunService.saveOrUpdate(analyticalRun);
//        }
//
//        //import PeptideShaker .cps file
//        unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
//        //set mgf files and fasta file
//        mgfFiles = new ArrayList<>();
//        mgfFiles.add(new ClassPathResource("small_scale/example.mgf").getFile());
//        unpackedPsDataImport.setMgfFiles(mgfFiles);
//        unpackedPsDataImport.setFastaDb(fastaDb);
//
////        analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);
//
//        //get sample from db
//        sample = sampleService.findAll().get(0);
//
//        //set sample and persist
//        for (AnalyticalRun analyticalRun : analyticalRuns) {
//            //set modification and creation date
//            analyticalRun.setCreationDate(new Date());
//            analyticalRun.setModificationDate(new Date());
//            analyticalRun.setUserName(authenticationBean.getCurrentUser().getName());
//            analyticalRun.setSample(sample);
//            analyticalRunService.saveOrUpdate(analyticalRun);
//        }

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
