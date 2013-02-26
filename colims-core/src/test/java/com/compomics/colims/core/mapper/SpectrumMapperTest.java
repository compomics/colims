
package com.compomics.colims.core.mapper;

import com.compomics.colims.core.io.mapper.SpectrumMapper;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.IOManager;
import com.compomics.colims.core.io.model.MascotGenericFile;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class SpectrumMapperTest {
    
    @Autowired
    private SpectrumMapper spectrumMapper;
    @Autowired
    private IOManager iOManager;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Test
    public void testMapSpectrum() throws MappingException, IOException{
        //create new MSnSpectrum
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
        
        MSnSpectrum mSnSpectrum = new MSnSpectrum(2, precursor, "spectrum title", peaks, "spectrum file name");        
        Spectrum spectrum = new Spectrum();
        
        spectrumMapper.map(mSnSpectrum, spectrum);
                
        Assert.assertEquals(mSnSpectrum.getSpectrumTitle(), spectrum.getTitle());        
        Assert.assertEquals(mSnSpectrum.getPrecursor().getMz(), spectrum.getMzRatio(), 0.001);
        Assert.assertEquals(mSnSpectrum.getPrecursor().getIntensity(), spectrum.getIntensity(), 0.001);
        Assert.assertEquals(mSnSpectrum.getPrecursor().getRt(), spectrum.getRetentionTime(), 0.001);
        Assert.assertEquals(mSnSpectrum.getPrecursor().getPossibleCharges().get(0).value, spectrum.getCharge().intValue());
        
        Assert.assertNotNull(spectrum.getSpectrumFiles());
        Assert.assertEquals(1, spectrum.getSpectrumFiles().size());
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0));
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0).getContent());
        Assert.assertNotNull(spectrum.getSpectrumFiles().get(0).getSpectrum());
        
        //check if the SpectrumFile byte array contains the correct peaks
        File spectrumFile = temporaryFolder.newFile("spectrumFile");
                        
        iOManager.unzipAndWriteBytesToFile(spectrumFile, spectrum.getSpectrumFiles().get(0).getContent());
        MascotGenericFile mascotGenericFile = new MascotGenericFile(spectrumFile);
        HashMap<Double, Double> mappedPeaks = mascotGenericFile.getPeaks(); 
        Assert.assertEquals(peaks.size(), mappedPeaks.size());
        
        //compare the spectrum peaks
        for(Double mzRatio : peaks.keySet()){
            Assert.assertTrue(mappedPeaks.containsKey(mzRatio));
            Assert.assertEquals(peaks.get(mzRatio).intensity, mappedPeaks.get(mzRatio), 0.01);
        }
    }

}
