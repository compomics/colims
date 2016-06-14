
package com.compomics.colims.distributed.playground;

import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.service.PersistService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.MaxQuantMapper;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantAndromedaParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSearchSettingsParser;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.math.DoubleMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Niels Hulstaert
 */
public class Playground {

    @Autowired
    static MaxQuantMapper maxQuantMapper;

    public static void main(String[] args) throws MappingException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");

        MaxQuantMapper maxQuantMapper = applicationContext.getBean("maxQuantMapper", MaxQuantMapper.class);
        UserBean userBean = applicationContext.getBean("userBean", UserBean.class);
        UserService userService = applicationContext.getBean("userService", UserService.class);
        FastaDbService fastaDbService = applicationContext.getBean("fastaDbService", FastaDbService.class);

        //set admin user in authentication bean
        User adminUser = userService.findByName("admin");
        userBean.setCurrentUser(adminUser);

        String maxquantPath = "C:/Users/demet/projects/colims/colims-distributed/src/test/resources/data/maxquant_1.5.2.8";
        String txtDirectory = maxquantPath + File.separator + MaxQuantConstants.TXT_DIRECTORY.value();
        FastaDb testFastaDb = new FastaDb();
        testFastaDb.setName("test fasta");
        testFastaDb.setFileName("uniprot-taxonomy%3A10090.fasta");
        testFastaDb.setFilePath(txtDirectory +  "uniprot-taxonomy%3A10090.fasta");
        fastaDbService.persist(testFastaDb);

        EnumMap<FastaDbType, Long> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, testFastaDb.getId());

        MaxQuantImport maxQuantImport = new MaxQuantImport(Paths.get(maxquantPath), fastaDbIds);
        MappedData mappedData = maxQuantMapper.mapData(maxQuantImport);
        List<AnalyticalRun> analyticalRuns = mappedData.getAnalyticalRuns();
        System.out.println("Everything is parsed!");
      //  MaxQuantSearchSettingsParser maxQuantSearchSettingsParser = applicationContext.getBean("maxQuantSearchSettingsParser", MaxQuantSearchSettingsParser.class);
     //   MaxQuantParser maxQuantParser = applicationContext.getBean("maxQuantParser", MaxQuantParser.class);
     //   MaxQuantAndromedaParser maxQuantAndromedaParser = applicationContext.getBean("maxQuantAndromedaParser", MaxQuantAndromedaParser.class);


//        PeptideShakerImporter peptideShakerImporter = applicationContext.getBean("peptideShakerImporter", PeptideShakerImporter.class);
//        UserService userService = applicationContext.getBean("userService", UserService.class);
////        SampleService sampleService = applicationContext.getBean("sampleService", SampleService.class);
////        AnalyticalRunService analyticalRunService = applicationContext.getBean("analyticalRunService", AnalyticalRunService.class);
//        UserBean userBean = applicationContext.getBean("userBean", UserBean.class);
////        UtilitiesProteinMapper utilitiesProteinMapper = applicationContext.getBean("utilitiesProteinMapper", UtilitiesProteinMapper.class);
////        UtilitiesPeptideMapper utilitiesPeptideMapper = applicationContext.getBean("utilitiesPeptideMapper", UtilitiesPeptideMapper.class);
////        PeptideService peptideService = applicationContext.getBean("peptideService", PeptideService.class);
//        ExperimentService experimentService = applicationContext.getBean("experimentService", ExperimentService.class);
//        PersistService persistService = applicationContext.getBean("persistService", PersistService.class);
//
////        //load mods from test resources instead of user folder
////        Resource utilitiesMods = new ClassPathResource("data/peptideshaker/searchGUI_mods.xml");
////        PTMFactory.getInstance().clearFactory();
////        PTMFactory.getInstance().importModifications(utilitiesMods.getFile(), false);
//
//        //set admin user in authentication bean
//        User adminUser = userService.findByName("admin");
//        userService.fetchAuthenticationRelations(adminUser);
//        userBean.setCurrentUser(adminUser);
//
//        //create 2 protein groups, with one shared and one not shared protein
//        //so first create 3 proteins
//        Protein protein1 = new Protein("LENNARTLENNART1");
//        Protein protein2 = new Protein("LENNARTLENNART2");
//        Protein protein3 = new Protein("LENNARTLENNART2");
//
//        ProteinGroup proteinGroup1 = new ProteinGroup();
//        ProteinGroup proteinGroup2 = new ProteinGroup();
//
//        //prot group 1
//        ProteinGroupHasProtein proteinGroupHasProtein1 = new ProteinGroupHasProtein();
//
//        //set entity associations
////        proteinGroupHasProtein1.setProteinGroup(proteinGroup1);
////        proteinGroupHasProtein1.setProtein(protein1);
////
////        proteinGroup1.getProteinGroupHasProteins().add(proteinGroupHasProtein1);
//
//        ProteinGroupHasProtein proteinGroupHasProtein2 = new ProteinGroupHasProtein();
//
////        //set entity associations
//        proteinGroupHasProtein2.setProteinGroup(proteinGroup1);
//        proteinGroupHasProtein2.setProtein(protein2);
//
//        proteinGroup1.getProteinGroupHasProteins().add(proteinGroupHasProtein2);
//
//        //prot group 2
//        ProteinGroupHasProtein proteinGroupHasProtein3 = new ProteinGroupHasProtein();
//
//        //set entity associations
//        proteinGroupHasProtein3.setProteinGroup(proteinGroup2);
//        proteinGroupHasProtein3.setProtein(protein2);
//
//        proteinGroup2.getProteinGroupHasProteins().add(proteinGroupHasProtein3);
//
//        ProteinGroupHasProtein proteinGroupHasProtein4 = new ProteinGroupHasProtein();
//
//        //set entity associations
////        proteinGroupHasProtein4.setProteinGroup(proteinGroup2);
////        proteinGroupHasProtein4.setProtein(protein3);
////
////        proteinGroup2.getProteinGroupHasProteins().add(proteinGroupHasProtein4);
//
//        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
//        analyticalRuns.add(new AnalyticalRun());
//
//        Set<ProteinGroup> proteinGroups = new HashSet<>();
//        proteinGroups.add(proteinGroup1);
//        proteinGroups.add(proteinGroup2);
//
//        MappedData mappedData = new MappedData(analyticalRuns, proteinGroups);
//
//        persistService.persist(mappedData, null, null, "dd", new Date());
//
////        //import PeptideShaker .cps file
////        UnpackedPeptideShakerImport unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
////        //set mgf files and fasta file
////        List<File> mgfFiles = new ArrayList<>();
////        mgfFiles.add(new ClassPathResource("data/peptideshaker/input_spectra.mgf").getFile());
////        unpackedPsDataImport.setMgfFiles(mgfFiles);
////        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
////        FastaDb fastaDb = new FastaDb();
////        fastaDb.setName(fastaFile.getName());
////        fastaDb.setFileName(fastaFile.getName());
////        fastaDb.setFilePath(fastaFile.getAbsolutePath());
////        unpackedPsDataImport.setFastaDb(fastaDb);
////
//////        List<AnalyticalRun> analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);
////        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
////
////        //get sample from db
////        Sample sample = sampleService.findAll().get(0);
////
////        //set sample and persist
////        for (AnalyticalRun analyticalRun : analyticalRuns) {
////            //set modification and creation date
////            analyticalRun.setCreationDate(new Date());
////            analyticalRun.setModificationDate(new Date());
////            analyticalRun.setUserName(userBean.getCurrentUser().getName());
////            analyticalRun.setSample(sample);
////            analyticalRunService.saveOrUpdate(analyticalRun);
////        }
////
////        //import PeptideShaker .cps file
////        unpackedPsDataImport = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("small_scale/small_scale.cps").getFile());
////        //set mgf files and fasta file
////        mgfFiles = new ArrayList<>();
////        mgfFiles.add(new ClassPathResource("small_scale/example.mgf").getFile());
////        unpackedPsDataImport.setMgfFiles(mgfFiles);
////        unpackedPsDataImport.setFastaDb(fastaDb);
////
//////        analyticalRuns = peptideShakerImporter.mapAnalyticalRuns(unpackedPsDataImport);
////
////        //get sample from db
////        sample = sampleService.findAll().get(0);
////
////        //set sample and persist
////        for (AnalyticalRun analyticalRun : analyticalRuns) {
////            //set modification and creation date
////            analyticalRun.setCreationDate(new Date());
////            analyticalRun.setModificationDate(new Date());
////            analyticalRun.setUserName(userBean.getCurrentUser().getName());
////            analyticalRun.setSample(sample);
////            analyticalRunService.saveOrUpdate(analyticalRun);
////        }
//
////        //load SequenceFactory for testing
////        File fastaFile = new ClassPathResource("data/peptideshaker/uniprot_sprot_101104_human_concat.fasta").getFile();
////        SequenceFactory.getInstance().loadFastaFile(fastaFile);
////
////        //set admin user in authentication bean
////        User adminUser = userService.findByName("admin1");
////        userService.fetchAuthenticationRelations(adminUser);
////        userBean.setCurrentUser(adminUser);
////
////        //create ModificationMatches
////        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
////        ModificationMatch oxidationMatch = new ModificationMatch("methionine oxidation with neutral loss of 64 Da", false, 7);
////        modificationMatches.add(oxidationMatch);
////
////        //create new utilities peptide
////        ArrayList<String> parentProteins = new ArrayList<>();
////        parentProteins.add("Q8IWA5");
////        parentProteins.add("Q13233");
////        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", parentProteins, modificationMatches);
////
////        Peptide targetPeptide = new Peptide();
////
////        //create utilities protein matches
////        List<ProteinMatch> proteinMatches = new ArrayList();
////        ProteinMatch proteinMatch = new ProteinMatch("Q8IWA5");
////        proteinMatches.add(proteinMatch);
////        proteinMatch = new ProteinMatch("Q13233");
////        proteinMatches.add(proteinMatch);
////
////        //create peptide scores
////        MatchScore peptideMatchScore = new MatchScore(0.5, 0.1);
////
////        //create PSPtmScores
////        PSPtmScores ptmScores = new PSPtmScores();
////
////        PtmScoring ptmScoring = new PtmScoring(oxidationMatch.getTheoreticPtm());
////        ArrayList<Integer> locations = new ArrayList();
////        locations.add(oxidationMatch.getModificationSite());
////        double oxidationScore = 100.0;
////        ptmScoring.addAScore(locations, oxidationScore);
////        ptmScoring.addDeltaScore(locations, oxidationScore);
////        ptmScores.addPtmScoring(oxidationMatch.getTheoreticPtm(), ptmScoring);
////
////        utilitiesProteinMapper.mapAnalyticalRuns(proteinMatches, peptideMatchScore, targetPeptide);
////        utilitiesPeptideMapper.mapAnalyticalRuns(sourcePeptide, peptideMatchScore, ptmScores, targetPeptide);
////
////        peptideService.saveOrUpdate(targetPeptide);

    }

}
