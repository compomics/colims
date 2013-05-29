package com.compomics.colims.core.mapper;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.exception.PeptideShakerIOException;
import com.compomics.colims.core.io.PeptideShakerIO;
import com.compomics.colims.core.io.mapper.Mapper;
import com.compomics.colims.core.io.mapper.UtilitiesExperimentMapper;
import com.compomics.colims.core.io.model.PeptideShakerImport;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UtilitiesExperimentMapperTest {

    @Autowired
    private Mapper utilitiesExperimentMapper;
    @Autowired
    private PeptideShakerIO peptideShakerIO;
    @Autowired
    private ProjectService projectService;

    @Test
    public void testExperimentMapper() throws IOException, PeptideShakerIOException, MappingException {
//        //import PeptideShaker .cps file
//        PeptideShakerImport peptideShakerImport = peptideShakerIO.importPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project_3.cps").getFile());
//        //set mgf files and fasta file
//        List<File> mgfFiles = new ArrayList<>();
//        mgfFiles.add(new ClassPathResource("input_spectra.mgf").getFile());
//        peptideShakerImport.setMgfFiles(mgfFiles);
//        peptideShakerImport.setFastaFile(new ClassPathResource("uniprot_sprot_101104_human_concat.fasta").getFile());
//
//        Experiment experiment = new Experiment();
//
//        utilitiesExperimentMapper.map(peptideShakerImport, experiment);
//
//        //experiment
//        Assert.assertNotNull(experiment.getSamples());
//        Assert.assertEquals(1, experiment.getSamples().size());
//        Assert.assertEquals(peptideShakerImport.getMsExperiment().getReference(), experiment.getTitle());
//
//        //sample
//        Sample sample = experiment.getSamples().get(0);
//        Assert.assertNotNull(sample);
//        Assert.assertNotNull(sample.getExperiment());
//        Assert.assertEquals(1, sample.getAnalyticalRuns().size());
//
//        //analytical run
//        AnalyticalRun analyticalRun = sample.getAnalyticalRuns().get(0);
//        Assert.assertNotNull(analyticalRun);
//        Assert.assertNotNull(analyticalRun.getSample());
//        Assert.assertNotNull(analyticalRun.getSpectrums());
//        Assert.assertEquals(4521, analyticalRun.getSpectrums().size());
//
//        //spectra
//        for (Spectrum spectrum : analyticalRun.getSpectrums()) {
//            Assert.assertNotNull(spectrum.getAnalyticalRun());
//            if (!spectrum.getPeptides().isEmpty()) {
//                for (Peptide peptide : spectrum.getPeptides()) {
//                    Assert.assertNotNull(peptide.getSpectrum());
//                    Assert.assertFalse(peptide.getSequence().isEmpty());
//                    if (!peptide.getPeptideHasProteins().isEmpty()) {
//                        for (PeptideHasProtein peptideHasProtein : peptide.getPeptideHasProteins()) {
//                            Assert.assertNotNull(peptideHasProtein.getPeptide());
//                            Protein protein = peptideHasProtein.getProtein();
//                            Assert.assertNotNull(protein);
//                            Assert.assertFalse(protein.getAccession().isEmpty());
//                            Assert.assertFalse(protein.getSequence().isEmpty());
//                            Assert.assertNotNull(protein.getDatabaseType());
//                        }
//                    }
//                    if (!peptide.getPeptideHasModifications().isEmpty()) {
//                        for (PeptideHasModification peptideHasModification : peptide.getPeptideHasModifications()) {
//                            Assert.assertNotNull(peptideHasModification.getPeptide());
//                            Modification modification = peptideHasModification.getModification();
//                            Assert.assertNotNull(modification);
//                            //Assert.assertNotNull(modification.getPeptideHasModifications());
//                            Assert.assertFalse(modification.getName().isEmpty());
//                        }
//                    }
//                }
//            }
//        }
//
//        //persist project
//        Project project = new Project();
//        project.setDescription("test description");
//        project.setTitle("project title");
//        project.setLabel("pro001");
//        List<Experiment> experiments = new ArrayList<>();
//        experiments.add(experiment);
//        experiment.setProject(project);
//
//        projectService.save(project);
//
//        List<Project> projects = projectService.findAll();
//        System.out.println("projects size " + projects.size());
    }
}
