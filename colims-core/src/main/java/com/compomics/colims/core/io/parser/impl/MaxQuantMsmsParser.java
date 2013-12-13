package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
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
 * instances and {@link Quantification}s and links them to the argument
 * {@link QuantificationGroup}.<br>
 * <br>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse
 * the files into Map<String,String>
 * records.
 */
@Component("maxQuantMsmsParser")
public class MaxQuantMsmsParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantMsmsParser.class);

    private HashMap<Double, Peak> parsePeakList(String peaklist, String intensities, String masses) {
        HashMap<Double, Peak> peakMap = new HashMap<>();
        String[] peakList = peaklist.split(";");
        String[] intensityList = intensities.split(";");
        String[] massList = masses.split(";");
        for (int i = 0; i < peakList.length; i++) {
            int charge = 1;
            if (peakList[i].contains("")) {
            }
            Double moverz = Double.parseDouble(massList[i]) / charge;
            peakMap.put(moverz, new Peak(moverz, Double.parseDouble(intensityList[i])));
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
     * @throws HeaderEnumNotInitialisedException if a header was requested that
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
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile,MsmsHeaders.values());

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create objects
            if (values.containsKey(MsmsHeaders.id.getColumnName())) {
                Integer id = Integer.parseInt(values.get(MsmsHeaders.id.getColumnName()));

                MSnSpectrum spectrum = parseSpectrum(values, addPeakList);
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
        //Double simpleMassError = Double.valueOf(values.get(MsmsHeaders.Simple_Mass_Error_ppm.column));
        // Andromeda score for the best associated MS/MS spectrum.
//            Double score = Double.valueOf(values.get(MsmsHeaders.Score.column));
        // Posterior Error Probability
//          Double pep = Double.valueOf(values.get(MsmsHeaders.PEP.column));
        // The type of precursor ion as identified by MaxQuant.
//            String type = values.get(MsmsHeaders.Type.column);
        //create the precursor of the fragment
        double rt = Double.valueOf(values.get(MsmsHeaders.Retention_Time.getColumnName()));
        // The mass-over-charge of the precursor ion. Double m_z =
        double mz = Double.valueOf(values.get(MsmsHeaders.m_z.getColumnName()));
        //charge - state of the precursor ion
        Precursor precursor = null;

        if (values.containsKey(MsmsHeaders.Charge.getColumnName()) && values.containsKey(MsmsHeaders.Precursor_Intensity.getColumnName())) {
            ArrayList<Charge> charges = new ArrayList<>();
            charges.add(new Charge(Charge.PLUS, Integer.valueOf(values.get(MsmsHeaders.Charge.getColumnName()))));
            Double precursorIntensity = Double.parseDouble(values.get(MsmsHeaders.Precursor_Intensity.getColumnName()));
            precursor = new Precursor(rt, mz, precursorIntensity, charges);
        } else {
            throw new UnparseableException("could not parse precursor");
        }
        String scanNumber = values.get(MsmsHeaders.Scan_Number.getColumnName());
        String fileName = values.get(MsmsHeaders.Raw_File.getColumnName());
        String spectrumTitle = String.format("%s-%s", fileName, values.get(MsmsHeaders.Scan_Number.getColumnName()));
        // we add an empty peaklist should there be no peaks to parse. it is initialised on null in the parent object and this could give problems down the line
        HashMap<Double, Peak> peakList = new HashMap<>();
        if (addPeakList
                && values.containsKey(MsmsHeaders.Matches.getColumnName()) && values.containsKey(MsmsHeaders.Intensities.getColumnName()) && values.containsKey(MsmsHeaders.Masses.getColumnName())) {
            peakList = parsePeakList(values.get(MsmsHeaders.Matches.getColumnName()), values.get(MsmsHeaders.Intensities.getColumnName()), values.get(MsmsHeaders.Masses.getColumnName()));
        }
        MSnSpectrum spectrum = new MSnSpectrum(2, precursor, spectrumTitle, peakList, fileName);
        spectrum.setScanNumber(scanNumber);
        return spectrum;
    }
// Create Quantification object and link to the quantificationGroup
//Quantification quantification = new Quantification();
// TODO The above values should possibly be added to this Quantification instance, to give it some content
//quantification.setSpectrum(spectrum);
//quantification.setQuantificationGroup(quantificationGroup);
//quantificationGroup.getQuantifications().add(quantification);

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
     * Integer.valueOf(values.get(MsmsHeaders.Charge.column));
     *
     * // The mass-over-charge of the precursor ion. Double m_z =
     * Double.valueOf(values.get(MsmsHeaders.m_z.column));
     *
     * // Set field values on Spectrum Spectrum spectrum = new Spectrum();
     * spectrum.setCharge(charge); spectrum.setMzRatio(m_z); return spectrum; }
     * }
     */
    /**
     * Refer to headers in MaxQuant msms.txt output files by enum values, as
     * headers are likely to change with each new version of MaxQuant. Their
     * order is also likely to change between files regardless of MaxQuant
     * version.
     */
    enum MsmsHeaders implements HeaderEnum {

        id(new String[]{"id"}),
        Protein_Group_IDs(new String[]{"Protein Group IDs"}),
        Peptide_ID(new String[]{"Peptide ID"}),
        Mod_Peptide_ID(new String[]{"Mod. Peptide ID"}),
        Evidence_ID(new String[]{"Evidence ID"}),
        Oxidation_M_Site_IDs(new String[]{"Oxidation (M) Site IDs"}),
        Raw_File(new String[]{"Raw File"}),
        Scan_Type(new String[]{"Scan Type"}),
        Scan_Number(new String[]{"Scan Number"}),
        Precursor(new String[]{"Precursor"}),
        Sequence(new String[]{"Sequence"}),
        Length(new String[]{"Length"}),
        Missed_Cleavages(new String[]{"Missed Cleavages"}),
        Modifications(new String[]{"Modifications"}),
        Modified_Sequence(new String[]{"Modified Sequence"}),
        Oxidation_M_Probabilities(new String[]{"Oxidation (M) Probabilities"}),
        Oxidation_M_Score_Diffs(new String[]{"Oxidation (M) Score Diffs"}),
        Acetyl_Protein_N_term(new String[]{"Acetyl (Protein N-term)"}),
        Oxidation_M(new String[]{"Oxidation (M)"}),
        Proteins(new String[]{"Proteins"}),
        Gene_Names(new String[]{"Gene Names"}),
        Protein_Names(new String[]{"Protein Names"}),
        Uniprot(new String[]{"Uniprot"}),
        Charge(new String[]{"Charge"}),
        Fragmentation(new String[]{"Fragmentation"}),
        Mass_analyzer(new String[]{"Mass analyzer"}),
        Type(new String[]{"Type"}),
        Scan_event_number(new String[]{"Scan event number"}),
        Isotope_Index(new String[]{"Isotope Index"}),
        m_z(new String[]{"m/z"}),
        Mass(new String[]{"Mass"}),
        Mass_Error_ppm(new String[]{"Mass Error [ppm]"}),
        Simple_Mass_Error_ppm(new String[]{"Simple Mass Error [ppm]"}),
        Retention_Time(new String[]{"Retention Time"}),
        PEP(new String[]{"PEP"}),
        Score(new String[]{"Score"}),
        Delta_score(new String[]{"Delta score"}),
        Combinatorics(new String[]{"Combinatorics"}),
        PIF(new String[]{"PIF"}),
        Fraction_of_total_spectrum(new String[]{"Fraction of total spectrum"}),
        Base_peak_fraction(new String[]{"Base peak fraction"}),
        Precursor_Full_ScanNumber(new String[]{"Precursor Full ScanNumber"}),
        Precursor_Intensity(new String[]{"Precursor Intensity"}),
        Precursor_Apex_Fraction(new String[]{"Precursor Apex Fraction"}),
        Precursor_Apex_Offset(new String[]{"Precursor Apex Offset"}),
        Matches(new String[]{"Matches"}),
        Intensities(new String[]{"Intensities"}),
        Mass_Deviations_Da(new String[]{"Mass Deviations [Da]"}),
        Mass_Deviations_ppm(new String[]{"Mass Deviations [ppm]"}),
        Masses(new String[]{"Masses"}),
        Number_of_Matches(new String[]{"Number of Matches"}),
        Intensity_coverage(new String[]{"Intensity coverage"}),
        Peak_coverage(new String[]{"Peak coverage"}),
        Neutral_loss_level(new String[]{"Neutral loss level"}),
        Reverse(new String[]{"Reverse"});
        protected String[] columnNames;
        protected int columnReference = -1;

        private MsmsHeaders(final String[] fieldnames) {
            columnNames = fieldnames;
        }

        @Override
        public final String[] returnPossibleColumnNames() {
            return columnNames;
        }

        @Override
        public final void setColumnReference(int columnReference) {
            this.columnReference = columnReference;
        }

        @Override
        public final String getColumnName() throws HeaderEnumNotInitialisedException {
            if (columnNames != null) {
                if (columnReference < 0 || columnReference > (columnNames.length - 1) && columnNames.length > 0) {
                    return columnNames[0];
                } else if (columnNames.length < 0) {
                    throw new HeaderEnumNotInitialisedException("header enum not initialised");
                } else {
                    return columnNames[columnReference].toLowerCase(Locale.US);
                }
            } else {
                throw new HeaderEnumNotInitialisedException("array was null");
            }
        }
    }
}