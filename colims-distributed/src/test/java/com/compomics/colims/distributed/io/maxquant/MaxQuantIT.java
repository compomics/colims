package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantSpectrumParser;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AuthenticationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * the actual max quant integration test, to be renamed when the parser gets extracted.
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantIT {

    private static final String maxQuantVersion = "1.5.2.8";
    private static final String fastaFilePath = "data/maxquant_" + maxQuantVersion + "/uniprot-taxonomy%3A10090.fasta";
    private static final String maxQuantTextFolderPath = "data/maxquant_" + maxQuantVersion;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private InstrumentService instrumentService;

    @Test
    public void runStorage() throws IOException, UnparseableException, MappingException, SQLException, ClassNotFoundException {
        FastaDb maxQuantTestFastaDb = new FastaDb();
        ClassPathResource fastaResource = new ClassPathResource(fastaFilePath);
        maxQuantTestFastaDb.setName(fastaResource.getFilename());
        maxQuantTestFastaDb.setFileName(fastaResource.getFilename());
        maxQuantTestFastaDb.setFilePath(fastaResource.getFile().getPath());

        maxQuantParser.parseFolder(new ClassPathResource(maxQuantTextFolderPath).getFile(), maxQuantTestFastaDb);

        User user = userService.findByName("admin");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);

        List<AnalyticalRun> colimsRuns = new ArrayList<>(maxQuantParser.getRuns().size());
        for (AnalyticalRun aRun : maxQuantParser.getRuns()) {
            List<Spectrum> mappedSpectra = new ArrayList<>(aRun.getSpectrums().size());

            for (Spectrum aSpectrum : aRun.getSpectrums()) {
                mappedSpectra.add(aSpectrum);
                //only get best hit
                Peptide identification = maxQuantParser.getIdentificationForSpectrum(aSpectrum);

                aSpectrum.getPeptides().add(identification);
            }
            aRun.setSpectrums(mappedSpectra);

            colimsRuns.add(aRun);
        }

        //get sample from db
        Sample sample = sampleService.findAll().get(0);

        for (AnalyticalRun analyticalRun : colimsRuns) {
            Date auditDate = new Date();

            analyticalRun.setCreationDate(auditDate);
            analyticalRun.setModificationDate(auditDate);
            analyticalRun.setUserName("testing");
            analyticalRun.setStartDate(auditDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrumentService.findAll().get(0));

            analyticalRunService.saveOrUpdate(analyticalRun);
        }
        // TODO: more assertions
    }

}
