package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.Spectrum;

/**
 * Parser for the MaxQuant msms.txt output files that creates {@link Spectrum} instances and {@link Quantification}s and
 * links them to the argument {@link QuantificationGroup}.<br>
 * <br>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse the files into Map<String,String>
 * records.
 */
public class MaxQuantMsmsParser {
	private static final Logger	log	= LoggerFactory.getLogger(MaxQuantMsmsParser.class);

	/**
	 * Parse the argument msms.txt file, and link all {@link Spectrum} instances and {@link Quantification}s found
	 * within to the argument {@link QuantificationGroup}.
	 * 
	 * @param msmsFile
	 * @param quantificationGroup
	 * @throws IOException
	 */
	public void parse(final File msmsFile, final QuantificationGroup quantificationGroup) throws IOException {
		// Convert file into some values we can loop over, without reading file in at once
		TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile);

		// Create and persist objects for all lines in file
		for (Map<String, String> values : valuesIterator) {
			// Create objects
			Spectrum spectrum = createSpectrum(values);

			// A unique (consecutive) identifier for each row in the msms
			// table, which is used to cross-link the information in this file with
			// the information stored in the other files.
			String id = values.get(MsmsHeaders.id.column);

			// - Unannotated in documentation -
			Double simpleMassError = Double.valueOf(values.get(MsmsHeaders.Simple_Mass_Error_ppm.column));

			// Andromeda score for the best associated MS/MS spectrum.
			Double score = Double.valueOf(values.get(MsmsHeaders.Score.column));

			// Posterior Error Probability
			Double pep = Double.valueOf(values.get(MsmsHeaders.PEP.column));

			// The type of precursor ion as identified by MaxQuant.
			String type = values.get(MsmsHeaders.Type.column);

			// Create Quantification object and link to the quantificationGroup
			Quantification quantification = new Quantification();
			// TODO The above values should possibly be added to this Quantification instance, to give it some content
			quantification.setSpectrum(spectrum);
			quantification.setQuantificationGroup(quantificationGroup);
			quantificationGroup.getQuantifications().add(quantification);
		}
	}

	/**
	 * Create a very sparsely populated {@link Spectrum} instance, as not all the desired information can be restrieved
	 * from the file we are parsing. The misisng values will be supplied at a later point in time by other components.
	 * 
	 * @param values
	 * @return the created spectrum
	 */
	Spectrum createSpectrum(final Map<String, String> values) {
		// The charge-state of the precursor ion.
		Integer charge = Integer.valueOf(values.get(MsmsHeaders.Charge.column));

		// The mass-over-charge of the precursor ion.
		Double m_z = Double.valueOf(values.get(MsmsHeaders.m_z.column));

		// Set field values on Spectrum
		Spectrum spectrum = new Spectrum();
		spectrum.setCharge(charge);
		spectrum.setMzRatio(m_z);
		return spectrum;
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
	String	column;

	private MsmsHeaders(final String fieldname) {
		column = fieldname;
	}
}
