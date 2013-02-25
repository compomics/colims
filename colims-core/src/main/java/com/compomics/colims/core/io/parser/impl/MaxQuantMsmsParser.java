package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MaxQuantMsmsParser {
    public void parse(final File msmsFile) throws IOException {
        // Convert file into some values we can loop over, without reading file in at once
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile);

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // TODO
        }
    }
}

/**
 * Refer to headers in MaxQuant msms.txt output files by enum values, as headers are likely to change with each new
 * version of MaxQuant. Their order is also likely to change between files regardless of MaxQuant version.
 */
enum MsmsHeaders {
    id("id"),
    Protein_Group_IDs("Protein Group IDs"),
    Peptide_ID("Peptide ID"),
    Mod_Peptide_ID("Mod. Peptide ID"),
    Evidence_ID("Evidence ID"),
    Oxidation_M_Site_IDs("Oxidation (M) Site IDs"),
    Raw_File("Raw File"),
    Scan_Type("Scan Type"),
    Scan_Number("Scan Number"),
    Precursor("Precursor"),
    Sequence("Sequence"),
    Length("Length"),
    Missed_Cleavages("Missed Cleavages"),
    Modifications("Modifications"),
    Modified_Sequence("Modified Sequence"),
    Oxidation_M_Probabilities("Oxidation (M) Probabilities"),
    Oxidation_M_Score_Diffs("Oxidation (M) Score Diffs"),
    Acetyl_Protein_N_term("Acetyl (Protein N-term)"),
    Oxidation_M("Oxidation (M)"),
    Proteins("Proteins"),
    Gene_Names("Gene Names"),
    Protein_Names("Protein Names"),
    Uniprot("Uniprot"),
    Charge("Charge"),
    Fragmentation("Fragmentation"),
    Mass_analyzer("Mass analyzer"),
    Type("Type"),
    Scan_event_number("Scan event number"),
    Isotope_Index("Isotope Index"),
    m_z("m/z"),
    Mass("Mass"),
    Mass_Error_ppm("Mass Error [ppm]"),
    Simple_Mass_Error_ppm("Simple Mass Error [ppm]"),
    Retention_Time("Retention Time"),
    PEP("PEP"),
    Score("Score"),
    Delta_score("Delta score"),
    Combinatorics("Combinatorics"),
    PIF("PIF"),
    Fraction_of_total_spectrum("Fraction of total spectrum"),
    Base_peak_fraction("Base peak fraction"),
    Precursor_Full_ScanNumber("Precursor Full ScanNumber"),
    Precursor_Intensity("Precursor Intensity"),
    Precursor_Apex_Fraction("Precursor Apex Fraction"),
    Precursor_Apex_Offset("Precursor Apex Offset"),
    Matches("Matches"),
    Intensities("Intensities"),
    Mass_Deviations_Da("Mass Deviations [Da]"),
    Mass_Deviations_ppm("Mass Deviations [ppm]"),
    Masses("Masses"),
    Number_of_Matches("Number of Matches"),
    Intensity_coverage("Intensity coverage"),
    Peak_coverage("Peak coverage"),
    Neutral_loss_level("Neutral loss level"),
    Reverse("Reverse");

    /**
     * The name of the field in the evidence.txt MaxQuant output file
     */
    String column;

    private MsmsHeaders(final String fieldname) {
        column = fieldname;
    }
}
