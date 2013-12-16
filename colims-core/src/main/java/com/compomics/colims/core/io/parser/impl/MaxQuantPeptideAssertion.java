package com.compomics.colims.core.io.parser.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Davy
 */
public class MaxQuantPeptideAssertion {

    private List<String> peptideAssumptionKeysOfBackingEvidenceForGuess = new ArrayList<>();

    public void addEvidence(String peptideAssumptionKey) {
        peptideAssumptionKeysOfBackingEvidenceForGuess.add(peptideAssumptionKey);
    }

    public List<String> getPeptideAssumptionKeys() {
        return Collections.unmodifiableList(peptideAssumptionKeysOfBackingEvidenceForGuess);
    }
}
