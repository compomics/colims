package com.compomics.colims.core.logic;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.UnknownAAException;

/**
 * This class calculates ion ladder masses.
 *
 * @author Niels Hulstaert
 */
public interface IonLadderMassesCalculator {

    /**
     * Calculate the B ion ladder masses.
     *
     * @param peptide the peptide
     * @param charge the precursor charge
     * @return the array of ladder masses
     * @throws UnknownAAException exception thrown in case of an unknown AA
     */
    double[] calculateBIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException;

    /**
     * Calculate the Y ion ladder masses.
     *
     * @param peptide the peptde
     * @param charge the precursor charge
     * @return the array of ladder masses
     * @throws UnknownAAException exception thrown in case of an unknown AA
     */
    double[] calculateYIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException;
}
