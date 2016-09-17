package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantHeader;
import com.google.common.io.LineReader;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Convert a tabular file into an {@link Iterable} that returns {@link Map}<String, String> instances per line that use
 * the values on the first line as keys and the values per line as value. Using this approach one does not have to read
 * the entire file into memory first, providing some much needed relief when parsing large MaxQuant files.
 */
public class TabularFileIterator implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(TabularFileIterator.class);

    private static final char DELIMITER = '\t';

    private BufferedReader bufferedReader;
    private LineReader lineReader;
    private String[] nextLine;
    /**
     * The header values array. All values are lowercase.
     */
    private String[] headerValues = new String[0];

    /**
     * Initialize an iterator for the data file. When iterating of the rows, the columns with the given headerValues are
     * being parsed and put in a map (key: the {@link MaxQuantHeader} instance; value: the column entry). The method
     * throws an {@link IllegalArgumentException} if a header is not present in the given file.
     *
     * @param tsvFile         the tab separated data file
     * @param maxQuantHeaders the list of headers that have to be present
     * @throws IOException              in case of an Input/Output related problem
     * @throws IllegalArgumentException in case on of the given headers is not present
     */
    public TabularFileIterator(final Path tsvFile, List<MaxQuantHeader> maxQuantHeaders) throws IOException {
        bufferedReader = Files.newBufferedReader(tsvFile);
        lineReader = new LineReader(bufferedReader);

        //read the first line
        String firstLine = lineReader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IOException("Input file " + tsvFile.getFileName() + " is empty.");
        } else {
            firstLine = firstLine.toLowerCase(Locale.US);
        }

        List<String> firstLineList = Arrays.asList(firstLine.split(String.valueOf(DELIMITER)));
        //check if each of the given header values is present in the file header
        for (MaxQuantHeader maxQuantHeader : maxQuantHeaders) {
            Optional<String> header = maxQuantHeader.getValues()
                    .stream()
                    .filter(firstLineList::contains)
                    .findFirst();

            if (header.isPresent()) {
                maxQuantHeader.setParsedValue(maxQuantHeader.getValues().indexOf(header.get()));
            } else {
                throw new IllegalArgumentException("The mandatory header " + maxQuantHeader.getName() + " is not present in the given file " + tsvFile.getFileName());
            }
        }

        headerValues = firstLine.split(String.valueOf(DELIMITER));

        advanceLine();
    }

    @Override
    public boolean hasNext() {
        if (nextLine == null) {
            try {
                bufferedReader.close();
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
                //the -1 argument is for not discarding trailing empty fields
                nextLine = readLine.split(String.valueOf(DELIMITER), -1);
                return;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        nextLine = null;
    }
}
