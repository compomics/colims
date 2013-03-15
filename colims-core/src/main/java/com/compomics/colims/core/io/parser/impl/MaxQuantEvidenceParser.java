package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ModificationRepository;
import com.compomics.colims.repository.ProteinRepository;
import com.compomics.util.protein.Header.DatabaseType;

@Service
public class MaxQuantEvidenceParser {
    MaxQuantEvidenceParser() {/*Do not allow just anyone to instantiate this class, we need access to beans!*/
    }

    @Autowired
    ProteinRepository proteinRepository;

    @Autowired
    ModificationRepository modificationRepository;

    public void parse(final File evidenceFile) throws IOException {
        // Convert file into some values we can loop over, without reading file in at once
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(evidenceFile);

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create a peptide for this line
            Peptide peptide = createPeptide(values);
            // TODO Persist the peptide

            linkPeptideToProtein(peptide, values);
            linkPeptideToModifications(peptide, values);
        }
    }

    /**
     * Create a new peptide instance from the values contained in the map.
     * 
     * @param values
     * @return
     */
    static Peptide createPeptide(final Map<String, String> values) {
        //The identified AA sequence of the peptide.
        String sequence = values.get(EvidenceHeaders.Sequence.column);

        //The charge corrected mass of the precursor ion.
        Double massCorrected = Double.valueOf(values.get(EvidenceHeaders.Mass.column));

        //Create peptide
        Peptide peptide = new Peptide();
        peptide.setTheoreticalMass(massCorrected);
        peptide.setSequence(sequence);
        return peptide;
    }

    void linkPeptideToProtein(final Peptide peptide, final Map<String, String> values) {
        // Accession codes can be parsed from the lines stored in the Proteins column
        String entireLine = values.get(EvidenceHeaders.Proteins.column);
        List<String> proteinAccessioncodes = ProteinAccessioncodeParser.extractProteinAccessioncodes(entireLine);

        // Locate the first protein by accession and link the peptide to that protein through PeptideHasProtein
        String firstAccession = proteinAccessioncodes.get(0);
        Protein protein = proteinRepository.findByAccession(firstAccession);
        if (protein == null) {
            String sequence = values.get(EvidenceHeaders.Sequence.column);
            DatabaseType databaseType = DatabaseType.Unknown;
            protein = new Protein(firstAccession, sequence, databaseType);
            //TODO Persist protein, probably, depending on the protein availability method decided on by Davy & users
        }

        // Create a PeptideHasProtein instance to link protein and peptide
        PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
        peptideHasProtein.setProtein(protein);
        peptideHasProtein.setPeptide(peptide);
        // Store the reference between each of the two in either instance
        protein.getPeptideHasProteins().add(peptideHasProtein);
        peptide.getPeptideHasProteins().add(peptideHasProtein);
    }

    void linkPeptideToModifications(final Peptide peptide, final Map<String, String> values) {
        //Sequence representation including the post-translational
        //modifications (abbreviation of the modification in brackets
        //before the modified AA). The sequence is always surrounded
        //by underscore characters ('_').
        String modifications = values.get(EvidenceHeaders.Modifications.column);
        if ("Unmodified".equalsIgnoreCase(modifications))
            return;

        //Fields we need to create the PeptideHasModification
        final int location;
        final String modificationName;

        //Look for Oxidation (M) Probabilities
        String oxidationProbabilities = values.get(EvidenceHeaders.Oxidation_M_Probabilities.column);
        if (oxidationProbabilities != null && oxidationProbabilities.contains("(1)")) {
            //Find precise location
            location = oxidationProbabilities.indexOf("(1)") - 1;
            modificationName = EvidenceHeaders.Oxidation_M_Probabilities.column;
        }
        else
            //Look for Acetyl (Protein N-term)
            if ("1".equals(values.get(EvidenceHeaders.Acetyl_Protein_N_term.column))) {
                //N-term has position 0
                location = 0;
                modificationName = EvidenceHeaders.Acetyl_Protein_N_term.column;
            } else {
                //Unexpected value: throw an exception
                String modifiedSequence = values.get(EvidenceHeaders.Modified_Sequence.column);
                String message = String.format("Unexpected, unhandled modification '%s' in sequence '%s'", modifications, modifiedSequence);
                throw new IllegalStateException(message);
            }

        //Retrieve modification from database, or create a new one
        Modification modification = modificationRepository.findByName(modificationName);
        if (modification == null) {
            modification = new Modification(modificationName);
            //TODO figure out where to get values for: monoIsotopicMass, averageMass
            //Persist modification 
            modificationRepository.save(modification);
        }

        //Create PeptideHasModification
        PeptideHasModification peptideHasModification = new PeptideHasModification();
        peptideHasModification.setLocation(location);
        peptideHasModification.setModification(modification);
        peptideHasModification.setPeptide(peptide);

        //Store the PeptideHasModification in both peptide and modification
        peptide.getPeptideHasModifications().add(peptideHasModification);
        modification.getPeptideHasModifications().add(peptideHasModification);
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
