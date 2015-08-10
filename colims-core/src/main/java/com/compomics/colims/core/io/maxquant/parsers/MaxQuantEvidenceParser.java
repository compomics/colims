package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantModificationHeaders;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.colims.model.enums.QuantificationWeight;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Iain on 01/12/2014.
 */
@Component
public class MaxQuantEvidenceParser {
    private static final String EVIDENCETXT = "evidence.txt";

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantEvidenceHeaders.MS_MS_IDS,
            MaxQuantEvidenceHeaders.SEQUENCE,
            MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS,
            MaxQuantEvidenceHeaders.SCORE,
            MaxQuantEvidenceHeaders.DELTA_SCORE,
            MaxQuantEvidenceHeaders.PEP,
            MaxQuantEvidenceHeaders.MODIFICATIONS
    };

    public Map<Integer, List<Quantification>> quantifications = new HashMap<>();
    public Map<Integer, com.compomics.colims.model.Peptide> peptides = new HashMap<>();

    /**
     * Iterable intensity headers, based on number of labels chosen.
     */
    public static final Map<Integer, String[]> intensityHeaders = new HashMap<>();

    static {
        intensityHeaders.put(1, new String[]{"intensity"});
        intensityHeaders.put(2, new String[]{"intensity l", "intensity h"});
        intensityHeaders.put(3, new String[]{"intensity l", "intensity m", "intensity h"});
    }

    /**
     * As above but quantification weights.
     */
    public static final Map<Integer, QuantificationWeight[]> weightOptions = new HashMap<>();

    static {
        weightOptions.put(1, new QuantificationWeight[]{QuantificationWeight.LIGHT});
        weightOptions.put(2, new QuantificationWeight[]{QuantificationWeight.LIGHT, QuantificationWeight.HEAVY});
        weightOptions.put(3, new QuantificationWeight[]{QuantificationWeight.LIGHT, QuantificationWeight.MEDIUM, QuantificationWeight.HEAVY});
    }

    /**
     * Parse an evidence file for peptides and quantifications, also create groups for these.
     *
     * @param quantFolder Evidence text file from MQ output
     * @throws IOException
     */
    public void parse(final File quantFolder, String multiplicity) throws IOException, UnparseableException, MappingException {
        //todo change to headerenum constructor
        TabularFileLineValuesIterator evidenceIterator = new TabularFileLineValuesIterator(new File(quantFolder, EVIDENCETXT),mandatoryHeaders);

        Map<String, String> values;
        int intensityCount;

        intensityCount = Integer.parseInt(multiplicity);

        QuantificationWeight[] weights = weightOptions.get(intensityCount);
        String[] intensityColumns = intensityHeaders.get(intensityCount);

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
                        com.compomics.colims.model.Peptide peptide = createPeptide(values);

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

    public com.compomics.colims.model.Peptide createPeptide(final Map<String, String> values) {
        com.compomics.colims.model.Peptide peptide = new com.compomics.colims.model.Peptide();

        double probability = -1, pep = -1;

        if (!values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()));
        } else if (!values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()).equalsIgnoreCase("nan")) {
            probability = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()));
        }

        if(values.containsKey(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName())) {
            pep = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName()));
        }

        peptide.setCharge(Integer.parseInt(values.get(MaxQuantEvidenceHeaders.CHARGE.getDefaultColumnName())));
        peptide.setPsmPostErrorProbability(pep);
        peptide.setPsmProbability(probability);
        peptide.setSequence(values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()));
        peptide.setTheoreticalMass(Double.valueOf(values.get(MaxQuantEvidenceHeaders.MASS.getDefaultColumnName())));
        peptide.setPeptideHasModifications(createModifications(peptide, values));

        return peptide;
    }

    private List<PeptideHasModification> createModifications(com.compomics.colims.model.Peptide peptide, final Map<String, String> values) {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        // TODO: check locations (mapper mentions 1-indexed in utils modification...)

        // Sequence representation including the post-translational modifications (abbreviation of the modification in
        // brackets before the modified AA). The sequence is always surrounded by underscore characters ('_').
        // Also that looks like a face.
        String modifications = values.get(MaxQuantEvidenceHeaders.MODIFICATIONS.getDefaultColumnName());

        if (modifications == null || "Unmodified".equalsIgnoreCase(modifications)) {
            return peptideHasModifications;
        } else {
            for (MaxQuantModificationHeaders modificationHeader : MaxQuantModificationHeaders.values()) {
                String modificationString = values.get(modificationHeader.getDefaultColumnName());

                if (modificationString != null) {
                    if (modificationHeader.getDefaultColumnName().contains("n-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = new PeptideHasModification();
                        // all currently set as variable mods
                        phModification.setModificationType(ModificationType.VARIABLE);
                        // theoretic ptm = modificationHeader.getDefaultColumnName()
                        phModification.setModification(new Modification()); // TODO!! need methods from utilsmodmapper

                        phModification.setDeltaScore(100.0);
                        phModification.setLocation(0);
                        phModification.setProbabilityScore(100.0);
                        phModification.setPeptide(peptide);

                        peptideHasModifications.add(phModification);
                    }

                    if (modificationHeader.getDefaultColumnName().contains("c-term") && "1".equals(modificationString)) {
                        PeptideHasModification phModification = new PeptideHasModification();

                        phModification.setModificationType(ModificationType.VARIABLE);
                        phModification.setModification(new Modification());
                        phModification.setDeltaScore(100.0);
                        phModification.setLocation(values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()).length() - 1);
                        phModification.setProbabilityScore(100.0);
                        phModification.setPeptide(peptide);

                        peptideHasModifications.add(phModification);
                    }

                    if ((modificationString = values.get(modificationHeader.getDefaultColumnName() + " probabilities")) != null && modificationString.contains("(")) {
                        modificationString = modificationString.replaceAll("_", "");
                        int location = -1;
                        int previousLocation = 0;
                        String[] modificationLocations = modificationString.split("\\(");
                        String[] modificationDeltaScores = null;

                        if (values.containsKey(modificationHeader.getDefaultColumnName() + " score diffs")) {
                            modificationDeltaScores = values.get(modificationHeader.getDefaultColumnName() + " score diffs").split("\\(");
                        }

                        for (int i = 0; i < modificationLocations.length; i++) {
                            if (modificationLocations[i].contains(")")) {
                                PeptideHasModification phModification = new PeptideHasModification();

                                phModification.setModificationType(ModificationType.VARIABLE);
                                phModification.setModification(new Modification());
                                phModification.setPeptide(peptide);

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
        peptides.clear();
        quantifications.clear();
    }
}