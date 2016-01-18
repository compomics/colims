package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class contains utilities methods for parsing MaxQuant files.
 * <p/>
 * Created by Niels Hulstaert on 14/01/16.
 */
public class ParseUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ParseUtils() {
    }

    /**
     * Parse the given parameters file and put the parameters in a map.
     *
     * @param parametersFile the given parameters file
     * @param toLowerCase    whether or not to convert the parameter namses to lowercase
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     * @throws IOException thrown in case of of an I/O related problem
     */
    public static Map<String, String> parseParameters(File parametersFile, boolean toLowerCase) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(parametersFile.toURI()), StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(MaxQuantConstants.PARAM_DELIMITER.value());

                if (split.length == 2) {
                    if (toLowerCase) {
                        parameters.put(split[0].toLowerCase(Locale.US), split[1]);
                    } else {
                        parameters.put(split[0], split[1]);
                    }
                } else {
                    if (toLowerCase) {
                        parameters.put(split[0].toLowerCase(Locale.US), "");
                    } else {
                        parameters.put(split[0], "");
                    }
                }
            }
        }

        return parameters;
    }

}
