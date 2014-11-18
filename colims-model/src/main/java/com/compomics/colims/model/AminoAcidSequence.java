package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Florian Reisinger Date: 15-Jan-2008
 * @since 0.1
 */
public class AminoAcidSequence {

    /**
     * The list of amino acids
     */
    private final List<AminoAcid> aaSequence;
    /**
     * The list of AAs with unknown masses
     */
    private final List<AminoAcid> unknownMassAAs;
    private final String sequenceString;
    private double sequenceMass;
    private boolean allMassesKnown = true;

    /**
     * Creates an AminoAcidSequence from the provided String. Note: the
     * internally kept List<AminoAcid> is unmodifiable, therefore once created
     * the sequence can not be modified.
     *
     * @param sequence the amino acid sequence as string.
     * @throws UnknownAAException in case the provided amino acid string
     * contains a character that is not recognized as an amino acid.
     * @see AminoAcid
     */
    public AminoAcidSequence(String sequence) throws UnknownAAException {
        this(toAASequence(sequence));
    }

    /**
     * Creates an AminoAcidSequence from the provided List of AminoAcids. Note:
     * the internally kept List<AminoAcid> is unmodifiable, therefore once
     * created the sequence can not be modified.
     *
     * @param aaSequence the list of AminoAcids to generate the
     * AminoAcidSequence from.
     * @see AminoAcid
     */
    public AminoAcidSequence(List<AminoAcid> aaSequence) {
        //set the AminoAcid sequence
        this.aaSequence = Collections.unmodifiableList(aaSequence);
        //calculate the sequence mass and check for AAs with unknown mass
        List<AminoAcid> tmp = null;
        for (AminoAcid aa : aaSequence) {
            //note that the calculated mass might not be correct!
            //however if that is the case an exception will be thrown
            //when trying to retrieve the mass of the whole sequence
            this.sequenceMass += aa.mass();
            if (aa.mass() == 0D) {
                allMassesKnown = false;
                if (tmp == null) {
                    tmp = new ArrayList<>();
                }
                tmp.add(aa);
            }
        }
        if (tmp != null && !tmp.isEmpty()) {
            unknownMassAAs = Collections.unmodifiableList(tmp);
        } else {
            unknownMassAAs = null;
        }
        // generate the sequence string
        this.sequenceString = aaSequenceAsString(aaSequence);
    }

    /**
     * Gets the amino acid sequence as a string
     *
     * @param aaSequence the amino acid sequence
     * @return the amino acid sequence string
     */
    public static String aaSequenceAsString(List<AminoAcid> aaSequence) {
        StringBuilder sb = new StringBuilder();
        for (AminoAcid aa : aaSequence) {
            sb.append(aa.letter());
        }
        return sb.toString();
    }

    /**
     * Converts the amino acid sequence string to a amino acid sequence
     *
     * @param sequence the amino acid sequence string
     * @return the amino acid sequence
     * @throws UnknownAAException
     */
    public static List<AminoAcid> toAASequence(String sequence) throws UnknownAAException {
        List<AminoAcid> aaSequence = new ArrayList<>();
        for (char c : sequence.toCharArray()) {
            if (AminoAcid.containsAA(c)) {
                aaSequence.add(AminoAcid.getAA(c));
            } else {
                if (c == '*') {
                    continue;
                }
                throw new UnknownAAException("Not recognized amino acid: " + c);
            }
        }
        return aaSequence;
    }

    /**
     * Gets the amino acid sub sequence
     *
     * @param start start position of the sub-sequence.
     * @param end end position of the sub-sequence.
     * @return the derived sub-AminoAcidSequence.
     * @see String#subSequence(int, int)
     */
    public AminoAcidSequence subSequence(int start, int end) {
        AminoAcidSequence aas;
        try {
            aas = new AminoAcidSequence(sequenceString.substring(start, end));
        } catch (UnknownAAException e) {
            throw new IllegalStateException("SEVERE: Found unrecognised amino acid in AminoAcidSequence! "
                    + "This should never happen, since the internal sequenceString should have been checked "
                    + "during creation of the AminoAcidSequence object");
        }
        return aas;
    }

    /**
     * Returns the AminoAcid on the specified location of the AminoAcidSequence.
     * (Note: the index is 0 based)
     *
     * @param index the index of the amino acid to retrieve.
     * @return the AminoAcid at the specified location.
     * @throws IndexOutOfBoundsException if the specified location is outside
     * the sequence. E.g. the index is out of range (index < 0 || index >=
     * length())
     */
    public AminoAcid getAA(int index) {
        return aaSequence.get(index);
    }

    /**
     * @return the length of the amino acid sequence string.
     */
    public int length() {
        return sequenceString.length();
    }

    /**
     * Gets the number of unknown amino acid masses
     *
     * @return the number of unknown amino acid masses
     */
    public int getNumberOfUnknownAAMasses() {
        if (unknownMassAAs == null) {
            return 0;
        } // else
        return unknownMassAAs.size();
    }

    @Override
    public String toString() {
        return sequenceString;
    }

    /**
     * Gets the amino acid sequence mass
     *
     * @return the amino acid sequence mass
     * @throws AASequenceMassUnknownException
     */
    public double getSequenceMass() throws AASequenceMassUnknownException {
        if (!allMassesKnown) {
            throw new AASequenceMassUnknownException("Unknown mass in AA sequence: " + sequenceString + "!");
        }
        return sequenceMass;
    }

    /**
     * Gets the accumulated mass of the sequence part for which the mass is
     * known.
     *
     * @return the accumulated known mass
     */
    public double getMassOfKnownSequencePart() {
        return sequenceMass;
    }

    /**
     * Gets the amino acid sequence
     *
     * @return the amino acid sequence
     */
    public List<AminoAcid> getAASequence() {
        return aaSequence;
    }

    /**
     * Checks if all masses of the sequence are known
     *
     * @return true only if all amino acid masses are known
     */
    public boolean isAllMassesKnown() {
        return allMassesKnown;
    }

    /**
     * Gets the amino acids with unknown masses
     *
     * @return the amino acids with unknown masses
     */
    public List<AminoAcid> getUnknownMassAAs() {
        return unknownMassAAs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AminoAcidSequence other = (AminoAcidSequence) obj;
        return !((this.sequenceString == null) ? (other.sequenceString != null) : !this.sequenceString.equals(other.sequenceString));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.sequenceString != null ? this.sequenceString.hashCode() : 0);
        return hash;
    }
}