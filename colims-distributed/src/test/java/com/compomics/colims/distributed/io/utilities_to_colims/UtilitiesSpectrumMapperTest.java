package com.compomics.colims.distributed.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class UtilitiesSpectrumMapperTest {

    private MSnSpectrum mSnSpectrum;
    private HashMap<Double, Peak> peaks;
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private SpectrumService spectrumService;

    @Before
    public void init() {
        //create new MSnSpectrum
        peaks = new HashMap<>();
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
        mSnSpectrum = new MSnSpectrum(2, precursor, "spectrum title scan=8196\"", peaks, "spectrum file name");
        mSnSpectrum.setScanNumber("");
        mSnSpectrum.setScanStartTime(300.5);
    }

    /**
     * Test the mapping of a utilities spectrum to a colims spectrum.
     *
     * @throws MappingException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException thrown in case of a failure to load a
     * class by it's string name.
     * @throws MzMLUnmarshallerException
     */
    @Test
    public void testMapSpectrum() throws MappingException, IOException, ClassNotFoundException, MzMLUnmarshallerException {
        Spectrum spectrum = new Spectrum();

        utilitiesSpectrumMapper.map(mSnSpectrum, FragmentationType.CID, spectrum);

        Assert.assertEquals(mSnSpectrum.getSpectrumTitle(), spectrum.getTitle());
        Assert.assertEquals(mSnSpectrum.getPrecursor().getMz(), spectrum.getMzRatio(), 0.001);
        Assert.assertEquals(mSnSpectrum.getPrecursor().getIntensity(), spectrum.getIntensity(), 0.001);
        Assert.assertEquals(mSnSpectrum.getPrecursor().getRt(), spectrum.getRetentionTime(), 0.001);
        Assert.assertEquals("8196", Long.toString(spectrum.getScanNumber()));
        Assert.assertEquals(mSnSpectrum.getScanStartTime(), spectrum.getScanTime(), 0.001);
        Assert.assertEquals(2, spectrum.getCharge().intValue());
        Assert.assertEquals(FragmentationType.CID, spectrum.getFragmentationType());

        Assert.assertNotNull(spectrum.getSpectrumFiles());
        Assert.assertEquals(1, spectrum.getSpectrumFiles().size());
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0));
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0).getContent());
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0).getSpectrum());

        //check if the spectrum peaks were mapped correctly
        Map<Double, Double> spectrumPeaks = spectrumService.getSpectrumPeaks(spectrum.getSpectrumFiles().get(0));

        //compare the spectrum peaks
        peaks.keySet().stream().forEach(mzRatio -> {
            Assert.assertTrue(spectrumPeaks.containsKey(mzRatio));
            Assert.assertEquals(peaks.get(mzRatio).intensity, spectrumPeaks.get(mzRatio), 0.001);
        });
    }
}
