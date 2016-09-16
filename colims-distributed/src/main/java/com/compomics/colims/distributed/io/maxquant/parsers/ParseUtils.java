package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
     * Parse the given parameters file and put the parameters in a map (key: parameters name; value: parameter value).
     *
     * @param parametersPath the given parameters path
     * @param delimiter      the delimiter between the parameter name and value
     * @param toLowerCase    whether or not to convert the parameter names to lowercase
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     * @throws IOException thrown in case of of an I/O related problem
     */
    public static Map<String, String> parseParameters(Path parametersPath, String delimiter, boolean toLowerCase) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        try (BufferedReader bufferedReader = Files.newBufferedReader(parametersPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                parseParameter(line, parameters, delimiter, toLowerCase);
            }
        }

        return parameters;
    }

    /**
     * Parse the given parameters file and put the parameters in a map (key: parameters name; value: parameter value).
     *
     * @param parametersPath  the given parameters path
     * @param maxQuantHeaders the mandatory MaxQuant headers
     * @param delimiter       the delimiter between the parameter name and value
     * @param toLowerCase     whether or not to convert the parameter names to lowercase
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     * @throws IOException              thrown in case of of an I/O related problem
     * @throws IllegalArgumentException in case one of the mandatory headers is not present
     */
    public static Map<String, String> parseParameters(Path parametersPath, List<MaxQuantHeader> maxQuantHeaders, String delimiter, boolean toLowerCase) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        try (BufferedReader bufferedReader = Files.newBufferedReader(parametersPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                parseParameter(line, parameters, delimiter, toLowerCase);
            }
        }

        //check for the mandatory headers
        for (MaxQuantHeader maxQuantHeader : maxQuantHeaders) {
            Optional<String> optionalHeader = maxQuantHeader.getValues()
                    .stream()
                    .filter(parameters.keySet()::contains)
                    .findFirst();

            if (optionalHeader.isPresent()) {
                maxQuantHeader.setParsedValue(maxQuantHeader.getValues().indexOf(optionalHeader.get()));
            } else {
                throw new IllegalArgumentException("The mandatory header " + maxQuantHeader.getName() + " is not present in the given file " + parametersPath.getFileName());
            }
        }

        return parameters;
    }

    /**
     * Parse the given parameters file and put the parameters in a map (key: parameters name; value: parameter value).
     *
     * @param parametersPath the given parameters path
     * @param delimiter      the delimiter between the parameter name and value
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     * @throws IOException thrown in case of of an I/O related problem
     */
    public static Map<String, String> parseParameters(Path parametersPath, String delimiter) throws IOException {
        return parseParameters(parametersPath, delimiter, false);
    }

    /**
     * Parse the given parameters String and add it to the given parameter map (key: parameters name; value: parameter
     * value).
     *
     * @param parameterString the given parameters String
     * @param parameters      the parameters map
     * @param delimiter       the delimiter between the parameter name and value
     * @param toLowerCase     whether or not to convert the parameter names to lowercase
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     */
    public static void parseParameter(String parameterString, Map<String, String> parameters, String delimiter, boolean toLowerCase) {
        String[] split = parameterString.split(delimiter);

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

    /**
     * Parse the given parameters String and add it to the given parameter map (key: parameters name; value: parameter
     * value).
     *
     * @param parameterString the given parameters String
     * @param parameters      the parameters map
     * @param delimiter       the delimiter between the parameter name and value
     * @return the map of parsed parameters (key: the parameter name; value: the parameter value)
     */
    public static void parseParameter(String parameterString, Map<String, String> parameters, String delimiter) {
        parseParameter(parameterString, parameters, delimiter, false);
    }

}
