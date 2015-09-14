package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.ModificationMappingException;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.UnparseableException;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesModificationMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.model.enums.QuantificationWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the MaxQuant evidence file
 * <p/>
 * Created by Iain on 01/12/2014.
 */
@Component
public class MaxQuantEvidenceParser {

    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;

    /**
     * Name of evidence file
     */
    private static final String EVIDENCETXT = "evidence.txt";

    private static final HeaderEnum[] MANDATORY_HEADERS = new HeaderEnum[]{
            MaxQuantEvidenceHeaders.ACETYL_PROTEIN_N_TERM,
            MaxQuantEvidenceHeaders.CHARGE,
            MaxQuantEvidenceHeaders.DELTA_SCORE,
            MaxQuantEvidenceHeaders.MASS,
            MaxQuantEvidenceHeaders.MODIFICATIONS,
            MaxQuantEvidenceHeaders.MS_MS_IDS,
            MaxQuantEvidenceHeaders.OXIDATION_M,
            MaxQuantEvidenceHeaders.PEP,
            MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS,
            MaxQuantEvidenceHeaders.SCORE,
            MaxQuantEvidenceHeaders.SEQUENCE
    };

    /**
     * Spectrum IDs and associated quantifications
     */
    public Map<Integer, List<Quantification>> quantifications = new HashMap<>();

    /**
     * Spectrum IDs and peptides
     */
    public Map<Integer, Peptide> peptides = new HashMap<>();

    /**
     * Peptides and associated protein group IDs
     */
    public Map<Peptide, List<Integer>> peptideProteins = new HashMap<>();

    /**
     * Iterable intensity headers, based on number of labels chosen.
     */
    private static final Map<Integer, String[]> INTENSITY_HEADERS = new HashMap<>();

    static {
        INTENSITY_HEADERS.put(1, new String[]{"intensity"});
        INTENSITY_HEADERS.put(2, new String[]{"intensity l", "intensity h"});
        INTENSITY_HEADERS.put(3, new String[]{"intensity l", "intensity m", "intensity h"});
    }

    /**
     * As above but quantification weights.
     */
    public static final Map<Integer, QuantificationWeight[]> WEIGHT_OPTIONS = new HashMap<>();

    static {
        WEIGHT_OPTIONS.put(1, new QuantificationWeight[]{QuantificationWeight.LIGHT});
        WEIGHT_OPTIONS.put(2, new QuantificationWeight[]{QuantificationWeight.LIGHT, QuantificationWeight.HEAVY});
        WEIGHT_OPTIONS.put(3, new QuantificationWeight[]{QuantificationWeight.LIGHT, QuantificationWeight.MEDIUM, QuantificationWeight.HEAVY});
    }

    /**
     * Parse an evidence file for peptides and quantifications, also create groups for these.
     *
     * @param quantFolder Evidence text file from MQ output
     * @throws IOException
     */
    public void parse(File quantFolder, String multiplicity) throws IOException, UnparseableException, MappingException {
        TabularFileLineValuesIterator evidenceIterator = new TabularFileLineValuesIterator(new File(quantFolder, EVIDENCETXT), MANDATORY_HEADERS);

        Map<String, String> values;
        int intensityCount;

        intensityCount = Integer.parseInt(multiplicity);

        QuantificationWeight[] weights = WEIGHT_OPTIONS.get(intensityCount);
        String[] intensityColumns = INTENSITY_HEADERS.get(intensityCount);

        while (evidenceIterator.hasNext()) {
            values = evidenceIterator.next();

            double[] intensities = new double[intensityCount];

            int i = 0;

            for (String header : intensityColumns) {
                intensities[i] = parseIntensity(values.get(header));
                ++i;
            }

            if (values.get(MaxQuantEvidenceHeaders.MS_MS_IDS.getDefaultColumnName()) != null) {
                String[] msmsIds = values.get(MaxQuantEvidenceHeaders.MS_MS_IDS.getDefaultColumnName()).split(";");

                for (String msmsId : msmsIds) {
                    if (!msmsId.isEmpty()) {
                        if (peptides.containsKey(Integer.parseInt(msmsId))) {
                            throw new UnparseableException("conflicts in the evidence file: multiple peptides for the same spectrum");
                        }

                        int spectrumID = Integer.parseInt(msmsId);
                        Peptide peptide = createPeptide(values);

                        peptides.put(spectrumID, peptide);

                        List<Quantification> spectrumQuantList = new ArrayList<>();

                        for (int j = 0; j < intensityCount; ++j) {
                            Quantification quant = new Quantification();
                            quant.setIntensity(intensities[j]);
                            quant.setWeight(weights[j]);

                            spectrumQuantList.add(quant);

                            QuantificationGroup quantGroup = new QuantificationGroup();
                            quantGroup.setQuantification(quant);
                            quantGroup.setPeptide(peptide);
                        }

                        if (quantifications.containsKey(spectrumID)) {
                            quantifications.get(spectrumID).addAll(spectrumQuantList);
                        } else {
                            quantifications.put(spectrumID, spectrumQuantList);
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse an intensity value from a string to a double.
     *
     * @param intensityStr Intensity in string form
     * @return Intensity in double form
     */
    public double parseIntensity(String intensityStr) {
        double intensity = 0.0;

        if (intensityStr != null && !intensityStr.isEmpty() && !intensityStr.toLowerCase().contains("nan")) {
            intensity = Double.parseDouble(intensityStr);
        }

        return intensity;
    }

    /**
     * Create a Peptide from a row in the evidence file
     *
     * @param values Set of values from a line in the file
     * @return Peptide object
     */
    public Peptide createPeptide(Map<String, String> values) throws ModificationMappingException {
        Peptide peptide = new Peptide();

        double probability = -1, pep = -1;

        if (!values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()));
        } else if (!values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()));
        }

        if (values.containsKey(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName())) {
            pep = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName()));
        }

        peptide.setCharge(Integer.parseInt(values.get(MaxQuantEvidenceHeaders.CHARGE.getDefaultColumnName())));
        peptide.setPsmPostErrorProbability(pep);
        peptide.setPsmProbability(probability);
        peptide.setSequence(values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()));
        peptide.setTheoreticalMass(Double.valueOf(values.get(MaxQuantEvidenceHeaders.MASS.getDefaultColumnName())));
        peptide.getPeptideHasModifications().addAll(createModifications(peptide, values));

        List<Integer> proteinGroups = new ArrayList<>();

        if (values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getDefaultColumnName()) != null) {
            for (String id : values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getDefaultColumnName()).split(";")) {
                proteinGroups.add(Integer.parseInt(id));
            }
        }

        peptideProteins.put(peptide, proteinGroups);

        return peptide;
    }

    /**
     * Create modifications for a given peptide
     *
     * @param peptide Peptide to associate with modifications
     * @param values  Row of data from evidence file
     * @return List of PeptideHasModification objects
     */
    private List<PeptideHasModification> createModifications(Peptide peptide, Map<String, String> values) throws ModificationMappingException {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        String modifications = values.get(MaxQuantEvidenceHeaders.MODIFICATIONS.getDefaultColumnName());

        if (modifications == null || "Unmodified".equalsIgnoreCase(modifications)) {
            return peptideHasModifications;
        } else {
            ArrayList<String> modificationList = new ArrayList<>();

            for (String mod : modifications.split(";")) {
                modificationList.add(mod.toLowerCase());
            }

            for (String modificationHeader : modificationList) {
                if (Character.isDigit(modificationHeader.charAt(0))) {
                    modificationHeader = modificationHeader.split(" ", 2)[1];
                }

                String modificationString = values.get(modificationHeader);

                if (modificationString != null) {
                    if (modificationHeader.contains("n-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = createPeptideHasModification(100.0, 0, 100.0, peptide);

                        Modification modification = utilitiesModificationMapper.mapModificationMatch(modificationHeader);

                        //modification.getPeptideHasModifications().add(phModification);

                        phModification.setModification(modification);
                        peptideHasModifications.add(phModification);
                    }

                    if (modificationHeader.contains("c-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = createPeptideHasModification(100.0, values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()).length() - 1, 100.0, peptide);

                        Modification modification = utilitiesModificationMapper.mapModificationMatch(modificationHeader);
                        //modification.getPeptideHasModifications().add(phModification);

                        phModification.setModification(modification);

                        peptideHasModifications.add(phModification);
                    }

                    // TODO: find some test data for this
                    if ((modificationString = values.get(modificationHeader + " probabilities")) != null && modificationString.contains("(")) {
                        modificationString = modificationString.replaceAll("_", "");
                        int location = -1;
                        int previousLocation = 0;
                        String[] modificationLocations = modificationString.split("\\(");
                        String[] modificationDeltaScores = null;

                        if (values.containsKey(modificationHeader + " score diffs")) {
                            modificationDeltaScores = values.get(modificationHeader + " score diffs").split("\\(");
                        }

                        for (int i = 0; i < modificationLocations.length; i++) {
                            if (modificationLocations[i].contains(")")) {
                                PeptideHasModification phModification = new PeptideHasModification();

                                phModification.setModificationType(ModificationType.VARIABLE);
                                phModification.setPeptide(peptide);

                                Modification modification = utilitiesModificationMapper.mapModificationMatch(modificationHeader);
                                //modification.getPeptideHasModifications().add(phModification);

                                if (modificationDeltaScores != null) {
                                    phModification.setDeltaScore(Double.parseDouble(modificationDeltaScores[i].substring(0, modificationDeltaScores[i].indexOf(")"))));
                                } else {
                                    phModification.setDeltaScore(-1.0);
                                }

                                phModification.setProbabilityScore(Double.parseDouble(modificationLocations[i].substring(0, modificationLocations[i].indexOf(")"))));

                                modificationLocations[i] = modificationLocations[i].replaceFirst(".*\\)", "");
                                phModification.setLocation(modificationLocations[i - 1].length());

                                peptideHasModifications.add(phModification);
                            }

                            previousLocation = location + previousLocation;
                        }
                    }
                }
            }
        }

        return peptideHasModifications;
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        peptideProteins.clear();
        peptides.clear();
        quantifications.clear();
    }

    private PeptideHasModification createPeptideHasModification(double deltaScore, int location, double probability, Peptide peptide) {
        PeptideHasModification phModification = new PeptideHasModification();

        phModification.setModificationType(ModificationType.VARIABLE);
        phModification.setDeltaScore(deltaScore);
        phModification.setLocation(location);
        phModification.setProbabilityScore(probability);
        phModification.setPeptide(peptide);

        return phModification;
    }
}