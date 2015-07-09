package com.compomics.colims.client.model;

/**
 * Created by Iain on 08/07/2015.
 */
public class PeptideTableRow {
    private String sequence;
    private long spectrumCount;
    private int charge;

    public PeptideTableRow(Object[] data) {
        sequence = (String) data[0];
        spectrumCount = (long) data[1];
        charge = (int) data[2];
    }

    public String getSequence() {
        return sequence;
    }

    public long getSpectrumCount() {
        return spectrumCount;
    }

    public int getCharge() {
        return charge;
    }
}
