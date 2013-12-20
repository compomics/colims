package com.compomics.colims.core.io.parser.impl;

import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.model.Modification;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.util.experiment.biology.Protein;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.quantification.Ratio;
import com.compomics.util.experiment.quantification.matches.PeptideQuantification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
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
@Component("maxQuantEvidenceParser")
public class MaxQuantPSMParser {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantPSMParser.class);
    private List<String> modificationList = new ArrayList<>();

    /**
     * Parse the evidenceFile and add the peptides, protein references and
     * modifications within to the quantificationGroup.
     *
     * @param evidenceFile
     * @throws IOException
     * @return a Map with key the best msms id and value the peptideAssumption
     */
    public Map<Integer, PeptideAssumption> parse(final File evidenceFile) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        //temporary solution until switched to properties based handling of headers instead of enums
        List<String> modificationHeaderEnums = new ArrayList<>();
        modificationHeaderEnums.add(EvidenceHeaders.Oxidation_M.getColumnName());
        modificationHeaderEnums.add(EvidenceHeaders.Acetyl_Protein_N_term.getColumnName());
        modificationHeaderEnums.add(EvidenceHeaders.Acetyl_K.getColumnName());
        return parse(evidenceFile, modificationHeaderEnums);
    }

    /**
     * parses an evidence file with the given modifications
     *
     * @param evidenceFile the file to parse
     * @param modificationsPresent the modifications to check for
     * @return a {@code Map} key: the msms id and value the peptide assumption
     * @throws IOException if the evidence file could not be read
     * @throws HeaderEnumNotInitialisedException if a header was not initialised
     * properly and does not have a default value
     * @throws UnparseableException if vital information was missing from the
     * evidence file
     */
    public Map<Integer, PeptideAssumption> parse(File evidenceFile, List<String> modificationsPresent) throws IOException, HeaderEnumNotInitialisedException, UnparseableException {
        modificationList = modificationsPresent;
        // Convert file into some values we can loop over, without reading file in at once
        Map<Integer, PeptideAssumption> parsedPeptideMap = new HashMap<>();
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(evidenceFile, EvidenceHeaders.values());

        // Create and persist objects for all lines in file
        for (Map<String, String> values : valuesIterator) {
            // Create a peptide for this line
            PeptideAssumption assumption = createPeptide(values);
            //linkPeptideToProtein(peptide, values);
            //linkPeptideToModifications(peptide, values);
            if (values.containsKey(EvidenceHeaders.MS_MS_IDs.getColumnName())) {
                String[] msmsIds = values.get(EvidenceHeaders.MS_MS_IDs.getColumnName()).split(";");
                for (String msmsId : msmsIds) {
                    if (parsedPeptideMap.containsKey(Integer.parseInt(msmsId))) {
                        throw new UnparseableException("conflicts in the evidence file: multiple peptides for the same spectrum");
                    }
                    parsedPeptideMap.put(Integer.parseInt(msmsId), assumption);
                }
            } else {
                throw new UnparseableException("no spectrum found for an identified peptide");
            }
        }
        // QuantificationGroupHasPeptide
        // XXX "de peptiden die tot een quantificationgroup behoren zijn gelinkt door de peptide ID identifier"
        //QuantificationGroupHasPeptide quantificationGroupHasPeptide = new QuantificationGroupHasPeptide();
        //quantificationGroupHasPeptide.setQuantificationGroup(quantificationGroup);
        //quantificationGroupHasPeptide.setPeptide(peptide);
        // TODO Store QuantificationGroupHasPeptide instances

        return parsedPeptideMap;
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
     * IOException { List<Peptide> parsedPeptideMap = new ArrayList<>();
     * TabularFileLineValuesIterator valuesIterator = new
     * TabularFileLineValuesIterator(evidenceFile);
     *
     * return parsedPeptideMap; }
     */
    /**
     * Create a new peptide instance from the values contained in the map.
     *
     * @param values
     * @return a {@code PeptideAssumption} or the parsed row
     * @throws UnparseableException if something went wrong while parsing the
     * evidence row
     */
    public final PeptideAssumption createPeptide(final Map<String, String> values) throws UnparseableException, HeaderEnumNotInitialisedException {
        if (values.get(EvidenceHeaders.MS_MS_IDs.getColumnName()).isEmpty()) {
            //can't have evidence without proof
            throw new UnparseableException("peptide does not have an MS/MS scan");
        } else {
            String sequence = values.get(EvidenceHeaders.Sequence.getColumnName());

            // The charge corrected mass of the precursor ion.
            //Double massCorrected = Double.valueOf(values.get(EvidenceHeaders.Mass.column));


            ArrayList<String> proteinIds = new ArrayList<>();

            if (values.containsKey(EvidenceHeaders.Protein_Group_IDs.getColumnName())) {
                proteinIds = new ArrayList(Arrays.asList(values.get(EvidenceHeaders.Protein_Group_IDs.getColumnName()).split(";")));
            }
            /**
             * else { }
             */
            // Create peptide
            Peptide peptide = new Peptide(sequence, proteinIds, extractModifications(values));
            double score = -1;
            double pep = -1;
            if (values.containsKey(EvidenceHeaders.Score.getColumnName())) {
                if (!values.get(EvidenceHeaders.Score.getColumnName()).equalsIgnoreCase("NAN")) {
                    score = Double.parseDouble(values.get(EvidenceHeaders.Score.getColumnName()));
                }
            } else if (values.containsKey(EvidenceHeaders.Delta_score.getColumnName())) {
                if (!values.get(EvidenceHeaders.Delta_score.getColumnName()).equalsIgnoreCase("NAN")) {
                    score = Double.parseDouble(values.get(EvidenceHeaders.Delta_score.getColumnName()));
                }
            }
            if (values.containsKey(EvidenceHeaders.PEP.getColumnName())) {
                pep = Double.parseDouble(values.get(EvidenceHeaders.PEP.getColumnName()));
            }
            //we do what we can because we must
            Charge identificationCharge = null;

            if (values.containsKey(EvidenceHeaders.Charge.getColumnName())) {
                identificationCharge = new Charge(Charge.PLUS, Integer.parseInt(values.get(EvidenceHeaders.Charge.getColumnName())));
            }

            //99 is missing value according to statistics (har har) --> Advocate.MAXQUANT does not exist for the moment(and probably never will)
            PeptideAssumption assumption = new PeptideAssumption(peptide, 1, 99, identificationCharge, score);
            assumption.addUrParam(new MatchScore(score, pep));
            return assumption;
        }
    }

    /**
     * Create a new PeptideQuant instance from the values contained in the map.
     *
     * @param values
     * @return
     */
    public final PeptideQuantification createPeptideQuantification(final Map<String, String> values) throws HeaderEnumNotInitialisedException {
        //String peptideId
        String peptideID = values.get(EvidenceHeaders.Peptide_ID.getColumnName());
        // The id of the peptide quant
        int id = Integer.parseInt(values.get(EvidenceHeaders.id.getColumnName()));
        // The normalized ratio
        double ratio = Double.parseDouble(values.get(EvidenceHeaders.Normalized_Ratio.getColumnName()));
        //create utilities Ratio object
        Ratio utilitiesRatio = new Ratio(id, ratio);
        // Create peptidequantification
        PeptideQuantification pepQuant = new PeptideQuantification(peptideID);
        // add the ratio to the quant object
        pepQuant.addRatio(id, utilitiesRatio);
        return pepQuant;
    }

    public void addModification(String aModification) {
        modificationList.add(aModification);
    }

    public void clearModificationsList() {
        modificationList.clear();
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
    public final ArrayList<ModificationMatch> extractModifications(final Map<String, String> values) throws HeaderEnumNotInitialisedException {
        ArrayList<ModificationMatch> modificationsForPeptide = new ArrayList<>();
        // Sequence representation including the post-translational
        // modifications (abbreviation of the modification in brackets
        // before the modified AA). The sequence is always surrounded
        // by underscore characters ('_').
        String modifications = values.get(EvidenceHeaders.Modifications.getColumnName());
        if (modifications == null) {
            return modificationsForPeptide;
        } else {
            if ("Unmodified".equalsIgnoreCase(modifications)) {
                return modificationsForPeptide;
            }


            for (String modificationHeader : modificationList) {
                int location = -1;
                String modificationString;

                modificationHeader = modificationHeader.toLowerCase(Locale.US);

                if ((modificationString = values.get(modificationHeader)) != null) {
                    // modification found
                    // N-term check
                    if (modificationHeader.contains("n-term")) {
                        if ("1".equals(modificationString)) {
                            MaxQuantPtmScoring score = new MaxQuantPtmScoring();
                            // N-term has position 0
                            location = 0;
                            score.setDeltaScore(100.0);
                            score.setScore(100.0);
                            ModificationMatch match = new ModificationMatch(modificationHeader, true, location);
                            match.addUrParam(score);
                            modificationsForPeptide.add(match);
                        }
                    }

                    //C-term check
                    if (modificationHeader.contains("c-term")) {
                        if ("1".equals(modificationString)) {
                            // C-term has position end of sequence
                            MaxQuantPtmScoring score = new MaxQuantPtmScoring();
                            location = values.get(EvidenceHeaders.Sequence.getColumnName()).length() + 1;
                            score.setDeltaScore(100.0);
                            score.setScore(100.0);
                            ModificationMatch match = new ModificationMatch(modificationHeader, true, location);
                            match.addUrParam(score);
                            modificationsForPeptide.add(match);
                        }
                    }
                    //means the modification is in the sequence and we have to check the most likely location
                    if ((modificationString = values.get(modificationHeader + " probabilities")) != null) {
                        if (modificationString.contains("(")) {
                            modificationString = modificationString.replaceAll("_", "");
                            int previousLocation = 0;
                            String[] modificationLocations = modificationString.split("\\(");
                            String[] modificationDeltaScores = null;
                            if (values.containsKey(modificationHeader + " score diffs")) {
                                modificationDeltaScores = values.get(modificationHeader + " score diffs").split("\\(");
                            }
                            for (int i = 0; i < modificationLocations.length; i++) {
                                if (modificationLocations[i].contains(")")) {
                                    MaxQuantPtmScoring score = new MaxQuantPtmScoring();
                                    if (modificationDeltaScores != null) {
                                        score.setDeltaScore(Double.parseDouble(modificationDeltaScores[i].substring(0, modificationDeltaScores[i].indexOf(")"))));
                                    } else {
                                        score.setDeltaScore(-1.0);
                                    }
                                    score.setScore(Double.parseDouble(modificationLocations[i].substring(0, modificationLocations[i].indexOf(")"))));

                                    modificationLocations[i] = modificationLocations[i].replaceFirst(".*\\)", "");
                                    location = modificationLocations[i -1].length();
                                    ModificationMatch match = new ModificationMatch(modificationHeader, true, location);
                                    match.addUrParam(score);
                                    modificationsForPeptide.add(match);
                                }
                                previousLocation = location + previousLocation;
                            }
                        }
                    }
                }
            }
            //TODO parse parameters for fixed and variable modifications
        }
        return modificationsForPeptide;
    }
}

/**
 * Refer to headers in MaxQuant evidence.txt output files by enum values, as
 * headers are likely to change with each new version of MaxQuant. Their order
 * is also likely to change between files regardless of MaxQuant version.
 */
enum EvidenceHeaders implements HeaderEnum {

    id(new String[]{"id"}),
    Protein_Group_IDs(new String[]{"Protein Group IDs"}),
    Peptide_ID(new String[]{"Peptide ID"}),
    Mod_Peptide_ID(new String[]{"Mod. Peptide ID"}),
    MS_MS_IDs(new String[]{"MS/MS IDs"}),
    AIF_MS_MS_IDs(new String[]{"AIF MS/MS IDs"}),
    Oxidation_M_Site_IDs(new String[]{"Oxidation (M) Site IDs"}),
    Sequence(new String[]{"Sequence"}),
    Length(new String[]{"Length"}),
    Modifications(new String[]{"Modifications"}),
    Modified_Sequence(new String[]{"Modified Sequence"}),
    Oxidation_M_Probabilities(new String[]{"Oxidation (M) Probabilities"}),
    Oxidation_M_Score_Diffs(new String[]{"Oxidation (M) Score Diffs"}),
    Acetyl_Protein_N_term(new String[]{"Acetyl (Protein N-term)"}),
    Acetyl_K(new String[]{"Acetyl (K)"}),
    Acetyl_K_Probabilities(new String[]{"Acetyl (K) Probabilities"}),
    Acetyl_K_Score_Diffs(new String[]{"Acetyl (K) Score Diffs"}),
    Oxidation_M(new String[]{"Oxidation (M)"}),
    Proteins(new String[]{"Proteins"}),
    Leading_Proteins(new String[]{"Leading Proteins"}),
    Leading_Razor_Protein(new String[]{"Leading Razor Protein"}),
    Gene_Names(new String[]{"Gene Names"}),
    Protein_Names(new String[]{"Protein Names"}),
    Protein_Descriptions(new String[]{"Protein Descriptions"}),
    Uniprot(new String[]{"Uniprot"}),
    Type(new String[]{"Type"}),
    Raw_File(new String[]{"Raw File"}),
    Fraction(new String[]{"Fraction"}),
    Experiment(new String[]{"Experiment"}),
    Charge(new String[]{"Charge"}),
    m_z(new String[]{"m/z"}),
    Mass(new String[]{"Mass"}),
    Resolution(new String[]{"Resolution"}),
    Uncalibrated_Calibrated_m_z_ppm(new String[]{"Uncalibrated - Calibrated m/z [ppm]"}),
    Mass_Error_ppm(new String[]{"Mass Error [ppm]"}),
    Uncalibrated_Mass_Error_ppm(new String[]{"Uncalibrated Mass Error [ppm]"}),
    Retention_Time(new String[]{"Retention Time"}),
    Retention_Length(new String[]{"Retention Length"}),
    Calibrated_Retention_Time(new String[]{"Calibrated Retention Time"}),
    Retention_Time_Calibration(new String[]{"Retention Time Calibration"}),
    Match_Time_Difference(new String[]{"Match Time Difference"}),
    PIF(new String[]{"PIF"}),
    Fraction_of_total_spectrum(new String[]{"Fraction of total spectrum"}),
    Base_peak_fraction(new String[]{"Base peak fraction"}),
    PEP(new String[]{"PEP"}),
    MS_MS_Count(new String[]{"MS/MS Count"}),
    MS_MS_Scan_Number(new String[]{"MS/MS Scan Number"}),
    Score(new String[]{"Score"}),
    Delta_score(new String[]{"Delta score"}),
    Combinatorics(new String[]{"Combinatorics"}),
    Intensity(new String[]{"Intensity"}),
    Reverse(new String[]{"Reverse"}),
    Contaminant(new String[]{"Contaminant"}),
    Normalized_Ratio(new String[]{"Ratio H/L normalized"}),
    best_MS_MS_ID(new String[]{"Best MS/MS"});
    /**
     * The name of the field in the evidence.txt MaxQuant output file
     */
    private String[] columnNames;
    private int columnReference = -1;

    private EvidenceHeaders(final String[] fieldnames) {
        columnNames = fieldnames;
    }

    @Override
    public final String[] returnPossibleColumnNames() {
        return columnNames;
    }

    @Override
    public final void setColumnReference(int columnReference) {
        this.columnReference = columnReference;
    }

    @Override
    public final String getColumnName() throws HeaderEnumNotInitialisedException {
        if (columnNames != null) {
            if (columnReference < 0 || columnReference > (columnNames.length - 1) && columnNames.length > 0) {
                return columnNames[0].toLowerCase(Locale.US);
            } else if (columnNames.length < 0) {
                throw new HeaderEnumNotInitialisedException("header enum not initialised");
            } else {
                return columnNames[columnReference].toLowerCase(Locale.US);
            }
        } else {
            throw new HeaderEnumNotInitialisedException("array was null");
        }
    }
}
