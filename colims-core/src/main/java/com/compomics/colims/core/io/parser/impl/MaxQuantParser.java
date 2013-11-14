package com.compomics.colims.core.io.parser.impl;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.QuantificationFile;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.QuantificationMethod;

/**
 * Parser for the text files generated by a <a
 * href="http://maxquant.org/">MaxQuant</a>run. Invokes sub-parsers such as
 * {@link MaxQuantEvidenceParser} and {@link MaxQuantMsmsParser} to handle
 * specific files contained in the text folder.
 */
@Service("maxQuantParser")
public class MaxQuantParser {

    private static final String MSMSTXT = "msms.txt";
    private static final String EVIDENCETXT = "evidence.txt";
    
    @Autowired
    MaxQuantEvidenceParser evidenceParser;
    @Autowired
    MaxQuantMsmsParser msmsParser;

    /**
     * Parse a folder containing the MaxQuant txt output files, using the
     * correct sub-parser for each known file type.
     *
     * @param maxQuantTextFolder
     * @throws IOException
     */
    @Transactional
    public void parseMaxQuantTextFolder(final Path maxQuantTextFolder) throws IOException {
        // Create a single QuantificationFile file to the argument folder, and store it in the database
        QuantificationFile quantificationFile = new QuantificationFile();
        // TODO Probably store the folder name in quantificationFile, but there are no such fields available
        // Associate the correct QuantificationMethod with this QuantificationFile
        // TODO Actually retrieve or create and store quantificationMethod, but it currently lacks a field for names
        QuantificationMethod quantificationMethod = null;
        quantificationFile.setQuantificationMethod(quantificationMethod);
        // TODO Store quantificationFile; we are currently missing a Hibernate Repository to do so, so skip for now

        // Create a new QuantificationGroup to group all the Quantifications found and link it to the QuantificationFile
        QuantificationGroup quantificationGroup = new QuantificationGroup();
        quantificationGroup.setQuantificationFile(quantificationFile);
        // TODO Store quantificationGroup; we are currently missing a Hibernate Repository to do so, so skip for now

        // Parse evidence.txt and create and persist the objects found within
        Path evidenceFile = maxQuantTextFolder.resolve(EVIDENCETXT);
        evidenceParser.parse(evidenceFile.toFile(), quantificationGroup);

        // Parse msms.txt and create and persist the objects found within
        Path msmsFile = maxQuantTextFolder.resolve(MSMSTXT);
        msmsParser.parse(msmsFile.toFile(), false);

        // Protein ??
        // Modification ??
    }
}