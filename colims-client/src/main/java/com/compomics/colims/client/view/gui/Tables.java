/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view.gui;

/**
 *
 * @author Kenneth
 */
public enum Tables {

    PROJECT, EXPERIMENT, SAMPLE, RUNS, SPECTRUMMATCHES;

    public Tables getNext() {
        return this.ordinal() < Tables.values().length - 1
                ? Tables.values()[this.ordinal() + 1]
                : null;
    }
}
