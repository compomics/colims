package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantHeader;
import com.google.common.io.LineReader;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Convert a tabular file into an {@link Iterable} that returns {@link Map}<String, String> instances per line that use
 * the values on the first line as keys and the values per line as value. Using this approach one does not have to read
 * the entire file into memory first, providing some much needed relief when parsing large MaxQuant files.
 */
public class TabularFileLineValuesIterator2 implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(TabularFileLineValuesIterator2.class);

    private static final char DELIMITER = '\t';
    private FileReader fileReader;
    private LineReader lineReader;
    private String[] nextLine;
    /**
     * The header values array. All values are lowercase.
     */
    private String[] headerValues = new String[0];

    /**
     * Initialize an iterator for the data file. When iterating of the rows, the columns with the given headerValues are
     * being parsed and put in a map (key: the {@link MaxQuantHeader} instance; value: the column entry).
     *
     * @param tsvFile tab separated values file
     * @throws IOException
     */
    public TabularFileLineValuesIterator2(final File tsvFile) throws IOException {
        fileReader = new FileReader(tsvFile);
        lineReader = new LineReader(fileReader);

        String firstLine = lineReader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IOException("Input file " + tsvFile.getPath() + " is empty.");
        } else {
            headerValues = firstLine.toLowerCase(Locale.US).split("" + DELIMITER);

            advanceLine();
        }
    }

    /**
     * Initialize an iterator for the data file. When iterating of the rows, the columns with the given headerValues are
     * being parsed and put in a map (key: the {@link MaxQuantHeader} instance; value: the column entry).
     *
     * @param tsvFile the tab separated data file
     * @param maxQuantHeaders the list of headerValues
     * @throws IOException in case of an Input/Output related problem
     */
    public TabularFileLineValuesIterator2(final File tsvFile, List<MaxQuantHeader> maxQuantHeaders) throws IOException {
        fileReader = new FileReader(tsvFile);
        lineReader = new LineReader(fileReader);

        //read the first line
        String firstLine = lineReader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IOException("Input file " + tsvFile.getPath() + " is empty");
        } else {
            firstLine = firstLine.toLowerCase(Locale.US);
        }

        List<String> firstLineList = Arrays.asList(firstLine.split(String.valueOf(DELIMITER)));
        //check if each of the given header values is present in the file header
        for (MaxQuantHeader maxQuantHeader : maxQuantHeaders) {
            Optional<String> optionalHeader = maxQuantHeader.getValues()
                    .stream()
                    .filter(firstLineList::contains)
                    .findFirst();

            if (optionalHeader.isPresent()) {
                maxQuantHeader.setParsedValue(maxQuantHeader.getValues().indexOf(optionalHeader.get()));
            }
        }

        headerValues = firstLine.split(String.valueOf(DELIMITER));

        advanceLine();
    }

    /**
     * Get the header of the file to parse. Note that the header values are lowercase.
     *
     * @return the headerValues identified for the currently parsed file
     */
    public String[] getHeaderValues() {
        return headerValues.clone();
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
        Map<String, String> lineValues = new HashMap<>();

        for (int i = 0; i < nextLine.length; i++) {
            lineValues.put(headerValues[i], nextLine[i]);
        }

        //advance to the next line for the next invocation
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

    /**
     * Parse the next line and split its values by the configured delimiter. Also handles the end of file by setting
     * nextLine to null.
     */
    private void advanceLine() {
        try {
            String readLine = lineReader.readLine();
            if (readLine != null) {
                nextLine = readLine.split(String.valueOf(DELIMITER));
                return;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        nextLine = null;
    }
}
