package com.compomics.colims.repository.hibernate.model;

/**
 * Created by Niels Hulstaert on 14/10/15.
 */
public class ProteinGroupForRun {

    private Long id;
    private Double proteinPostErrorProbability;
    private String mainAccession;
    private String mainSequence;
    private long distinctPeptideCount;
    private long spectrumCount;

    public ProteinGroupForRun() {
    }


}
