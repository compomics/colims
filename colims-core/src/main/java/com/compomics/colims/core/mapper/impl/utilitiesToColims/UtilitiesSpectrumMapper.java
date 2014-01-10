package com.compomics.colims.core.mapper.impl.utilitiesToColims;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Precursor;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
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
     * @param fragmentationType the fragmentation type of the spectrum
     * @param targetSpectrum the colims spectrum
     * @throws MappingException
     */
    public void map(MSnSpectrum sourceSpectrum, FragmentationType fragmentationType, Spectrum targetSpectrum) throws MappingException {        
        if (sourceSpectrum == null || targetSpectrum == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        
        LOGGER.debug("Start mapping MSnSpectrum with title" + sourceSpectrum.getSpectrumTitle());

        //get precursor from source
        Precursor precursor = sourceSpectrum.getPrecursor();

        //set target spectrum properties        
        //@todo is spectrum key the correct accession property?
        targetSpectrum.setAccession(sourceSpectrum.getSpectrumKey());
        targetSpectrum.setTitle(sourceSpectrum.getSpectrumTitle());
        targetSpectrum.setScanNumber(sourceSpectrum.getScanNumber());
        targetSpectrum.setScanTime(sourceSpectrum.getScanStartTime());
        targetSpectrum.setMzRatio(precursor.getMz());
        targetSpectrum.setIntensity(precursor.getIntensity());
        targetSpectrum.setRetentionTime(precursor.getRt());
        targetSpectrum.setCharge(precursor.getPossibleCharges().get(0).value);
        if (fragmentationType != null) {
            targetSpectrum.setFragmentationType(fragmentationType);
        }

        //create new SpectrumFile
        SpectrumFile spectrumFile = new SpectrumFile();
        spectrumFile.setSpectrum(targetSpectrum);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
                ByteArrayOutputStream zippedByteArrayOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(zippedByteArrayOutputStream);) {

            //write MSnSpectum to a byte array output stream
            sourceSpectrum.writeMgf(bufferedWriter);
            bufferedWriter.flush();
            
            //get the bytes from the stream
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
