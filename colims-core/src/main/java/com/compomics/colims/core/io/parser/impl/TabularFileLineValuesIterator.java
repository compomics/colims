package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.io.LineReader;

/**
 * Convert a tabular file into an {@link Iterable} that returns {@link Map}<String, String> instances per line that use
 * the values on the first line as keys.
 */
class TabularFileLineValuesIterator implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {
    static final char delimiter = '\t';

    FileReader fileReader = null;
    LineReader lineReader = null;
    String[] nextLine = null;
    private String[] headers = new String[0];

    public TabularFileLineValuesIterator(final File evidenceFile) throws IOException {
        // Extract headers
        fileReader = new FileReader(evidenceFile);
        lineReader = new LineReader(fileReader);
        String readLine = lineReader.readLine();

        // Determine the headers for this particular file, so we can assign values to the right key in our map
        headers = readLine.split("" + delimiter);

        // Initialize the first line in the nextLine field
        advanceLine();
    }

    String[] getHeaders() {
        return headers.clone();
    }

    void advanceLine() {
        // Advance to the next line
        try {
            String readLine = lineReader.readLine();
            if (readLine != null) {
                nextLine = readLine.split("" + delimiter);
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        nextLine = null;
    }

    @Override
    public boolean hasNext() {
        if (nextLine == null)
            try {
                fileReader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        return nextLine != null;
    }

    @Override
    public Map<String, String> next() {
        // nextLine[] is an array of values from the line
        Map<String, String> lineValues = new HashMap<>();
        for (int i = 0; i < nextLine.length; i++)
            lineValues.put(headers[i], nextLine[i]);

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
        return this;
    }
}
