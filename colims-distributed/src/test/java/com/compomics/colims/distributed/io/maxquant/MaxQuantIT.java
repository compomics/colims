package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSpectrumParser;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AuthenticationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * the actual max quant integration test, to be renamed when the parser gets extracted
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
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
        FastaDb maxQuantTestFastaDb = new FastaDb();
        maxQuantTestFastaDb.setName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFileName(MaxQuantTestSuite.fastaFile.getName());
        maxQuantTestFastaDb.setFilePath(MaxQuantTestSuite.fastaFile.getAbsolutePath());

        maxQuantParser.parseFolder(MaxQuantTestSuite.maxQuantTextFolder, maxQuantTestFastaDb);

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

        Map<Spectrum, Integer> spectrumMap = maxQuantSpectrumParser.parse(MaxQuantTestSuite.msmsFile);
        List<Spectrum> spectrumHolder = spectrumMap.keySet().stream().collect(Collectors.toList());

        spectrumHolder.forEach(spectrumService::save);

        List<Spectrum> storedSpectra = spectrumService.findAll();
        int endAmountOfSpectra = storedSpectra.size();

        assertThat(endAmountOfSpectra, is(both(not(startSpectraCount)).and(is(startSpectraCount + spectrumMap.size()))));
        assertThat(storedSpectra.get(startSpectraCount + 138).getTitle(), is(spectrumHolder.get(138).getTitle()));
    }
}
