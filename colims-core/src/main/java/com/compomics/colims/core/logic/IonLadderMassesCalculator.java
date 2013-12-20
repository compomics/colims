package com.compomics.colims.core.logic;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.UnknownAAException;

/**
 *
 * @author Niels Hulstaert
 */
public interface IonLadderMassesCalculator {
    
    /**
     * Calculate the B ion ladder masses
     * 
     * @param peptide the peptde
     * @param charge the precursor charge
     * @return
     * @throws UnknownAAException 
     */
    double[] calculateBIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException;
    
    /**
     * Calculate the Y ion ladder masses
     * 
     * @param peptide the peptde
     * @param charge the precursor charge
     * @return
     * @throws UnknownAAException 
     */
    double[] calculateYIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException;
}
