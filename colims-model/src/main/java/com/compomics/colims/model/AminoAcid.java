package com.compomics.colims.model;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * This enum represents all the recognised amino acids. Note: this includes
 * common place holders like X for unknown AA B for Asn or Asp J for Leu or Ile
 * Z for Glu or Gln
 *
 * (reference: http://www.matrixscience.com/help/aa_help.html)
 *
 * @author Florian Reisinger Date: 30-Jul-2009
 * @since 0.1
 */
public enum AminoAcid {

    Ala("Alanine", 'A', 71.037114D),
    Arg("Arginine", 'R', 156.101111D),
    Asn("Asparagine", 'N', 114.042927D),
    Asp("Aspartic_Acid", 'D', 115.026943D),
    Cys("Cysteine", 'C', 103.009185D),
    Glu("Glutamic_Acid", 'E', 129.042593D),
    Gln("Glutamine", 'Q', 128.058578D),
    Gly("Glycine", 'G', 57.021464D),
    His("Histidine", 'H', 137.058912D),
    Ile("Isoleucine", 'I', 113.084064D),
    Xle("Isoleucine_or_Leucine", 'J', 113.084064D),
    Leu("Leucine", 'L', 113.084064D),
    Lys("Lysine", 'K', 128.094963D),
    Met("Methionine", 'M', 131.040485D),
    Phe("Phenylalanine", 'F', 147.068414D),
    Pro("Proline", 'P', 97.052764D),
    Ser("Serine", 'S', 87.032028D),
    Thr("Threonine", 'T', 101.047679D),
    SeC("Selenocysteine", 'U', 150.95363D),
    Trp("Tryptophan", 'W', 186.079313D),
    Tyr("Tyrosine", 'Y', 163.06332D),
    Val("Valine", 'V', 99.068414D),
    Pyl("Pyrrolysine", '0', 237.147727D),
    Asx("Asparagine_or_Aspartic_Acid", 'B', 0D),
    Glx("Glutamic_Acid_or_Glutamine", 'Z', 0D),
    Xaa("Unknown amino acid", 'X', 0D);
    
    /**
     * The amino acid name
     */
    private final String name;
    /**
     * The amino acid 1-letter code
     */
    private final char letter;
    /**
     * The amino acid mass
     */
    private final double mass;
    /**
     * The letters map (key: the amino acid 1-letter code, value: the amino acid)
     */
    private static final HashMap<Character, AminoAcid> letters = new HashMap<>();
    /**
     * The names map (key: the amino acid 1-letter code, value: the amino acid)
     */
    private static final HashMap<String, AminoAcid> names = new HashMap<>();

    static {
        for (AminoAcid aa : EnumSet.allOf(AminoAcid.class)) {
            //make sure we only use upper case in the maps
            letters.put(Character.toUpperCase(aa.letter()), aa);
            names.put(aa.fullName().toUpperCase(), aa);
        }
    }

    AminoAcid(String name, char letter, double mass) {
        this.name = name;
        this.letter = letter;
        this.mass = mass;
    }

    public String fullName() {
        return name;
    }

    public char letter() {
        return letter;
    }

    public double mass() {
        return mass;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Get the AA by the 1 letter code as a char.
     * 
     * @param c the 1 letter AA code char
     * @return the AA
     */
    public static AminoAcid getAA(char c) {
        //in the maps we only use upper case
        return letters.get(Character.toUpperCase(c));
    }

    /**
     * Get the AA by the 1 letter code as a string.
     * 
     * @param letter the 1 letter AA code string
     * @return the AA
     */
    public static AminoAcid getAA(String letter) {
        return letters.get(getCharForString(letter));
    }

    /**
     * Check if an AA with the given code is found.
     * 
     * @param c the AA code char
     * @return true if the AA is found
     */
    public static boolean containsAA(char c) {
        //in the maps we only use upper case
        return letters.containsKey(Character.toUpperCase(c));
    }

    /**
     * Check if an AA with the given code is found.
     * 
     * @param letter the AA code String
     * @return true if the AA is found
     */
    public static boolean containsAA(String letter) {
        //in the maps we only use upper case
        return letters.containsKey(getCharForString(letter));
    }

    /**
     * Get the AA by name.
     * 
     * @param s the AA name
     * @return the AA
     */
    public static AminoAcid getAAForName(String s) {
        //in the maps we only use upper case
        return names.get(s.toUpperCase());
    }

    /**
     * Check if the AA is found by name.
     * 
     * @param s the AA name
     * @return true if the AA is found
     */
    public static boolean containsAAByName(String s) {
        //in the maps we only use upper case
        return names.containsKey(s.toUpperCase());
    }

    private static char getCharForString(String letter) {
        if (letter.length() != 1) {
            throw new IllegalArgumentException("Allowed values must have length 1! "
                    + "The provided letter did not match this criterium: " + letter);
        }
        return letter.toUpperCase().toCharArray()[0];
    }
}