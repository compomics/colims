package com.compomics.colims.core.io.mzml;

import com.compomics.colims.core.io.MappingException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import com.compomics.colims.model.Experiment;

/**
 *
 * @author Niels Hulstaert
 */
public interface MzMLParser {

    /**
     * Imports the mzML files and stores them in a Map (key: file name, value:
     * mzMLUnmarshaller). This method does't parse the files.
     *
     * @param mzMLfiles the mzML files
     */
    void importMzMLFiles(List<File> mzMLFiles);

    /**
     * Parses the experiment from the given mzML file.
     *
     * @param mzMLFileName the mzML file name
     * @return the experiment
     */
    Experiment parseMzmlFile(String mzMLFileName) throws MzMLUnmarshallerException, IOException, MappingException;
        
}
