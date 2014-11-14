package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Precursor;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class maps a Compomics Utilities spectrum to a Colims spectrum.
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesSpectrumMapper")
public class UtilitiesSpectrumMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesSpectrumMapper.class);

    /**
     * Map the utilities spectrum onto the Colims spectrum.
     *
     * @param sourceSpectrum the Utilities spectrum
     * @param fragmentationType the fragmentation type of the spectrum
     * @param targetSpectrum the Colims spectrum
     * @throws MappingException thrown in case of a mapping related problem
     */
    public void map(final MSnSpectrum sourceSpectrum, final FragmentationType fragmentationType, final Spectrum targetSpectrum) throws MappingException {
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
        if (!precursor.getPossibleCharges().isEmpty()) {
            targetSpectrum.setCharge(precursor.getPossibleCharges().get(0).value);
        }
        if (fragmentationType != null) {
            targetSpectrum.setFragmentationType(fragmentationType);
        }

        //create new SpectrumFile
        SpectrumFile spectrumFile = new SpectrumFile();
        spectrumFile.setSpectrum(targetSpectrum);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName("UTF-8").newEncoder());
                BufferedWriter bw = new BufferedWriter(osw);
                ByteArrayOutputStream zbaos = new ByteArrayOutputStream();
                GZIPOutputStream gzipos = new GZIPOutputStream(zbaos)) {

            //write MSnSpectum to a byte array output stream
            sourceSpectrum.writeMgf(bw);
            bw.flush();

            //get the bytes from the stream
            byte[] unzippedBytes = baos.toByteArray();

            //gzip byte array
            gzipos.write(unzippedBytes);
            gzipos.flush();
            gzipos.finish();

            //set content of the SpectrumFile
            spectrumFile.setContent(zbaos.toByteArray());
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
