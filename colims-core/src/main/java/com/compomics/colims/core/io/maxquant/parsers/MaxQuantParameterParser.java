package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantParameterHeaders;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantSummaryHeaders;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.ModificationProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

/**
 * @author Davy
 */
@Component
public class MaxQuantParameterParser {

    private String version, multiplicity = null;
    private Map<String, SearchParameters> runParameters = new HashMap<>();

    private static final String PARAMETERS = "parameters.txt";
    private static final String SUMMARY = "summary.txt";

    public void parse(File quantFolder) throws IOException {
        SearchParameters experimentParams = parseExperiment(quantFolder);

        parseRuns(quantFolder, experimentParams);
    }

    public SearchParameters parseExperiment(File quantFolder) throws IOException {
        SearchParameters experimentParams = new SearchParameters();
        File parameterFile = new File(quantFolder, PARAMETERS);

        Iterator<Entry<String, String>> parameterIter = parseParameters(parameterFile).entrySet().iterator();
        Entry<String, String> row;

        while (parameterIter.hasNext()) {
            row = parameterIter.next();

            if (row.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.FASTA_FILE.getDefaultColumnName())) {
                experimentParams.setFastaFile(new File(FilenameUtils.separatorsToSystem(row.getValue())));
            } else if (row.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.FTMS_MS_MS_TOLERANCE.getDefaultColumnName())) {
                experimentParams.setFragmentIonAccuracy(Double.parseDouble(row.getValue().split(" ")[0]));

                if (row.getValue().split(" ")[1].equalsIgnoreCase("da")) {
                    experimentParams.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.DA);
                } else {
                    experimentParams.setPrecursorAccuracyType(SearchParameters.MassAccuracyType.PPM);
                }

            } else if (row.getKey().equalsIgnoreCase(MaxQuantParameterHeaders.VERSION.getDefaultColumnName())) {
                version = row.getValue();
            }
        }

        return experimentParams;
    }

    public Map<String, SearchParameters> parseRuns(File quantFolder, SearchParameters experimentParams) throws IOException {
        TabularFileLineValuesIterator summaryIter = new TabularFileLineValuesIterator(new File(quantFolder, SUMMARY));
        Map<String, String> row;

        while (summaryIter.hasNext()) {
            row = summaryIter.next();
            SearchParameters run = cloneSearchParametersObject(experimentParams);

            if (multiplicity == null && row.containsKey(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName())) {
                multiplicity = row.get(MaxQuantSummaryHeaders.MULTIPLICITY.getDefaultColumnName());
            }

            if (row.containsKey(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()) && !row.get(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()).equalsIgnoreCase("total")) {
                ModificationProfile runModifications = new ModificationProfile();

                if (row.containsKey(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getDefaultColumnName())) {
                    for (String fixedMod : row.get(MaxQuantSummaryHeaders.FIXED_MODIFICATIONS.getDefaultColumnName()).split(";")) {
                        if (!fixedMod.isEmpty()) {
                            PTM fixedPTM = new PTM();
                            fixedPTM.setName(fixedMod);
                            runModifications.addFixedModification(fixedPTM);
                        }
                    }
                }

                if (row.containsKey(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getDefaultColumnName())) {
                    for (String varMod : row.get(MaxQuantSummaryHeaders.VARIABLE_MODIFICATIONS.getDefaultColumnName()).split(";")) {
                        if (!varMod.isEmpty()) {
                            PTM varPTM = new PTM();
                            varPTM.setName(varMod);
                            runModifications.addVariableModification(varPTM);
                        }
                    }
                }

                run.setModificationProfile(runModifications);

                // dummy enzyme (only name required)
                if (row.containsKey(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName()) && !row.get(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName()).isEmpty()) {
                    run.setEnzyme(new Enzyme(0, row.get(MaxQuantSummaryHeaders.PROTEASE.getDefaultColumnName()), "a", "a", "a", "a"));
                }

                if (row.containsKey(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName()) && !row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName()).isEmpty()) {
                    run.setnMissedCleavages(Integer.parseInt(row.get(MaxQuantSummaryHeaders.MAX_MISSED_CLEAVAGES.getDefaultColumnName())));
                }

                //MaxQuantParameterFileAggregator tarredParameters = new MaxQuantParameterFileAggregator(summaryFile, parameterFile);
                //run.setParametersFile(tarredParameters.getTarredParaMeterFiles());
                runParameters.put(row.get(MaxQuantSummaryHeaders.RAW_FILE.getDefaultColumnName()), run);
            }
        }

        return runParameters;
    }

    /**
     * Get the version of MaxQuant used for the experiment
     *
     * @return Version number
     */
    public String getMaxQuantVersion() {
        return version;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public Map<String, SearchParameters> getRunParameters() {
        return Collections.unmodifiableMap(runParameters);
    }

    public void clear() {
        runParameters.clear();
        version = null;
        multiplicity = null;
    }

    public Map<String, String> parseParameters(File parameterFile) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(parameterFile);
             InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
             LineNumberReader reader = new LineNumberReader(isr)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");

                if (split.length == 2) {
                    parameters.put(split[0].toLowerCase(Locale.US), split[1]);
                } else {
                    parameters.put(split[0].toLowerCase(Locale.US), "");
                }
            }
        }

        return parameters;
    }

    //naive copying of the data we fetched from the global parameters file
    private SearchParameters cloneSearchParametersObject(SearchParameters experimentParameters) {
        SearchParameters searchParams = new SearchParameters();
        searchParams.setFastaFile(experimentParameters.getFastaFile());
        searchParams.setFragmentIonAccuracy(experimentParameters.getFragmentIonAccuracy());
        return searchParams;
    }
}
