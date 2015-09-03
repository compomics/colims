/**
 * This package contains the implementations to parse MaxQuant text output files themselves, as well as some helper
 * classes that provide some more generic functionality. External packages will most likely want to invoke just the
 * {@link com.compomics.colims.core.io.parser.impl.MaxQuantParser} with the MaxQuant txt output folder as argument.
 * This class will in turn invoke the {@link com.compomics.colims.core.io.parser.impl.MaxQuantEvidenceParser} for the
 * evidence.txt file contained within, and {@link com.compomics.colims.core.io.parser.impl.MaxQuantMsmsParser} for the
 * msms.txt file. Both of these latter classes use the
 * {@link com.compomics.colims.core.io.parser.impl.TabularFileLineValuesIterator} class to parse the large output files
 * without reading the entire file into memory up front.
 * 
 * The {@link com.compomics.colims.core.io.parser.impl.ProteinAccessioncodeParser} contains some shared logic to extract
 * accessioncodes from an accessioncode line as returned by MaxQuant.
 * 
 * The {@link com.compomics.colims.core.io.parser.impl.MzMLParserImpl} can import multiple mzML files into COLIMS.
 */
package com.compomics.colims.distributed.io.maxquant;