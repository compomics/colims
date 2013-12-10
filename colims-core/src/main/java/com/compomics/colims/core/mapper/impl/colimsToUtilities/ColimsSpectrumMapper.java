package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("colimsSpectrumMapper")
public class ColimsSpectrumMapper {

    private static final Logger LOGGER = Logger.getLogger(ColimsSpectrumMapper.class);

    @Autowired
    private SpectrumService spectrumService;

    /**
     * Map the utilities spectrum onto the colims spectrum.
     *
     * @param sourceSpectrum the colims spectrum
     * @param targetSpectrum the utilities MSnSpectrum
     * @throws MappingException
     */
    public void map(Spectrum sourceSpectrum, MSnSpectrum targetSpectrum) throws MappingException {
        LOGGER.debug("Start mapping MSnSpectrum with title" + sourceSpectrum.getTitle());

        if (sourceSpectrum == null || targetSpectrum == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        //Build the precursor
        double retentionTime = sourceSpectrum.getRetentionTime();
        double moverz = sourceSpectrum.getMzRatio();
        ArrayList<Charge> chargeList = new ArrayList<Charge>();
        chargeList.add(new Charge(1, sourceSpectrum.getCharge()));
        Precursor precursor = new Precursor(retentionTime, moverz, sourceSpectrum.getIntensity(), chargeList);
        targetSpectrum.setPrecursor(precursor);
        //Add other parameters
        targetSpectrum.setScanNumber(sourceSpectrum.getScanNumber());
        targetSpectrum.setScanStartTime(sourceSpectrum.getScanTime());
        targetSpectrum.setSpectrumTitle(sourceSpectrum.getTitle());
        //Add peaks
        for (SpectrumFile aFile : sourceSpectrum.getSpectrumFiles()) {
            try {
                Map<Double, Double> mzAndIntensities = spectrumService.getSpectrumPeaks(aFile);
                for (Double mz : mzAndIntensities.keySet()) {
                    Peak peak = new Peak(mz, mzAndIntensities.get(mz));
                    targetSpectrum.addPeak(peak);
                }
            } catch (IOException ex) {
                LOGGER.error(ex);
            }
        }

    }
}
