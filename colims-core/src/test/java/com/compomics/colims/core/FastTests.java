package com.compomics.colims.core;

import com.compomics.colims.core.authorization.AuthorizationInterceptorTest;
import com.compomics.colims.core.io.SearchSettingsMapperTest;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapperTest;
import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.core.io.mzml.MzMLParserTest;
import com.compomics.colims.core.io.unimod.UnimodMarshallerTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPeptideMapperTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapperTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapperTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AuthorizationInterceptorTest.class,
        ColimsSpectrumMapperTest.class,
        UnimodMarshallerTest.class,
        UtilitiesSpectrumMapperTest.class,
        UtilitiesPeptideMapperTest.class,
        UtilitiesProteinMapperTest.class,
        UtilitiesSearchParametersMapperTest.class,
        UtilitiesSpectrumMapperTest.class,
        MzMLParserTest.class,
        SearchSettingsMapperTest.class,
        MaxQuantTestSuite.class
})
public class FastTests {
}
