package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.SearchEngine;

public class MaxQuantEvidenceParser {
    public void parse(final File evidenceFile) throws IOException {
        // Convert file into some values we can loop over, without reading file in at once
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(evidenceFile);

        // TODO Probably retrieve search engine from database instead
        SearchEngine engine = new SearchEngine();

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create a peptide for this line
            Peptide peptide = extractPeptide(values);
            // TODO Persist the peptide

            // Extract the protein accessioncodes involved
            List<String> proteinAccessioncodes = extractProteinAccessioncodes(values);
            System.out.println(proteinAccessioncodes);
            // TODO Locate the corresponding proteins and link the peptide to those proteins

        }
    }

    /**
     * Create a new peptide instance from the values contained in the map.
     * 
     * @param values
     * @return
     */
    static Peptide extractPeptide(final Map<String, String> values) {
        // Extract relevant fields
        String sequence = values.get(EvidenceHeaders.Sequence.column);
        String theoreticalMassStr = values.get(EvidenceHeaders.Mass.column);
        Double theoreticalMass = Double.valueOf(theoreticalMassStr);

        // Create peptide
        Peptide peptide = new Peptide();
        peptide.setSequence(sequence);
        peptide.setTheoreticalMass(theoreticalMass);
        return peptide;
    }

    /**
     * From the {@link EvidenceHeaders#Proteins} column extract all the accession codes we can find using a collection
     * of regular expressions.
     * 
     * @param values
     * @return list of accession codes found in the {@link EvidenceHeaders#Proteins} column
     */
    static List<String> extractProteinAccessioncodes(final Map<String, String> values) {
        // Accession codes can be parsed from the lines stored in the Proteins column
        String entireLine = values.get(EvidenceHeaders.Proteins.column);

        // Unfortunately, there are different formats in use for the accession codes, handle each case we find here

        List<String> regexes = getProteinAccessioncodeMatchingRegexes();

        // Add all occurrences of any matches found in entireLine for any of the patterns
        List<String> accessionCodes = new ArrayList<>();
        for (String regex : regexes) {
            Pattern compile = Pattern.compile(regex);
            Matcher matcher = compile.matcher(entireLine);
            while (matcher.find())
                accessionCodes.add(matcher.group(1));
        }

        // If we're still missing accession codes, we probably should adjust or add our patterns
        assert !accessionCodes.isEmpty() : entireLine;
        return accessionCodes;
    }

    static List<String> getProteinAccessioncodeMatchingRegexes() {
        List<String> regexes = new ArrayList<>();
        // gi|153807749|ref|ZP_01960417.1| hypothetical protein BACCAC_02032 [Bacteroides caccae ATCC 43185];
        regexes.add("gi\\|\\d+\\|ref\\|(.+?)\\|");
        // IPI:IPI00257882.7|SWISS-PROT:P12955|TREMBL:A8K3Z1;A8K416;A8K696;A8MX47|ENSEMBL:ENSP00000244137|REFSEQ:...
        regexes.add("IPI:(.+?)\\|");
        return regexes;
    }
}

/**
 * Refer to headers in MaxQuant evidence.txt output files by enum values, as headers are likely to change with each new
 * version of MaxQuant. Their order is also likely to change between files regardless of MaxQuant version.
 */
enum EvidenceHeaders {
    id("id"),
    Protein_Group_IDs("Protein Group IDs"),
    Peptide_ID("Peptide ID"),
    Mod_Peptide_ID("Mod. Peptide ID"),
    MS_MS_IDs("MS/MS IDs"),
    AIF_MS_MS_IDs("AIF MS/MS IDs"),
    Oxidation_M_Site_IDs("Oxidation (M) Site IDs"),
    Sequence("Sequence"),
    Length("Length"),
    Modifications("Modifications"),
    Modified_Sequence("Modified Sequence"),
    Oxidation_M_Probabilities("Oxidation (M) Probabilities"),
    Oxidation_M_Score_Diffs("Oxidation (M) Score Diffs"),
    Acetyl_Protein_N_term("Acetyl (Protein N-term)"),
    Oxidation_M("Oxidation (M)"),
    Proteins("Proteins"),
    Leading_Proteins("Leading Proteins"),
    Leading_Razor_Protein("Leading Razor Protein"),
    Gene_Names("Gene Names"),
    Protein_Names("Protein Names"),
    Protein_Descriptions("Protein Descriptions"),
    Uniprot("Uniprot"),
    Type("Type"),
    Raw_File("Raw File"),
    Fraction("Fraction"),
    Experiment("Experiment"),
    Charge("Charge"),
    m_z("m/z"),
    Mass("Mass"),
    Resolution("Resolution"),
    Uncalibrated_Calibrated_m_z_ppm("Uncalibrated - Calibrated m/z [ppm]"),
    Mass_Error_ppm("Mass Error [ppm]"),
    Uncalibrated_Mass_Error_ppm("Uncalibrated Mass Error [ppm]"),
    Retention_Time("Retention Time"),
    Retention_Length("Retention Length"),
    Calibrated_Retention_Time("Calibrated Retention Time"),
    Retention_Time_Calibration("Retention Time Calibration"),
    Match_Time_Difference("Match Time Difference"),
    PIF("PIF"),
    Fraction_of_total_spectrum("Fraction of total spectrum"),
    Base_peak_fraction("Base peak fraction"),
    PEP("PEP"),
    MS_MS_Count("MS/MS Count"),
    MS_MS_Scan_Number("MS/MS Scan Number"),
    Score("Score"),
    Delta_score("Delta score"),
    Combinatorics("Combinatorics"),
    Intensity("Intensity"),
    Reverse("Reverse"),
    Contaminant("Contaminant");

    /**
     * The name of the field in the evidence.txt MaxQuant output file
     */
    String column;

    private EvidenceHeaders(final String fieldname) {
        column = fieldname;
    }
}
