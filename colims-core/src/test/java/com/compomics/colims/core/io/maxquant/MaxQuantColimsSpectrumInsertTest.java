package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantColimsSpectrumInsertTest {

    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private MaxQuantSpectrumParser maxQuantSpectrumParser;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private UserService userService;
    private File maxQuantMsMsFile;
    private Map<Integer, MSnSpectrum> spectrumMap = new HashMap<>();

    public MaxQuantColimsSpectrumInsertTest() throws IOException {
        maxQuantMsMsFile = new ClassPathResource("data/maxquant/msms_subset_1000.tsv").getFile();
    }

    @Test
    public void testSpectrumMapping() throws IOException, MappingException, HeaderEnumNotInitialisedException, UnparseableException {
        List<Spectrum> mappedSpectra = new ArrayList<>();
        spectrumMap = maxQuantSpectrumParser.parse(maxQuantMsMsFile, true);
        int spectrumArrayindex = -1;

        for (Integer msmsKey : spectrumMap.keySet()) {
            MSnSpectrum spectrum = spectrumMap.get(msmsKey);
            Spectrum colimsSpectrum = new Spectrum();
            utilitiesSpectrumMapper.map(spectrum, FragmentationType.CID, colimsSpectrum);
            //convoluted way to get the expected result and be able to test against static values
            mappedSpectra.add(colimsSpectrum);

            if (msmsKey == 899) {
                spectrumArrayindex = mappedSpectra.size() - 1;
            }
        }

        assertThat(mappedSpectra.size(), is(999));

        Spectrum toTest = mappedSpectra.get(spectrumArrayindex);
        MSnSpectrum expectedResult = spectrumMap.get(899);
        assertThat(Integer.parseInt(toTest.getScanNumber()), both(is(Integer.parseInt(expectedResult.getScanNumber()))).and(is(58732)));
        assertThat(toTest.getTitle(), both(is(expectedResult.getSpectrumTitle())).and(is("QE2_120326_OPL2023_stoolpools_MdW_LB_stoolpool1_02-58732")));
        assertThat(toTest.getRetentionTime(), both(is(expectedResult.getPrecursor().getRt())).and(is(145.76)));
        assertThat(toTest.getCharge(), both(is(expectedResult.getPrecursor().getPossibleCharges().get(0).value)).and(is(2)));
        assertThat(toTest.getMzRatio(), both(is(expectedResult.getPrecursor().getMz())).and(is(993.51256)));
        assertThat(toTest.getIntensity(), both(is(expectedResult.getPrecursor().getIntensity())).and(is(0.0)));
    }

    @Test
    public void testSpectrumInsertion() throws IOException, MappingException, HeaderEnumNotInitialisedException, UnparseableException {
        User user = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        int startSpectraCount = spectrumService.findAll().size();
        System.out.println("start amount of spectra = " + startSpectraCount);
        spectrumMap = maxQuantSpectrumParser.parse(maxQuantMsMsFile, true);
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
        System.out.println("end amount of spectra is " + endAmountOfSpectra);
        assertThat(endAmountOfSpectra, is(both(not(startSpectraCount)).and(is(startSpectraCount + spectrumMap.size()))));
        assertThat(storedSpectra.get(startSpectraCount + 138).getTitle(), is(spectrumHolder.get(138).getTitle()));
    }
}
