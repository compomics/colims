package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantSpectrumParser;
import com.compomics.colims.core.io.maxquant.utilities_mappers.MaxQuantUtilitiesAnalyticalRunMapper;
import com.compomics.colims.core.io.maxquant.utilities_mappers.MaxQuantUtilitiesPeptideMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProjectService;
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
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * the actual max quant integration test, to be renamed when the parser gets
 * extracted
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantIT {

    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    MaxQuantUtilitiesAnalyticalRunMapper maxQuantUtilitiesAnalyticalRunMapper;
    @Autowired
    UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    MaxQuantUtilitiesPeptideMapper maxQuantUtilitiesPeptideMapper;
    @Autowired
    UtilitiesProteinMapper maxQuantProteinMapperStub;

    @Ignore
    @Test
    public void runStorage() throws IOException, UnparseableException, MappingException, SQLException, ClassNotFoundException {
        // TODO: ignored due to utilities
        SequenceFactory sequenceFactory = SequenceFactory.getInstance();
        sequenceFactory.clearFactory();
        sequenceFactory.loadFastaFile(MaxQuantTestSuite.fastaFile, null);

        maxQuantParser.parseFolder(MaxQuantTestSuite.maxQuantTextFolder);

        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        Project project = projectService.findById(1L);

        final Experiment experiment = new Experiment();
        experiment.setProject(project);
        project.setExperiments(new ArrayList<Experiment>() {
            {
                this.add(experiment);
            }
        });
        experiment.setTitle("MaxQuant insertion experiment");

        final Sample maxQuantSample = new Sample("BREADBREADBREADBREAD");
        maxQuantSample.setExperiment(experiment);
        experiment.setSamples(new ArrayList<Sample>() {
            {
                this.add(maxQuantSample);
            }
        });

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
                maxQuantUtilitiesPeptideMapper.map(identification, targetPeptide);
                targetSpectrum.setPeptides(new ArrayList<Peptide>() {
                    {
                        this.add(targetPeptide);
                    }
                });
//                maxQuantProteinMapperStub.map(new ArrayList(maxQuantParser.getProteinHitsForIdentification(identification)), (MatchScore) identification.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY)), targetPeptide);
            }
            targetRun.setSpectrums(mappedSpectra);
            colimsRuns.add(targetRun);
        }

        maxQuantSample.setAnalyticalRuns(colimsRuns);
        experimentService.save(experiment);

        Experiment savedExperiment = experimentService.findById(2L);
        List<Spectrum> spectrums = savedExperiment.getSamples().get(0).getAnalyticalRuns().get(0).getSpectrums();

        assertThat(spectrums.size(), is(colimsRuns.get(0).getSpectrums().size()));
        assertThat(savedExperiment.getSamples().get(0).getName(), is("BREADBREADBREADBREAD"));
        // TODO: more assertions
    }

    @Ignore
    @Test
    public void testSpectrumInsertion() throws IOException, MappingException, UnparseableException {
        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        int startSpectraCount = spectrumService.findAll().size();

        Map<Integer, MSnSpectrum> spectrumMap = maxQuantSpectrumParser.parse(MaxQuantTestSuite.msmsFile, true);
        List<Spectrum> spectrumHolder = new ArrayList<>();
        
        for (MSnSpectrum spectrum : spectrumMap.values()) {
            Spectrum colimsSpectrum = new Spectrum();
            utilitiesSpectrumMapper.map(spectrum, FragmentationType.CID, colimsSpectrum);
            spectrumHolder.add(colimsSpectrum);
        }
        
        for (Spectrum spectrum : spectrumHolder) {
            spectrumService.save(spectrum);
        }
        
        List<Spectrum> storedSpectra = spectrumService.findAll();
        int endAmountOfSpectra = storedSpectra.size();

        assertThat(endAmountOfSpectra, is(both(not(startSpectraCount)).and(is(startSpectraCount + spectrumMap.size()))));
        assertThat(storedSpectra.get(startSpectraCount + 138).getTitle(), is(spectrumHolder.get(138).getTitle()));
    }
}
