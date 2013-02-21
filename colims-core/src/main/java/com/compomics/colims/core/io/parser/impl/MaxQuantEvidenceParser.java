package com.compomics.colims.core.io.parser.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.io.LineReader;

public class MaxQuantEvidenceParser {
	public MaxQuantEvidenceParser() {
		// TODO Auto-generated constructor stub
	}

	public void parse(final File evidenceFile) throws IOException {
		for (Map<String, String> values : new EvidenceLineValuesIterator(evidenceFile))
			System.out.println(values);

		// Explore Apache Commons Lang Math NumberUtils to parse scientific values into numbers where appropriate
	}
}

class EvidenceLineValuesIterator implements Iterable<Map<String, String>>, Iterator<Map<String, String>> {
	static final char	delimiter	= '\t';

	FileReader			fileReader	= null;
	LineReader			lineReader	= null;
	String[]			nextLine	= null;
	String[]			headers		= new String[0];

	public EvidenceLineValuesIterator(final File evidenceFile) throws IOException {
		// Extract headers
		fileReader = new FileReader(evidenceFile);
		lineReader = new LineReader(fileReader);
		String readLine = lineReader.readLine();

		// Determine the headers for this particular file, so we can assign values to the right key in our map
		headers = readLine.split("" + delimiter);

		// Initialize the first line in the nextLine field
		advanceLine();
	}

	void advanceLine() {
		// Advance to the next line
		try {
			String readLine = lineReader.readLine();
			if (readLine != null) {
				nextLine = readLine.split("" + delimiter);
				return;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		nextLine = null;
	}

	@Override
	public boolean hasNext() {
		if (nextLine == null)
			try {
				fileReader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		return nextLine != null;
	}

	@Override
	public Map<String, String> next() {
		// nextLine[] is an array of values from the line
		Map<String, String> lineValues = new HashMap<>();
		for (int i = 0; i < nextLine.length; i++)
			lineValues.put(headers[i], nextLine[i]);

		// Advance to the next line for the next invocation
		advanceLine();

		return lineValues;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This parser does not support removing lines from a MaxQuant file.");
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		return this;
	}
}

/**
 * Refer to headers in MaxQuant evidence.txt output files by enum values, as headers are likely to change with each new
 * version of MaxQuant. Their order is also likely to change between files regardless of MaxQuant version.
 */
enum Headers {
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
	String	fieldname;

	private Headers(final String fieldname) {
		this.fieldname = fieldname;
	}
}
