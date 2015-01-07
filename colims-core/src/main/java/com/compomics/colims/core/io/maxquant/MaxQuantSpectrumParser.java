package com.compomics.colims.core.io.maxquant;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Parser for the MaxQuant msms.txt output files that creates {@link Spectrum}
 * instances
 * <br>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse
 * the files into Map<String,String>
 * records.
 */
@Component("maxQuantSpectrumParser")
public class MaxQuantSpectrumParser {

    public Map<Integer, FragmentationType> parseFragmenations(final File msmsFile) throws IOException, UnparseableException, HeaderEnumNotInitialisedException {
        Map<Integer, FragmentationType> fragmentations = new HashMap<>();

        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, MaxQuantMSMSHeaders.values());

        for (Map<String, String> values : valuesIterator) {
            // Create objects
            if (values.containsKey(MaxQuantMSMSHeaders.ID.getColumnName())) {
                Integer id = Integer.parseInt(values.get(MaxQuantMSMSHeaders.ID.getColumnName()));

                fragmentations.put(id, FragmentationType.valueOf(values.get(MaxQuantMSMSHeaders.FRAGMENTATION.getColumnName())));
            }
        }

        return fragmentations;
    }

    private HashMap<Double, Peak> parsePeakList(String peaklist, String intensities, String masses) {
        HashMap<Double, Peak> peakMap = new HashMap<>();

        if (!peaklist.isEmpty() && !intensities.isEmpty() && !masses.isEmpty()) {
            String[] peakList = peaklist.split(";");
            String[] intensityList = intensities.split(";");
            String[] massList = masses.split(";");

            for (int i = 0; i < peakList.length; i++) {
                int charge = 1;

                Double moverz = Double.parseDouble(massList[i]) / charge;
                peakMap.put(moverz, new Peak(moverz, Double.parseDouble(intensityList[i])));
            }
        }

        return peakMap;
    }

    /**
     * parses a max quant msms text file without adding a peaklist to each
     * spectrum
     *
     * @param msmsFile the file to parse
     * @return a map with key: spectrumid and value the corresponding
     * {@link  MSnSpectrumm}
     * @throws IOException
     * @throws com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException if a header was requested that
     * was not defined
     */
    public Map<Integer, MSnSpectrum> parse(final File msmsFile) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        return parse(msmsFile, false);
    }

    /**
     * Parse the argument msms.txt file, and link all {@link Spectrum} instances
     * and {@link Quantification}s found within to the argument
     * {@link QuantificationGroup}.
     *
     * @param msmsFile
     * @boolean addPeakList if a peaklist should be added to the spectra, should
     * a peak list be requested and none could be built, an empty peaklist is
     * added
     * @throws IOException
     */
    public Map<Integer, MSnSpectrum> parse(final File msmsFile, boolean addPeakList) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        Map<Integer, MSnSpectrum> spectrumMap = new HashMap<>();
        // Convert file into some values we can loop over, without reading file in at once
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, MaxQuantMSMSHeaders.values());

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create objects
            if (values.containsKey(MaxQuantMSMSHeaders.ID.getColumnName())) {
                Integer id = Integer.parseInt(values.get(MaxQuantMSMSHeaders.ID.getColumnName()));

                MSnSpectrum spectrum = parseSpectrum(values, addPeakList);
                SpectrumIntUrParameterShizzleStuff nastyworkaround = new SpectrumIntUrParameterShizzleStuff();
                nastyworkaround.setSpectrumid(id);
                spectrum.addUrParam(nastyworkaround);
                spectrumMap.put(id, spectrum);
            }
        }
        return spectrumMap;
    }

    /**
     *
     * @param values
     * @param addPeakList
     * @return
     * @throws HeaderEnumNotInitialisedException
     * @throws UnparseableException
     */
    private MSnSpectrum parseSpectrum(Map<String, String> values, boolean addPeakList) throws HeaderEnumNotInitialisedException, UnparseableException {
        // A unique (consecutive) identifier for each row in the msms
        // table, which is used to cross-link the information in this file with
        // the information stored in the other files.
        // - Unannotated in documentation -
        //Double simpleMassError = Double.valueOf(values.get(MaxQuantMSMSHeaders.Simple_Mass_Error_ppm.column));
        // Andromeda score for the best associated MS/MS spectrum.
//            Double score = Double.valueOf(values.get(MaxQuantMSMSHeaders.Score.column));
        // Posterior Error Probability
//          Double pep = Double.valueOf(values.get(MaxQuantMSMSHeaders.PEP.column));
        // The type of precursor ion as identified by MaxQuant.
//            String type = values.get(MaxQuantMSMSHeaders.Type.column);
        //create the precursor of the fragment
        double rt = Double.valueOf(values.get(MaxQuantMSMSHeaders.RETENTION_TIME.getColumnName()));
        // The mass-over-charge of the precursor ion. Double m_z =
        double mz = Double.valueOf(values.get(MaxQuantMSMSHeaders.M_Z.getColumnName()));
        //charge - state of the precursor ion
        Precursor precursor = null;

        if (values.containsKey(MaxQuantMSMSHeaders.CHARGE.getColumnName()) && values.containsKey(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getColumnName())) {
            ArrayList<Charge> charges = new ArrayList<>();
            charges.add(new Charge(Charge.PLUS, Integer.valueOf(values.get(MaxQuantMSMSHeaders.CHARGE.getColumnName()))));
            String precursorIntensityString = values.get(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getColumnName());
            if (precursorIntensityString.equalsIgnoreCase("NAN")) {
                precursorIntensityString = "-1";
            }
            Double precursorIntensity = Double.parseDouble(precursorIntensityString);
            precursor = new Precursor(rt, mz, precursorIntensity, charges);
        } else {
            throw new UnparseableException("could not parse precursor");
        }
        String scanNumber = values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getColumnName());
        String fileName = values.get(MaxQuantMSMSHeaders.RAW_FILE.getColumnName());
        String spectrumTitle = String.format("%s-%s", fileName, values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getColumnName()));
        // we add an empty peaklist should there be no peaks to parse. it is initialised on null in the parent object and this could give problems down the line
        HashMap<Double, Peak> peakList = new HashMap<>();
        if (addPeakList
                && values.containsKey(MaxQuantMSMSHeaders.MATCHES.getColumnName()) && values.containsKey(MaxQuantMSMSHeaders.INTENSITIES.getColumnName()) && values.containsKey(MaxQuantMSMSHeaders.MASSES.getColumnName())) {
            peakList = parsePeakList(values.get(MaxQuantMSMSHeaders.MATCHES.getColumnName()), values.get(MaxQuantMSMSHeaders.INTENSITIES.getColumnName()), values.get(MaxQuantMSMSHeaders.MASSES.getColumnName()));
        }
        MSnSpectrum spectrum = new MSnSpectrum(2, precursor, spectrumTitle, peakList, fileName);
        spectrum.setScanNumber(scanNumber);



        return spectrum;
    }

    /**
     * Create a very sparsely populated {@link Spectrum} instance, as not all
     * the desired information can be restrieved from the file we are parsing.
     * The misisng values will be supplied at a later point in time by other
     * components.
     *
     * @param values
     * @return the created spectrum
    */
    /**
     * Spectrum createSpectrum(final Map<String, String> values) { // The
     * charge-state of the precursor ion. Integer charge =
     * Integer.valueOf(values.get(MaxQuantMSMSHeaders.Charge.column));
     *
     * // The mass-over-charge of the precursor ion. Double m_z =
     * Double.valueOf(values.get(MaxQuantMSMSHeaders.m_z.column));
     *
     * // Set field values on Spectrum Spectrum spectrum = new Spectrum();
     * spectrum.setCharge(charge); spectrum.setMzRatio(m_z); return spectrum; }
     * }
     */
}
