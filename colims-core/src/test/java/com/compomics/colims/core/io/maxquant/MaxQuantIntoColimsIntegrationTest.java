package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * the actual max quant integration test, to be renamed when the parser gets
 * extracted
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantIntoColimsIntegrationTest {

    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private ProjectService projectService;
    // unused autowires, needed for asserts that will be added later
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private PeptideService peptideService;
    @Autowired
    private ProteinService proteinService;
    @Autowired
    MaxQuantUtilitiesAnalyticalRunMapper maxQuantUtilitiesAnalyticalRunMapper;
    @Autowired
    UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    MaxQuantUtilitiesPeptideMapper MaxQuantUtilitiesPeptideMapper;
    @Autowired
    UtilitiesProteinMapper maxQuantProteinMapperStub;
    private File testFolder;

    public MaxQuantIntoColimsIntegrationTest() throws IOException {
        this.testFolder = new ClassPathResource("data/maxquant").getFile();
    }

    @Test
    public void runStorage() throws IOException, HeaderEnumNotInitialisedException, UnparseableException, MappingException {
        System.out.println("Max Quant storage integration test");
        maxQuantParser.parseFolder(testFolder);
        User user = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        Project project = projectService.findById(1L);
        //Project project = new Project();
        //project.setDescription("experiment where we try to store max quant data");
        //project.setTitle("max quant experiment");
        //project.setLabel("MaxQuant insertion");
        // projectService.save(project);
        final Experiment experiment = new Experiment();
        experiment.setProject(project);
        project.setExperiments(new ArrayList<Experiment>() {
            {
                this.add(experiment);
            }
        });
        experiment.setTitle("MaxQuant insertion experiment");

        final Sample maxQuantSample = new Sample();
        maxQuantSample.setExperiment(experiment);
        experiment.setSamples(new ArrayList<Sample>() {
            {
                this.add(maxQuantSample);
            }
        });
        //TODO change this with the normal mapper
        List<AnalyticalRun> colimsRuns = new ArrayList<>(maxQuantParser.getRuns().size());
        for (MaxQuantAnalyticalRun aRun : maxQuantParser.getRuns()) {
            AnalyticalRun targetRun = new AnalyticalRun();
            maxQuantUtilitiesAnalyticalRunMapper.map(aRun, targetRun);
            List<Spectrum> mappedSpectra = new ArrayList<>(aRun.getListOfSpectra().size());
            for (MSnSpectrum aSpectrum : aRun.getListOfSpectra().values()) {
                Spectrum targetSpectrum = new Spectrum();
                utilitiesSpectrumMapper.map(aSpectrum, FragmentationType.CID, targetSpectrum);
                mappedSpectra.add(targetSpectrum);
                //only get best hit
                PeptideAssumption identification = maxQuantParser.getIdentificationForSpectrum(aSpectrum);
                final Peptide targetPeptide = new Peptide();
                MaxQuantUtilitiesPeptideMapper.map(identification, targetPeptide);
                targetSpectrum.setPeptides(new ArrayList<Peptide>() {
                    {
                        this.add(targetPeptide);
                    }
                });
                maxQuantProteinMapperStub.map(new ArrayList(maxQuantParser.getProteinHitsForIdentification(identification)), (MatchScore) identification.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY)), targetPeptide);
            }
            targetRun.setSpectrums(mappedSpectra);
            colimsRuns.add(targetRun);
        }
        maxQuantSample.setAnalyticalRuns(colimsRuns);
        //experimentService.save(experiment);
    }
}
