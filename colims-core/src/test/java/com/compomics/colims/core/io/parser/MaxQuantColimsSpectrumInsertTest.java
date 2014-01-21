package com.compomics.colims.core.io.parser;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.parser.impl.MaxQuantSpectrumParser;
import com.compomics.colims.core.io.parser.impl.UnparseableException;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesSpectrumMapper;
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
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Davy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class MaxQuantColimsSpectrumInsertTest {

    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private MaxQuantSpectrumParser maxQuantMsMsParser;
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
        spectrumMap = maxQuantMsMsParser.parse(maxQuantMsMsFile, true);
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


        //spike in spectrumtested in the spectrummapper test and see if it still is intact and retrievable
        HashMap<Double, Peak> peaks = new HashMap<>();
        peaks.put(123.3, new Peak(123.3, 100.0, 22.5));
        peaks.put(356.8, new Peak(356.8, 100.0, 22.5));
        peaks.put(452.1, new Peak(452.1, 100.0, 22.5));
        peaks.put(451.3, new Peak(451.3, 100.0, 22.5));
        peaks.put(874.3, new Peak(874.3, 100.0, 22.5));
        peaks.put(995.2, new Peak(995.2, 100.0, 22.5));
        peaks.put(789.0, new Peak(789.0, 100.0, 22.5));
        peaks.put(1125.5, new Peak(1125.5, 100.0, 22.5));
        peaks.put(474.3, new Peak(474.3, 100.0, 22.5));
        peaks.put(142.3, new Peak(142.3, 100.0, 22.5));

        ArrayList<Charge> possibleCharges = new ArrayList<>();
        possibleCharges.add(new Charge(Charge.PLUS, 2));
        Precursor precursor = new Precursor(25.3, 875.2, possibleCharges);
        expectedResult = new MSnSpectrum(2, precursor, "spectrum title", peaks, "spectrum file name");
        expectedResult.setScanNumber("1200");
        expectedResult.setScanStartTime(300.5);
        Spectrum testSpectrum = new Spectrum();
        utilitiesSpectrumMapper.map(expectedResult, FragmentationType.CID, testSpectrum);
        mappedSpectra.add(testSpectrum);
        Spectrum result = mappedSpectra.get(999);

        //direct rip from the UtilitiesSpectrumMapperTest, needs to be redone to remove redundant code

        Assert.assertEquals(expectedResult.getSpectrumTitle(), result.getTitle());
        Assert.assertEquals(expectedResult.getPrecursor().getMz(), result.getMzRatio(), 0.001);
        Assert.assertEquals(expectedResult.getPrecursor().getIntensity(), result.getIntensity(), 0.001);
        Assert.assertEquals(expectedResult.getPrecursor().getRt(), result.getRetentionTime(), 0.001);
        Assert.assertEquals(expectedResult.getScanNumber(), result.getScanNumber());
        Assert.assertEquals(expectedResult.getScanStartTime(), result.getScanTime(), 0.001);
        Assert.assertEquals(2, result.getCharge().intValue());
        Assert.assertEquals(FragmentationType.CID, result.getFragmentationType());

        Assert.assertNotNull(result.getSpectrumFiles());
        Assert.assertEquals(1, result.getSpectrumFiles().size());
        Assert.assertNotNull(result.getSpectrumFiles().get(0));
        Assert.assertNotNull(result.getSpectrumFiles().get(0).getContent());
        Assert.assertNotNull(result.getSpectrumFiles().get(0).getSpectrum());

        //check if the spectrum peaks were mapped correctly
        Map<Double, Double> spectrumPeaks = spectrumService.getSpectrumPeaks(result.getSpectrumFiles().get(0));

        //compare the spectrum peaks
        for (Double mzRatio : peaks.keySet()) {
            Assert.assertTrue(spectrumPeaks.containsKey(mzRatio));
            Assert.assertEquals(peaks.get(mzRatio).intensity, spectrumPeaks.get(mzRatio), 0.001);
        }
    }

    @Test
    public void testSpectrumInsertion() throws IOException, MappingException, HeaderEnumNotInitialisedException, UnparseableException {
        User user = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(user);
        authenticationBean.setCurrentUser(user);
        int startSpectraCount = spectrumService.findAll().size();
        System.out.println("start amount of spectra = " + startSpectraCount);
        spectrumMap = maxQuantMsMsParser.parse(maxQuantMsMsFile, true);
        List<Spectrum> spectrumHolder = new ArrayList<>();
        for (MSnSpectrum spectrum : spectrumMap.values()) {
            Spectrum colimsSpectrum = new Spectrum();
            utilitiesSpectrumMapper.map(spectrum, FragmentationType.CID, colimsSpectrum);
            spectrumHolder.add(colimsSpectrum);
        }
        for (Spectrum spectrum : spectrumHolder){
            spectrumService.save(spectrum);
        }
        List<Spectrum> storedSpectra = spectrumService.findAll();
        int endAmountOfSpectra = storedSpectra.size();
        System.out.println("end amount of spectra is " + endAmountOfSpectra);
        assertThat(endAmountOfSpectra, is(both(not(startSpectraCount)).and(is(999))));
        assertThat(storedSpectra.get(138).getTitle(),is (spectrumHolder.get(138).getTitle()));
    }
}
