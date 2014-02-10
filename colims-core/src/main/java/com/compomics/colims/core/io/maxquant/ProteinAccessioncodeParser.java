package com.compomics.colims.core.io.maxquant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common class used by MaxQuant related parser classes to extract the preferred protein accessioncode from a MaxQuant
 * string of separated accessioncodes.
 */
public class ProteinAccessioncodeParser {
	/**
	 * Return a list of regex strings that can isolate the accessioncodes from MaxQuant "Proteins" column values as the
	 * first matched group. TODO Alter the implementation to retrieve the accessioncode regexes from a configuration
	 * file or database table
	 * 
	 * @return list of regular expressions that return accession codes as group(1)
	 */
	static List<String> getProteinAccessioncodeMatchingRegexes() {
		List<String> regexes = new ArrayList<>();
		// gi|153807749|ref|ZP_01960417.1| hypothetical protein BACCAC_02032 [Bacteroides caccae ATCC 43185];
		regexes.add("gi\\|\\d+\\|ref\\|(.+?)\\|");
		// IPI:IPI00257882.7|SWISS-PROT:P12955|TREMBL:A8K3Z1;A8K416;A8K696;A8MX47|ENSEMBL:ENSP00000244137|REFSEQ:...
		regexes.add("IPI:(.+?)\\|");
		return regexes;
	}

	/**
	 * From the {@link EvidenceHeaders#Proteins} column (or similar string) extract all the accession codes we can find
	 * using a collection of regular expressions retrieved through {@link #getProteinAccessioncodeMatchingRegexes()}.
	 * 
	 * @param entireLine
	 *            string retrieved from MaxQuant column {@link EvidenceHeaders#Proteins}, or similar
	 * @return list of accession codes found in entireLine using the regular expressions retrieved through
	 *         {@link #getProteinAccessioncodeMatchingRegexes()}
	 */
	static ArrayList<String> extractProteinAccessioncodes(final String entireLine) {
		// Unfortunately, there are different formats in use for the accession codes, handle each case we find here
		List<String> regexes = getProteinAccessioncodeMatchingRegexes();

		// Add all occurrences of any matches found in entireLine for any of the patterns
		ArrayList<String> accessionCodes = new ArrayList<>();
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

}
