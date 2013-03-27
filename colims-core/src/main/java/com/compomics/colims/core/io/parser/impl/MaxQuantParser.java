package com.compomics.colims.core.io.parser.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.QuantificationFile;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.QuantificationGroupHasPeptide;
import com.compomics.colims.model.QuantificationMethod;

@Service
public class MaxQuantParser {
    private static final String MSMSTXT = "msms.txt";
    private static final String EVIDENCETXT = "evidence.txt";

    @Autowired
    MaxQuantEvidenceParser evidenceParser;

    @Autowired
    MaxQuantMsmsParser msmsParser;

    @Transactional
    public void parseMaxQuantTextFolder(final Path maxQuantTextFolder) throws IOException {
        //QuantificationFile
        QuantificationFile quantificationFile = new QuantificationFile();//TODO Probably store the folder name in here
        QuantificationMethod quantificationMethod = null; //TODO Actually retrieve or create and store quantMethod 
        quantificationFile.setQuantificationMethod(quantificationMethod);
        //TODO Store quantificationFile

        //QuantificationGroup
        QuantificationGroup quantificationGroup = new QuantificationGroup();
        quantificationGroup.setQuantificationFile(quantificationFile);
        //TODO Store quantificationGroup

        //Parse evidence.txt
        Path evidenceFile = maxQuantTextFolder.resolve(EVIDENCETXT);
        evidenceParser.parse(evidenceFile.toFile());

        //Parse msms.txt
        Path msmsFile = maxQuantTextFolder.resolve(MSMSTXT);
        evidenceParser.parse(msmsFile.toFile());

        //Peptide
        Collection<Peptide> peptides = Collections.emptyList(); //TODO Actually retrieve peptides
        for (Peptide peptide : peptides) {
            //QuantificationGroupHasPeptide
            QuantificationGroupHasPeptide quantificationGroupHasPeptide = new QuantificationGroupHasPeptide();
            quantificationGroupHasPeptide.setQuantificationGroup(quantificationGroup);
            quantificationGroupHasPeptide.setPeptide(peptide);
            //TODO Store QuantificationGroupHasPeptide instances
        }

        //Protein

        //Modification

    }
}
