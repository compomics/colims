package com.compomics.colims.core.io.maxquant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.google.common.io.LineReader;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * Convert a tabular file into an {@link Iterable} that returns
 * {@link Map}<String, String> instances per line that use the values on the
 * first line as keys and the values per line as value. Using this approach one
 * does not have to read the entire file into memory first, providing some much
 * needed relief when parsing large MaxQuant files.
 */
public class TabularFileLineValuesIterator implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {

    private static final Logger LOGGER = Logger.getLogger(TabularFileLineValuesIterator.class);

    private static final char DELIMITER = '\t';
    private FileReader fileReader = null;
    private LineReader lineReader = null;
    private String[] nextLine = null;
    private String[] headers = new String[0];

    /**
     * Parse the .tsv evidenceFile and return a Map<String,String> instance for
     * each line that maps the keys found on the first line to the values found
     * the the values found on lines two and further until the end of the file.
     *
     * @param evidenceFile tab separated values file
     * @throws IOException thrown in case of an input/output related error
     */
    public TabularFileLineValuesIterator(final File evidenceFile) throws IOException {
        // Extract headers
        fileReader = new FileReader(evidenceFile);
        lineReader = new LineReader(fileReader);
        String readLine = lineReader.readLine().toLowerCase(Locale.US);

        // Determine the headers for this particular file, so we can assign values to the right key in our map
        headers = readLine.split("" + DELIMITER);

        // Initialize the first line in the nextLine field
        advanceLine();
    }

    public TabularFileLineValuesIterator(final File evidenceFile, HeaderEnum[] headerEnumeration) throws IOException {
        // Extract headers
        fileReader = new FileReader(evidenceFile);
        lineReader = new LineReader(fileReader);
        String readLine = lineReader.readLine().toLowerCase(Locale.US);
        for (HeaderEnum aHeader : headerEnumeration) {
            for (int numberOfPossibleHeaders = 0; numberOfPossibleHeaders < aHeader.allPossibleColumnNames().length; numberOfPossibleHeaders++) {
                if (readLine.contains(aHeader.allPossibleColumnNames()[numberOfPossibleHeaders])) {
                    aHeader.setColumnReference(numberOfPossibleHeaders);
                }
            }
        }

        // Determine the headers for this particular file, so we can assign values to the right key in our map
        headers = readLine.split("" + DELIMITER);

        // Initialize the first line in the nextLine field
        advanceLine();
    }

    /**
     * @return the headers identified for the currently parsed file
     */
    String[] getHeaders() {
        return headers.clone();
    }

    /**
     * Parse the next line and split its values by the configured DELIMITER.
     * Also handles the end of file by setting nextLine to null.
     */
    void advanceLine() {
        // Advance to the next line
        try {
            String readLine = lineReader.readLine();
            if (readLine != null) {
                nextLine = readLine.split("" + DELIMITER);
                return;
            }
        } catch (IOException e) {
            // XXX Hmmm, just printing a stacktrace is a very poor way to handle an IOException at this point...
           LOGGER.error(e.getMessage(), e);
        }
        nextLine = null;
    }

    @Override
    public boolean hasNext() {
        if (nextLine == null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return nextLine != null;
    }

    @Override
    public Map<String, String> next() {
        // nextLine[] is an array of values from the line
        Map<String, String> lineValues = new HashMap<>();
        for (int i = 0; i < nextLine.length; i++) {
            lineValues.put(headers[i], nextLine[i]);
        }

        // Advance to the next line for the next invocation
        advanceLine();

        return lineValues;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This parser does not support removing lines from a file.");
    }

    @Override
    public Iterator<Map<String, String>> iterator() {
        // We want to be an iterable and an iterator at the same time
        return this;
    }
}
