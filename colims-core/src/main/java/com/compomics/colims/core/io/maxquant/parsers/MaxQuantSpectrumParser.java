package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.core.io.maxquant.urparams.SpectrumIntUrParameterShizzleStuff;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for the MaxQuant msms.txt output files that creates {@link Spectrum} instances.
 * <p/>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse the files into Map<String,String>
 * records.
 */
@Component("maxQuantSpectrumParser")
public class MaxQuantSpectrumParser {

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantMSMSHeaders.ID,
            MaxQuantMSMSHeaders.FRAGMENTATION,
            MaxQuantMSMSHeaders.MATCHES,
            MaxQuantMSMSHeaders.INTENSITIES,
            MaxQuantMSMSHeaders.MASSES,
    };

    /**
     * parses a max quant msms text file without adding a peaklist to each spectrum
     *
     * @param msmsFile the file to parse
     * @return a map with key: spectrumid and value the corresponding spectrum
     * @throws IOException
     */
    public Map<Integer, MSnSpectrum> parse(final File msmsFile) throws IOException, UnparseableException {
        return parse(msmsFile, false);
    }

    /**
     * Parse the argument msms.txt file, and link all {@link Spectrum} instances and {@link Quantification}s found
     * within to the argument {@link QuantificationGroup}.
     *
     * @param msmsFile
     * @throws IOException
     * @boolean addPeakList if a peaklist should be added to the spectra, should a peak list be requested and none could
     * be built, an empty peaklist is added
     */
    public Map<Integer, MSnSpectrum> parse(final File msmsFile, boolean addPeakList) throws IOException, UnparseableException {
        Map<Integer, MSnSpectrum> spectrumMap = new HashMap<>();
        // Convert file into some values we can loop over, without reading file in at once
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, mandatoryHeaders);

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create objects
            Integer id = Integer.parseInt(values.get(MaxQuantMSMSHeaders.ID.getDefaultColumnName()));

            MSnSpectrum spectrum = parseSpectrum(values, addPeakList);
            SpectrumIntUrParameterShizzleStuff nastyworkaround = new SpectrumIntUrParameterShizzleStuff();
            nastyworkaround.setSpectrumid(id);
            spectrum.addUrParam(nastyworkaround);
            spectrumMap.put(id, spectrum);

        }
        return spectrumMap;
    }

    /**
     * Parse the fragmentations in an MSMS file
     *
     * @param msmsFile File pointer
     * @return Map of ids and fragmentation types
     * @throws IOException                       thrown in case of an I/O related problem
     */
    public Map<Integer, FragmentationType> parseFragmentations(final File msmsFile) throws IOException {
        Map<Integer, FragmentationType> fragmentations = new HashMap<>();

        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, mandatoryHeaders);

        for (Map<String, String> values : valuesIterator) {
            // Create objects
            Integer id = Integer.parseInt(values.get(MaxQuantMSMSHeaders.ID.getDefaultColumnName()));
            fragmentations.put(id, FragmentationType.valueOf(values.get(MaxQuantMSMSHeaders.FRAGMENTATION.getDefaultColumnName())));
        }

        return fragmentations;
    }

    /**
     * Creates a unique (consecutive) identifier for each row in the msms table, which is used to cross-link the
     * information in this file with the information stored in the other files.
     *
     * @param values      List of values from the data file
     * @param addPeakList Whether peak list should be parsed and included in spectrum
     * @return Spectrum object
     * @throws UnparseableException
     */
    private MSnSpectrum parseSpectrum(Map<String, String> values, boolean addPeakList)throws UnparseableException {
        //create the precursor of the fragment
        double rt = Double.valueOf(values.get(MaxQuantMSMSHeaders.RETENTION_TIME.getDefaultColumnName()));
        // The mass-over-charge of the precursor ion. Double m_z =
        double mz = Double.valueOf(values.get(MaxQuantMSMSHeaders.M_Z.getDefaultColumnName()));
        //charge - state of the precursor ion
        Precursor precursor;

        if (values.containsKey(MaxQuantMSMSHeaders.CHARGE.getDefaultColumnName()) && values.containsKey(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getDefaultColumnName())) {
            ArrayList<Charge> charges = new ArrayList<>();
            charges.add(new Charge(Charge.PLUS, Integer.valueOf(values.get(MaxQuantMSMSHeaders.CHARGE.getDefaultColumnName()))));
            String precursorIntensityString = values.get(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getDefaultColumnName());

            if (precursorIntensityString.equalsIgnoreCase("nan")) {
                precursorIntensityString = "-1";
            }

            Double precursorIntensity = Double.parseDouble(precursorIntensityString);
            precursor = new Precursor(rt, mz, precursorIntensity, charges);
        } else {
            throw new UnparseableException("could not parse precursor");
        }

        String scanNumber = values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName());
        String fileName = values.get(MaxQuantMSMSHeaders.RAW_FILE.getDefaultColumnName());
        String spectrumTitle = String.format("%s-%s", fileName, values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName()));

        // we add an empty peaklist should there be no peaks to parse. it is initialised on null in the parent object and this could give problems down the line
        HashMap<Double, Peak> peakList = parsePeakList(values.get(MaxQuantMSMSHeaders.MATCHES.getDefaultColumnName()), values.get(MaxQuantMSMSHeaders.INTENSITIES.getDefaultColumnName()), values.get(MaxQuantMSMSHeaders.MASSES.getDefaultColumnName()));


        MSnSpectrum spectrum = new MSnSpectrum(2, precursor, spectrumTitle, peakList, fileName);
        spectrum.setScanNumber(scanNumber);

        return spectrum;
    }

    /**
     * Parse a series of strings (separated with ;) (not winky face) and create some mad peaks
     *
     * @param peaks       String of peaks
     * @param intensities String of intensities
     * @param masses      String of masses
     * @return A map of peaks keyed with m/z
     */
    public HashMap<Double, Peak> parsePeakList(String peaks, String intensities, String masses) throws IllegalArgumentException {
        HashMap<Double, Peak> peakMap = new HashMap<>();

        if (!peaks.isEmpty() && !intensities.isEmpty() && !masses.isEmpty()) {
            String[] peakList = peaks.split(";");
            String[] intensityList = intensities.split(";");
            String[] massList = masses.split(";");

            if (intensityList.length != peakList.length || massList.length != peakList.length) {
                throw new IllegalArgumentException("Input lists are not equal length");
            }

            for (int i = 0; i < peakList.length; i++) {
                int charge = 1;

                Double moverz = Double.parseDouble(massList[i]) / charge;
                peakMap.put(moverz, new Peak(moverz, Double.parseDouble(intensityList[i])));
            }
        }

        return peakMap;
    }
}
