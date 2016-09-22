package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
@Component("colimsSpectrumMapper")
public class ColimsSpectrumMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsSpectrumMapper.class);

    private final SpectrumService spectrumService;

    @Autowired
    public ColimsSpectrumMapper(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    /**
     * Map the utilities spectrum onto the colims spectrum.
     *
     * @param sourceSpectrum the colims spectrum
     * @param targetSpectrum the utilities MSnSpectrum
     * @throws MappingException
     */
    public void map(final Spectrum sourceSpectrum, final MSnSpectrum targetSpectrum) throws MappingException {
        if (sourceSpectrum == null || targetSpectrum == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }

        LOGGER.debug("Start mapping MSnSpectrum with title" + sourceSpectrum.getTitle());

        //Build the precursor
        double retentionTime = sourceSpectrum.getRetentionTime();
        double mzRatio = sourceSpectrum.getMzRatio();
        ArrayList<Charge> chargeList = new ArrayList<>();
        if (sourceSpectrum.getCharge() != null) {
            chargeList.add(new Charge(1, sourceSpectrum.getCharge()));
        }
        Precursor precursor = new Precursor(retentionTime, mzRatio, sourceSpectrum.getIntensity(), chargeList);
        targetSpectrum.setPrecursor(precursor);
        //Add other parameters
        targetSpectrum.setScanNumber(sourceSpectrum.getScanNumber());
        targetSpectrum.setScanStartTime(sourceSpectrum.getScanTime());
        targetSpectrum.setSpectrumTitle(sourceSpectrum.getTitle());
        //Add peaks
        for (SpectrumFile aFile : sourceSpectrum.getSpectrumFiles()) {
            try {
                Map<Double, Double> mzAndIntensities = spectrumService.getSpectrumPeaks(aFile);
                mzAndIntensities.entrySet().stream().map((entry) -> new Peak(entry.getKey(), entry.getValue())).forEach((peak) -> {
                    targetSpectrum.addPeak(peak);
                });
            } catch (IOException ex) {
                LOGGER.error(ex);
                throw new MappingException(ex);
            }
        }
    }
}
