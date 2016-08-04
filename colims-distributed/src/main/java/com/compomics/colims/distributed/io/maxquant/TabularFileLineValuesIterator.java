package com.compomics.colims.distributed.io.maxquant;

import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
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
public class TabularFileLineValuesIterator implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(TabularFileLineValuesIterator.class);

    private static final char DELIMITER = '\t';
    private FileReader fileReader = null;
    private LineReader lineReader = null;
    private String[] nextLine = null;
    /**
     * The header values array. All values are lowercase.
     */
    private String[] headers = new String[0];

    /**
     * Parse a TSV file and create a map for each line that maps the keys found on the first line to the values found to
     * the values found on lines two and further until the end of the file.
     *
     * @param tsvFile tab separated values file
     * @throws IOException
     */
    public TabularFileLineValuesIterator(final File tsvFile) throws IOException {
        fileReader = new FileReader(tsvFile);
        lineReader = new LineReader(fileReader);

        String firstLine = lineReader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            throw new IOException("Input file " + tsvFile.getPath() + " is empty.");
        } else {
            headers = firstLine.toLowerCase(Locale.US).split("" + DELIMITER);

            advanceLine();
        }
    }

    /**
     * Parse a TSV file and create a map of keys and values for the given enum of headers and each line in the file.
     *
     * @param tsvFile           The data file
     * @param headerEnumeration An enum of headers to specify values for
     * @throws IOException
     */
    public TabularFileLineValuesIterator(final File tsvFile, HeaderEnum[] headerEnumeration) throws IOException {
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
        //check if each of the given header enum values is present in the file header
        for (HeaderEnum headerEnum : headerEnumeration) {
            Optional<String> header = headerEnum.getPossibleValues()
                    .stream()
                    .filter(firstLineList::contains)
                    .findFirst();

            if (header.isPresent()) {
                headerEnum.setParsedValue(headerEnum.getPossibleValues().indexOf(header.get()));
            }
        }

        headers = firstLine.split(String.valueOf(DELIMITER));

        advanceLine();
    }

    /**
     * Get the header of the file to parse. Note that the header values are lowercase.
     *
     * @return the headers identified for the currently parsed file
     */
    public String[] getHeaders() {
        return headers.clone();
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
            lineValues.put(headers[i], nextLine[i]);
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
