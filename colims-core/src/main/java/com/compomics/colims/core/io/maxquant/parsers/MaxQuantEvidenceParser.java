package com.compomics.colims.core.io.maxquant.parsers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantEvidenceHeaders;
import com.compomics.colims.core.io.maxquant.headers.MaxQuantModificationHeaders;
import com.compomics.colims.core.io.maxquant.urparams.MaxQuantPtmScoring;
import com.compomics.colims.core.io.maxquant.utilities_mappers.MaxQuantUtilitiesPeptideMapper;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.QuantificationWeight;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MaxQuantUtilitiesPeptideMapper maxQuantUtilitiesPeptideMapper;

    public Map<Integer, List<Quantification>> quantifications = new HashMap<>();
    public Map<Integer, PeptideAssumption> peptideAssumptions = new HashMap<>();

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
                        if (peptideAssumptions.containsKey(Integer.parseInt(msmsId))) {
                            throw new UnparseableException("conflicts in the evidence file: multiple peptides for the same spectrum");
                        }

                        int spectrumID = Integer.parseInt(msmsId);
                        PeptideAssumption assumption = createPeptideAssumption(values);

                        peptideAssumptions.put(spectrumID, assumption);

                        // create colims peptide from assumption, for use in group
                        com.compomics.colims.model.Peptide peptide = new com.compomics.colims.model.Peptide();
                        maxQuantUtilitiesPeptideMapper.map(assumption, peptide);
                        maxQuantUtilitiesPeptideMapper.clear();

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
     * Create a peptide assumption from a map of values.
     *
     * @param values Map of headers and values
     * @return A fresh peptide
     * @throws HeaderEnumNotInitialisedException
     */
    public PeptideAssumption createPeptideAssumption(final Map<String, String> values) {
        Peptide peptide = new Peptide(values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()), extractModifications(values));
        // possibly dubious
        peptide.setParentProteins(new ArrayList(Arrays.asList(values.get(MaxQuantEvidenceHeaders.PROTEIN_GROUP_IDS.getDefaultColumnName()).split(";"))));


        double score = -1, pep = -1;

        if (!values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()).equals("NaN")) {
            score = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.SCORE.getDefaultColumnName()));
        } else if (!values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()).equals("NaN")) {
            score = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.DELTA_SCORE.getDefaultColumnName()));
        }

        if(values.containsKey(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName()))
        pep = Double.parseDouble(values.get(MaxQuantEvidenceHeaders.PEP.getDefaultColumnName()));

        Charge identificationCharge =  new Charge(Charge.PLUS, Integer.parseInt(values.get(MaxQuantEvidenceHeaders.CHARGE.getDefaultColumnName())));

        //99 is missing value according to statistics (har har) --> Advocate.MAXQUANT does not exist for the moment(and probably never will)
        PeptideAssumption assumption = new PeptideAssumption(peptide, 1, 99, identificationCharge, score);
        assumption.addUrParam(new MatchScore(score, pep));


        return assumption;
    }

    /**
     * Extract modifications from a given value set and link them to relevant peptides.
     *
     * @param values Map of headers and values
     * @return A list of matches
     * @throws HeaderEnumNotInitialisedException
     */
    private ArrayList<ModificationMatch> extractModifications(final Map<String, String> values) {
        ArrayList<ModificationMatch> modificationsForPeptide = new ArrayList<>();

        // Sequence representation including the post-translational modifications (abbreviation of the modification in
        // brackets before the modified AA). The sequence is always surrounded by underscore characters ('_').
        // Also that looks like a face.
        String modifications = values.get(MaxQuantEvidenceHeaders.MODIFICATIONS.getDefaultColumnName());

        if (modifications == null || "Unmodified".equalsIgnoreCase(modifications)) {
            return modificationsForPeptide;
        } else {
            for (MaxQuantModificationHeaders modificationHeader : MaxQuantModificationHeaders.values()) {
                int location = -1;
                String modificationString = values.get(modificationHeader.getDefaultColumnName());

                // if modification found
                if (modificationString != null) {
                    // N-term check
                    if (modificationHeader.getDefaultColumnName().contains("n-term")) {
                        if ("1".equals(modificationString)) {
                            MaxQuantPtmScoring score = new MaxQuantPtmScoring();
                            // N-term has position 0
                            location = 0;
                            score.setDeltaScore(100.0);
                            score.setScore(100.0);
                            ModificationMatch match = new ModificationMatch(modificationHeader.getDefaultColumnName(), true, location);
                            match.addUrParam(score);
                            modificationsForPeptide.add(match);
                        }
                    }

                    //C-term check
                    if (modificationHeader.getDefaultColumnName().contains("c-term")) {
                        if ("1".equals(modificationString)) {
                            // C-term has position end of sequence
                            MaxQuantPtmScoring score = new MaxQuantPtmScoring();
                            location = values.get(MaxQuantEvidenceHeaders.SEQUENCE.getDefaultColumnName()).length() + 1;
                            score.setDeltaScore(100.0);
                            score.setScore(100.0);
                            ModificationMatch match = new ModificationMatch(modificationHeader.getDefaultColumnName(), true, location);
                            match.addUrParam(score);
                            modificationsForPeptide.add(match);
                        }
                    }

                    //means the modification is in the sequence and we have to check the most likely location
                    if ((modificationString = values.get(modificationHeader.getDefaultColumnName() + " probabilities")) != null) {
                        if (modificationString.contains("(")) {
                            modificationString = modificationString.replaceAll("_", "");
                            int previousLocation = 0;
                            String[] modificationLocations = modificationString.split("\\(");
                            String[] modificationDeltaScores = null;

                            if (values.containsKey(modificationHeader.getDefaultColumnName() + " score diffs")) {
                                modificationDeltaScores = values.get(modificationHeader.getDefaultColumnName() + " score diffs").split("\\(");
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
                                    location = modificationLocations[i - 1].length();
                                    ModificationMatch match = new ModificationMatch(modificationHeader.getDefaultColumnName(), true, location);
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
            //parameters.get("fixed modifications"), parameters.get("variable modifications")
        }

        return modificationsForPeptide;
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        peptideAssumptions.clear();
        quantifications.clear();
    }
}
