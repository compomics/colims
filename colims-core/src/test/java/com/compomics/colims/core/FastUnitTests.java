package com.compomics.colims.core;

import com.compomics.colims.core.io.fasta.FastaDbParser;
import com.compomics.colims.core.ontology.OntologyMapperTest;
import com.compomics.colims.core.permission.PermissionInterceptorTest;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapperTest;
import com.compomics.colims.core.io.mzidentml.MzIdentMlExporterTest;
import com.compomics.colims.core.io.mzml.MzMLParserTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({PermissionInterceptorTest.class,
        ColimsSpectrumMapperTest.class,
        MzMLParserTest.class,
        MzIdentMlExporterTest.class,
        OntologyMapperTest.class,
        FastaDbParser.class
})
public class FastUnitTests {
}
