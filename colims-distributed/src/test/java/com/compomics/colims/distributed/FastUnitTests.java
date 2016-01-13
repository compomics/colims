package com.compomics.colims.distributed;

import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSearchSettingsMapperTest;
import com.compomics.colims.distributed.io.maxquant.MaxQuantTestSuite;
import com.compomics.colims.distributed.io.unimod.UnimodMarshallerTest;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesPeptideMapperTest;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesProteinMapperTest;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSearchParametersMapperTest;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSpectrumMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all fast unit tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UtilitiesSpectrumMapperTest.class,
        UtilitiesPeptideMapperTest.class,
        UtilitiesProteinMapperTest.class,
        UtilitiesSearchParametersMapperTest.class,
        UtilitiesSpectrumMapperTest.class,
        UtilitiesSearchSettingsMapperTest.class,
        UnimodMarshallerTest.class,
        MaxQuantTestSuite.class
})
public class FastUnitTests {
}
