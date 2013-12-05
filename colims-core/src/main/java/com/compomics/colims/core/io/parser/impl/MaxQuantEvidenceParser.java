package com.compomics.colims.core.io.parser.impl;

import com.compomics.colims.model.Modification;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//import com.compomics.colims.model.Modification;
//import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.biology.Peptide;
//import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.PeptideHasProtein;
//import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.biology.Protein;
import com.compomics.colims.model.QuantificationGroup;
//import com.compomics.colims.model.QuantificationGroupHasPeptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.quantification.Ratio;
import com.compomics.util.experiment.quantification.matches.PeptideQuantification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.log4j.Logger;
//import com.compomics.util.protein.Header.DatabaseType;

/**
 * Parser for MaxQuant evidence.txt output files, that creates {@link Peptide}s,
 * {@link Protein}s and {@link Modification}s and links them with eachother and
 * a argument {@link QuantificationGroup}. <br>
 * <br>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse
 * the files into Map<String,String>
 * records. The {@link ProteinAccessioncodeParser} is used to retrieve the
 * correct accessioncode to use from a String.
 */
public class MaxQuantEvidenceParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantEvidenceParser.class);

    /**
     * Parse the evidenceFile and add the peptides, protein references and
     * modifications within to the quantificationGroup.
     *
     * @param evidenceFile
     * @param quantificationGroup
     * @throws IOException
     */
    public static Map<Integer, PeptideAssumption> parse(final File evidenceFile, final QuantificationGroup quantificationGroup) throws IOException {
        // Convert file into some values we can loop over, without reading file in at once
        Map<Integer, PeptideAssumption> parsedPeptideList = new HashMap<>();
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(evidenceFile);

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create a peptide for this line
            try {
                PeptideAssumption assumption = createPeptide(values);
                //linkPeptideToProtein(peptide, values);
                //linkPeptideToModifications(peptide, values);
                Integer bestMsMs;
                if (values.containsKey(EvidenceHeaders.best_MS_MS_ID.column)) {
                    bestMsMs = Integer.parseInt(values.get(EvidenceHeaders.best_MS_MS_ID.column));
                } else {
                    bestMsMs = Integer.parseInt(values.get(EvidenceHeaders.MS_MS_IDs.column).split(";")[0]);
                }

                parsedPeptideList.put(bestMsMs, assumption);
            } catch (UnparseableException upe) {
                //TODO decide what to do with this
                LOGGER.error(upe);
            }
            // QuantificationGroupHasPeptide
            // XXX "de peptiden die tot een quantificationgroup behoren zijn gelinkt door de peptide ID identifier"
            //QuantificationGroupHasPeptide quantificationGroupHasPeptide = new QuantificationGroupHasPeptide();
            //quantificationGroupHasPeptide.setQuantificationGroup(quantificationGroup);
            //quantificationGroupHasPeptide.setPeptide(peptide);
            // TODO Store QuantificationGroupHasPeptide instances
        }
        return parsedPeptideList;
    }

    /**
     * Parse the evidenceFile and add the peptides, proteins and modifications
     *
     * @param evidenceFile
     * @param proteinGroupFile
     * @param quantificationGroup
     * @return
     * @throws IOException
     *
     * public final List<Peptide> parse(final File evidenceFile, final File
     * proteinGroupFile, final QuantificationGroup quantificationGroup) throws
     * IOException { List<Peptide> parsedPeptideList = new ArrayList<>();
     * TabularFileLineValuesIterator valuesIterator = new
     * TabularFileLineValuesIterator(evidenceFile);
     *
     * return parsedPeptideList; }
     */
    /**
     * Create a new peptide instance from the values contained in the map.
     *
     * @param values
     * @return a {@code PeptideAssumption} or the parsed row
     * @throws UnparseableException if something went wrong while parsing the
     * evidence row
     */
    public static PeptideAssumption createPeptide(final Map<String, String> values) throws UnparseableException {
        if (values.get(EvidenceHeaders.MS_MS_IDs.column).isEmpty()) {
            //can't have evidence without proof
            throw new UnparseableException("peptide does not have an MS/MS scan");
        } else {
            String sequence = values.get(EvidenceHeaders.Sequence.column);

            // The charge corrected mass of the precursor ion.
            //Double massCorrected = Double.valueOf(values.get(EvidenceHeaders.Mass.column));


            ArrayList<String> proteinIds = new ArrayList<>();

            if (values.containsKey(EvidenceHeaders.Protein_Group_IDs.column)) {
                proteinIds = new ArrayList(Arrays.asList(values.get(EvidenceHeaders.Protein_Group_IDs.column).split(";")));
            } else {
                //up for discussion
            }

            // Create peptide
            Peptide peptide = new Peptide(sequence, proteinIds, extractModifications(values));
            double score = -1;
            if (values.get(EvidenceHeaders.Score.column).equalsIgnoreCase("NAN")) {
                throw new UnparseableException("could not parse score for peptide");
            } else {
                score = Double.parseDouble(values.get(EvidenceHeaders.Score.column));
            }

            //because I can
            Charge identificationCharge = null;

            if (values.containsKey(EvidenceHeaders.Charge.column)) {
                identificationCharge = new Charge(Charge.PLUS, Integer.parseInt(values.get(EvidenceHeaders.Charge.column)));
            }

            //99 is missing value according to statistics --> Advocate.MAXQUANT does not exist for the moment(and probably never will)
            PeptideAssumption assumption = new PeptideAssumption(peptide, 1, 99, identificationCharge, score);
            return assumption;
        }
    }

    /**
     * Create a new PeptideQuant instance from the values contained in the map.
     *
     * @param values
     * @return
     */
    public static PeptideQuantification createPeptideQuantification(final Map<String, String> values) {
        //String peptideId
        String peptideID = values.get(EvidenceHeaders.Peptide_ID.column);
        // The id of the peptide quant
        int id = Integer.parseInt(values.get(EvidenceHeaders.id.column));
        // The normalized ratio
        double ratio = Double.parseDouble(values.get(EvidenceHeaders.Normalized_Ratio.column));
        //create utilities Ratio object
        Ratio utilitiesRatio = new Ratio(id, ratio);
        // Create peptidequantification
        PeptideQuantification pepQuant = new PeptideQuantification(peptideID);
        // add the ratio to the quant object
        pepQuant.addRatio(id, utilitiesRatio);
        return pepQuant;
    }

    /**
     * From values.get({@link EvidenceHeaders#Proteins}) extract {@link Protein}
     * accessioncodes, retrieve or create the Protein instance and link the two
     * using a {@link PeptideHasProtein} instance.
     *
     * @param peptide
     * @param values
     */
    //ArrayList<String> fetchProteinAccessionForPeptide(final Map<String, String> values) {
    // Accession codes can be parsed from the lines stored in the Proteins column
    //String entireLine = values.get(EvidenceHeaders.Proteins.column);
    // List<String> proteinAccessioncodes = ProteinAccessioncodeParser.extractProteinAccessioncodes(entireLine);
//TODO change peptide.setParentProteins in utilities to take a List
    //peptide.setParentProteins(proteinAccessioncodes);
    // Locate the first protein by accession and link the peptide to that protein through PeptideHasProtein
    //String firstAccession = proteinAccessioncodes.get(0);
    //Protein protein = proteinRepository.findByAccession(firstAccession);
    //if (protein == null) {
    //String sequence = values.get(EvidenceHeaders.Sequence.column);
    //DatabaseType databaseType = DatabaseType.Unknown;
    //Protein protein = new Protein(firstAccession, sequence, databaseType);
    // Protein protein = new Protein(firstAccession, sequence, false);
    // TODO Persist protein, probably, depending on the protein availability method decided on by Davy & users
    // ??? proteinRepository.save(protein);
    //}
    // Create a PeptideHasProtein instance to link protein and peptide
    //PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
    //peptideHasProtein.setProtein(protein);
    //peptideHasProtein.setPeptide(peptide);
    // Store the reference between each of the two in either instance
    //protein.getPeptideHasProteins().add(peptideHasProtein);
    //peptide.getPeptideHasProteins().add(peptideHasProtein);
    // TODO Persist PeptideHasProtein instance using a Hibernate Repository that still has to be created
    //}
    /**
     * From values extract a variety of {@link Modification}s and link them to
     * the {@link Peptide} instance using {@link PeptideHasProtein} as
     * appropriate.
     *
     * @param peptide
     * @param values
     */
    public static ArrayList<ModificationMatch> extractModifications(final Map<String, String> values) {
        ArrayList<ModificationMatch> modificationsForPeptide = new ArrayList<>();
        // Sequence representation including the post-translational
        // modifications (abbreviation of the modification in brackets
        // before the modified AA). The sequence is always surrounded
        // by underscore characters ('_').
        String modifications = values.get(EvidenceHeaders.Modifications.column);
        if ("Unmodified".equalsIgnoreCase(modifications)) {
            return modificationsForPeptide;
        }

        // Fields we need to create the PeptideHasModification
        final int location;
        final String modificationName;

        // Look for Oxidation (M) Probabilities
        String oxidationProbabilities = values.get(EvidenceHeaders.Oxidation_M_Probabilities.column);
        if (oxidationProbabilities != null && oxidationProbabilities.contains("(1)")) {
            // Find precise location
            location = oxidationProbabilities.indexOf("(1)") - 1;
            //in case of multiple modifications in one location, this wil break
            modificationName = EvidenceHeaders.Oxidation_M_Probabilities.column;
        } else // Look for Acetyl (Protein N-term)
        if ("1".equals(values.get(EvidenceHeaders.Acetyl_Protein_N_term.column))) {
            // N-term has position 0
            location = 0;
            modificationName = EvidenceHeaders.Acetyl_Protein_N_term.column;
        } else {
            // Unexpected value: throw an exception
            String modifiedSequence = values.get(EvidenceHeaders.Modified_Sequence.column);
            String message = String.format("Unexpected, unhandled modification '%s' in sequence '%s'", modifications, modifiedSequence);
            throw new IllegalStateException(message);
        }
        //TODO parse parameters for fixed and variable modifications
        modificationsForPeptide.add(new ModificationMatch(modificationName, true, location));
        // Retrieve modification from database, or create a new one
        //Modification modification = modificationRepository.findByName(modificationName);
        // if (modification == null) {
        // modification = new Modification(modificationName);
        // TODO figure out where to get values for: monoIsotopicMass, averageMass
        // Persist modification
        // modificationRepository.save(modification);
        //}

        // Create PeptideHasModification
        //PeptideHasModification peptideHasModification = new PeptideHasModification();
        //peptideHasModification.setLocation(location);
        // peptideHasModification.setModification(modification);
        //peptideHasModification.setPeptide(peptide);
        // TODO Persist the PeptideHasModification instance using a new to create Hibernate Repository
        // Store the PeptideHasModification in both peptide and modification
        //peptide.getPeptideHasModifications().add(peptideHasModification);
        // modification.getPeptideHasModifications().add(peptideHasModification);
        //
        return modificationsForPeptide;
    }
}

/**
 * Refer to headers in MaxQuant evidence.txt output files by enum values, as
 * headers are likely to change with each new version of MaxQuant. Their order
 * is also likely to change between files regardless of MaxQuant version.
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
    Contaminant("Contaminant"),
    Normalized_Ratio("Ratio H/L normalized"),
    best_MS_MS_ID("Best MS/MS");
    /**
     * The name of the field in the evidence.txt MaxQuant output file
     */
    protected String column;

    private EvidenceHeaders(final String fieldname) {
        column = fieldname;
    }
}
