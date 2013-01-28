package com.compomics.colims.core.logic.impl;

import com.compomics.colims.core.logic.IonLadderMassesCalculator;
import com.compomics.colims.model.AASequenceMassUnknownException;
import com.compomics.colims.model.AminoAcidSequence;
import com.compomics.colims.model.Constants;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.UnknownAAException;
import org.apache.log4j.Logger;

/**
 *
 * @author Niels Hulstaert
 */
public class IonLadderMassesCalculatorImpl implements IonLadderMassesCalculator {

    private static final Logger LOGGER = Logger.getLogger(IonLadderMassesCalculatorImpl.class);

    @Override
    public double[] calculateBIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException {
        //B ion series N -> C
        //b1 = subSequence(0, 1)
        //b2 = subSequence(0, 2)
        // ...
        AminoAcidSequence aaSeq = new AminoAcidSequence(peptide.getSequence());
        int numberOfFragmentIons = peptide.getLength() - 1;
        double[] masses = new double[numberOfFragmentIons];
        for (int i = 0; i < numberOfFragmentIons; i++) {
            try {
                //calculate mass adjusted for the charge state (taking modifications into account)
                AminoAcidSequence fragSeq = aaSeq.subSequence(0, i + 1);
                //calculate the adjustment of the mass due to the charge state
                double adjust = (double) charge * Constants.MASS_H;
                double chargeAdjustedMass = (fragSeq.getSequenceMass() + adjust) / (double) charge;
                //adjust for termini (H and OH)
                //chargeAdjustedMass += Constants.MASS_H2O;
                masses[i] = chargeAdjustedMass;
            } catch (AASequenceMassUnknownException e) {
                LOGGER.warn("Mass of fragment ion could not be calculated! " + e.getMessage());
                masses[i] = 0d;
            }
        }
        return masses;
    }

    @Override
    public double[] calculateYIonLadderMasses(Peptide peptide, int charge) throws UnknownAAException {

        //Y ion series N <- C
        //y1 = subSequence(length-1, length)
        //y2 = subSequence(length-2, length)
        //...     
        AminoAcidSequence aaSeq = new AminoAcidSequence(peptide.getSequence());
        int numberOfFragmentIons = peptide.getLength() - 1;
        double[] masses = new double[numberOfFragmentIons];
        for (int i = 0; i < numberOfFragmentIons; i++) {
            try {
                //calculate mass adjusted for the charge state (taking modifications into account)
                AminoAcidSequence fragSeq = aaSeq.subSequence(numberOfFragmentIons - i, numberOfFragmentIons + 1);
                //calculate the adjustment of the mass due to the charge state
                double adjust = (double) charge * Constants.MASS_H;
                //add up fragment mass (uncharged + charge adjustment + termini adjustment) and divide by charge                
                double chargeAdjustedMass = (fragSeq.getSequenceMass() + adjust + Constants.MASS_H2O) / (double) charge;
                masses[i] = chargeAdjustedMass;
            } catch (AASequenceMassUnknownException e) {
                //ToDo: do we want to handle this like that?
                //ToDo: should raise a warning or flag...
                LOGGER.warn("Mass of fragment ion could not be calculated! " + e.getMessage());
                masses[i] = 0d;
            }
        }
        return masses;
    }
}
