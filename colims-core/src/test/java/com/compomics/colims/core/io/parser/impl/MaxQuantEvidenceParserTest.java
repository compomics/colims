package com.compomics.colims.core.io.parser.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MaxQuantEvidenceParserTest {
	File	evidence;

	@Before
	public void setup() {
		// Get a handle to the file
		String filename = "testdata/evidence_subset.tsv";
		filename = getClass().getClassLoader().getResource(filename).getFile();
		evidence = new File(filename);
	}

	@Test
	public void testMaxQuantEvidenceParser() throws IOException {
		MaxQuantEvidenceParser parser = new MaxQuantEvidenceParser();

		// Parse file
		parser.parse(evidence);

		// Assertions
		fail("Not yet implemented");
	}

	@Test
	public void testEvidenceLineValuesIterator() throws IOException {
		// Create iterator for ELVI
		EvidenceLineValuesIterator elvi = new EvidenceLineValuesIterator(evidence);

		// Iterate over ELVI and assign values to a list for further inspection
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = IteratorUtils.toList(elvi);

		// Check properties of first and last item in list
		Map<String, String> first = list.get(0);
		Assert.assertEquals("0", first.get(Headers.id.fieldname));

		Map<String, String> last = list.get(list.size());
		Assert.assertEquals("8", last.get(Headers.id.fieldname));
	}
}
