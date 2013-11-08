package com.compomics.colims.core.mapper.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.model.MascotGenericFile;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesSpectrumMapper")
public class UtilitiesSpectrumMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSpectrumMapper.class);

    /**
     * Map the utilities spectrum onto the colims spectrum.
     * 
     * @param sourceSpectrum the utilities spectrum
     * @param charge the spectrum charge
     * @param targetSpectrum the colims spectrum
     * @throws MappingException 
     */
    public void map(MSnSpectrum sourceSpectrum, int charge, Spectrum targetSpectrum) throws MappingException {
        LOGGER.debug("Start mapping MSnSpectrum with title" + sourceSpectrum.getSpectrumTitle());

        if (sourceSpectrum == null || targetSpectrum == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }

        //get precursor from source
        Precursor precursor = sourceSpectrum.getPrecursor();

        //create new mgf file from scratch
        MascotGenericFile mascotGenericFile = new MascotGenericFile();
        mascotGenericFile.setFilename(sourceSpectrum.getSpectrumTitle());

        //set mascot file properties
        mascotGenericFile.setComments("");
        mascotGenericFile.setPrecursorMZ(precursor.getMz());
        mascotGenericFile.setIntensity(precursor.getIntensity());
        mascotGenericFile.setCharge(charge);

        //set target spectrum properties        
        //@todo is spectrum key the correct accession property?
        targetSpectrum.setAccession(sourceSpectrum.getSpectrumKey());
        targetSpectrum.setTitle(sourceSpectrum.getSpectrumTitle());
        targetSpectrum.setScanNumber(sourceSpectrum.getScanNumber());
        targetSpectrum.setMzRatio(precursor.getMz());
        targetSpectrum.setIntensity(precursor.getIntensity());
        targetSpectrum.setRetentionTime(precursor.getRt());
        targetSpectrum.setCharge(charge);

        //copy spectrum peaks
        HashMap<Double, Double> peaks = new HashMap<>();
        for (Peak peak : sourceSpectrum.getPeakList()) {
            peaks.put(peak.mz, peak.intensity);
        }
        mascotGenericFile.setPeaks(peaks);

        //create new SpectrumFile
        SpectrumFile spectrumFile = new SpectrumFile();
        spectrumFile.setSpectrum(targetSpectrum);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream zippedByteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(zippedByteArrayOutputStream);) {

            //write mgf file as stream to byte array
            mascotGenericFile.writeToStream(byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] unzippedBytes = byteArrayOutputStream.toByteArray();

            //gzip byte array
            gZIPOutputStream.write(unzippedBytes);
            gZIPOutputStream.flush();
            gZIPOutputStream.finish();
            //set content of the SpectrumFile
            spectrumFile.setContent(zippedByteArrayOutputStream.toByteArray());
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        }

        //set SpectrumFiles
        List<SpectrumFile> spectrumFiles = new ArrayList<>();
        spectrumFiles.add(spectrumFile);
        targetSpectrum.setSpectrumFiles(spectrumFiles);

        LOGGER.debug("Finished mapping MSnSpectrum with title" + sourceSpectrum.getSpectrumTitle());
    }
}
