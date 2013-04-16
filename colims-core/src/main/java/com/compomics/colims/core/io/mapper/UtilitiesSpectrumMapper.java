package com.compomics.colims.core.io.mapper;

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
public class UtilitiesSpectrumMapper implements Mapper<MSnSpectrum, Spectrum> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSpectrumMapper.class);

    @Override
    public void map(MSnSpectrum source, Spectrum target) throws MappingException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }

        //get precursor from source
        Precursor precursor = source.getPrecursor();

        //create new mgf file from scratch
        MascotGenericFile mascotGenericFile = new MascotGenericFile();
        mascotGenericFile.setFilename(source.getSpectrumTitle());

        //set mascot file properties
        mascotGenericFile.setComments("");
        mascotGenericFile.setPrecursorMZ(precursor.getMz());
        mascotGenericFile.setIntensity(precursor.getIntensity());
        //@todo is it possible that a precursor has multiple charge possibilities?
        if (!precursor.getPossibleCharges().isEmpty()) {
            mascotGenericFile.setCharge(source.getPrecursor().getPossibleCharges().get(0).value);
        }

        //set target spectrum properties        
        //@todo is spectrum key the correct accession property?
        target.setAccession(source.getSpectrumKey());
        target.setTitle(source.getSpectrumTitle());
        target.setScanNumber(source.getScanNumber());
        target.setMzRatio(precursor.getMz());
        target.setIntensity(precursor.getIntensity());
        target.setRetentionTime(precursor.getRt());
        if (!precursor.getPossibleCharges().isEmpty()) {
            target.setCharge(precursor.getPossibleCharges().get(0).value);
        }

        //copy spectrum peaks
        HashMap<Double, Double> peaks = new HashMap<>();
        for (Peak peak : source.getPeakList()) {
            peaks.put(peak.mz, peak.intensity);
        }
        mascotGenericFile.setPeaks(peaks);

        //create new SpectrumFile
        SpectrumFile spectrumFile = new SpectrumFile();
        spectrumFile.setSpectrum(target);

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
        target.setSpectrumFiles(spectrumFiles);
    }
}
