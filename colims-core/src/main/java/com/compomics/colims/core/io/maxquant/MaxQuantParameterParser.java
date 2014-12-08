package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantParameterHeaders;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component
public class MaxQuantParameterParser {

    /**
     * Get the experiment parameters from the data files
     * @param maxQuantTextFolder Path of experiment data
     * @return Map of parameters
     * @throws IOException
     */
    public Map<String, String> parseExperimentParams(File maxQuantTextFolder) throws IOException, HeaderEnumNotInitialisedException {
        Map<String, String> parameters = parseParameters(new File(maxQuantTextFolder, "parameters.txt"));

        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(maxQuantTextFolder, "summary.txt"));

        while (summaryIter.hasNext()) {
            Map<String, String> values = summaryIter.next();

            if (values.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getColumnName())) {
                parameters.put("multiplicity", values.get(MaxQuantSummaryHeaders.MULTIPLICITY.getColumnName()));
                break;
            }
        }

        return parameters;
    }

    //pretty ductapey atm, needs to be cleaned up 
    /**
     * parses the settings for the search per run searched
     *
     * @param maxQuantTextFolder the folder containing the textual max quant
     * output files
     * @return a {@code Map} that, for each raw file, contains the settings for
     * that search
     * @throws IOException
     */
    public Map<String, SearchParameters> parse(File maxQuantTextFolder) throws IOException, HeaderEnumNotInitialisedException {
        File parameterFile = new File(maxQuantTextFolder, "parameters.txt");
        File summaryFile = new File(maxQuantTextFolder, "summary.txt");

        Entry<String, String> values;
        Map<String, SearchParameters> runParams = new HashMap<>();
        SearchParameters globalParameters = new SearchParameters();
        Iterator<Entry<String, String>> parameterIter = parseParameters(parameterFile).entrySet().iterator();

        while (parameterIter.hasNext()) {
            // for some reason this file was built vertically, a cleaner solution is possible
            values = parameterIter.next();
            if (Arrays.asList(MaxQuantParameterHeaders.KEEP_LOW_SCORING_PEPTIDES.allPossibleColumnNames()).contains(values.getKey())) {
                globalParameters.setDiscardLowQualitySpectra(Boolean.getBoolean(values.getValue().toLowerCase(Locale.US)));
            } else if (Arrays.asList(MaxQuantParameterHeaders.FASTA_FILE.allPossibleColumnNames()).contains(values.getKey())) {
                globalParameters.setFastaFile(new File(FilenameUtils.separatorsToSystem(values.getValue())));
            } else if (Arrays.asList(MaxQuantParameterHeaders.MIN_PEP_LENGTH.allPossibleColumnNames()).contains(values.getKey())) {
                globalParameters.setMinPeptideLength(Integer.parseInt(values.getValue()));
            } else if (Arrays.asList(MaxQuantParameterHeaders.MAX_PEP_PEP.allPossibleColumnNames()).contains(values.getKey())) {
                globalParameters.setMaxEValue(Double.parseDouble(values.getValue()));
            } else if (Arrays.asList(MaxQuantParameterHeaders.FTMS_MS_MS_TOLERANCE.allPossibleColumnNames()).contains(values.getKey())) {
                globalParameters.setFragmentIonAccuracy(Double.parseDouble(values.getValue().split(" ")[0]));
                if (values.getValue().split(" ")[1].equalsIgnoreCase("da")) {
                    globalParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
                } else {
                    globalParameters.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.PPM);
                }
            }
        }
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(summaryFile);

        while (summaryIter.hasNext()) {
            SearchParameters runParameters = cloneSearchParametersObject(globalParameters);
            Map<String, String> valuesIter = summaryIter.next();
            if (valuesIter.containsKey(MaxQuantSummaryHeaders.RAW_FILE.getColumnName()) && !valuesIter.get(MaxQuantSummaryHeaders.RAW_FILE.getColumnName()).equalsIgnoreCase("total")) {
                ModificationProfile runModifications = new ModificationProfile();
                if (valuesIter.containsKey(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getColumnName())) {
                    for (String fixedMod : valuesIter.get(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getColumnName()).split(";")) {
                        if (!fixedMod.isEmpty()) {
                            PTM fixedPTM = new PTM();
                            fixedPTM.setName(fixedMod);
                            runModifications.addFixedModification(fixedPTM);
                        }
                    }
                }
                if (valuesIter.containsKey(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getColumnName())) {

                    for (String varMod : valuesIter.get(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getColumnName()).split(";")) {
                        if (!varMod.isEmpty()) {
                            PTM varPTM = new PTM();
                            varPTM.setName(varMod);
                            runModifications.addVariableModification(varPTM);
                        }
                    }
                }
                runParameters.setModificationProfile(runModifications);
                //runParameters.setEnzyme(valuesIter.get(MaxQuantSummaryHeaders.PROTEASE.getColumnName()));
                if (valuesIter.containsKey(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getColumnName()) && !valuesIter.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getColumnName()).isEmpty()) {
                    runParameters.setnMissedCleavages(Integer.parseInt(valuesIter.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getColumnName())));
                }
                MaxQuantParameterFileAggregator tarredParameters = new MaxQuantParameterFileAggregator(summaryFile, parameterFile);
                runParameters.setParametersFile(tarredParameters.getTarredParaMeterFiles());
                runParams.put(valuesIter.get(MaxQuantSummaryHeaders.RAW_FILE.getColumnName()), runParameters);
            }
        }
        return runParams;
    }

    public Map<String, String> parseParameters(File parameterFile) throws FileNotFoundException, IOException {
        Map<String, String> parameters = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(parameterFile);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
                LineNumberReader reader = new LineNumberReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length == 2) {
                    parameters.put(split[0], split[1]);
                } else {
                    parameters.put(split[0], "");
                }
            }
        }
        return parameters;
    }

    //naive copying of the data we fetched from the global parameters file
    private SearchParameters cloneSearchParametersObject(SearchParameters globalParameters) throws IOException {
        SearchParameters searchParams = new SearchParameters();
        searchParams.setDiscardLowQualitySpectra(globalParameters.getDiscardLowQualitySpectra());
        searchParams.setFastaFile(globalParameters.getFastaFile());
        searchParams.setMinPeptideLength(globalParameters.getMinPeptideLength());
        searchParams.setMaxEValue(globalParameters.getMaxEValue());
        searchParams.setFragmentIonAccuracy(globalParameters.getFragmentIonAccuracy());
        return searchParams;
    }
}
