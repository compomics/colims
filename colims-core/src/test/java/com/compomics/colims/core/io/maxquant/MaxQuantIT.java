package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantSpectrumParser;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Test
    public void runStorage() throws IOException, UnparseableException, MappingException, SQLException, ClassNotFoundException {
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

        for (AnalyticalRun aRun : maxQuantParser.getRuns()) {
            List<Spectrum> mappedSpectra = new ArrayList<>(aRun.getSpectrums().size());

            for (Spectrum aSpectrum : aRun.getSpectrums()) {
                mappedSpectra.add(aSpectrum);
                //only get best hit
                Peptide identification = maxQuantParser.getIdentificationForSpectrum(aSpectrum);

                aSpectrum.setPeptides(new ArrayList<Peptide>() {
                    {
                        this.add(identification);
                    }
                });
            }
            aRun.setSpectrums(mappedSpectra);
            colimsRuns.add(aRun);
        }

        maxQuantSample.setAnalyticalRuns(colimsRuns);
        experimentService.save(experiment);

        Experiment savedExperiment = experimentService.findById(2L);
        List<Spectrum> spectrums = savedExperiment.getSamples().get(0).getAnalyticalRuns().get(0).getSpectrums();

        assertThat(spectrums.size(), is(colimsRuns.get(0).getSpectrums().size()));
        assertThat(savedExperiment.getSamples().get(0).getName(), is("BREADBREADBREADBREAD"));
        // TODO: more assertions
    }

    @Test
    public void testSpectrumInsertion() throws IOException, MappingException, UnparseableException {
        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        int startSpectraCount = spectrumService.findAll().size();

        Map<Integer, Spectrum> spectrumMap = maxQuantSpectrumParser.parse(MaxQuantTestSuite.msmsFile);
        List<Spectrum> spectrumHolder = spectrumMap.values().stream().collect(Collectors.toList());

        spectrumHolder.forEach(spectrumService::save);
        
        List<Spectrum> storedSpectra = spectrumService.findAll();
        int endAmountOfSpectra = storedSpectra.size();

        assertThat(endAmountOfSpectra, is(both(not(startSpectraCount)).and(is(startSpectraCount + spectrumMap.size()))));
        assertThat(storedSpectra.get(startSpectraCount + 138).getTitle(), is(spectrumHolder.get(138).getTitle()));
    }
}
