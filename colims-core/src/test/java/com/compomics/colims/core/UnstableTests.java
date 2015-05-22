package com.compomics.colims.core;

import com.compomics.colims.core.io.maxquant.MaxQuantSpectrumParserTest;
import com.compomics.colims.core.io.maxquant.MaxQuantTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MaxQuantSpectrumParserTest.class,
        MaxQuantTestSuite.class
})
public class UnstableTests {
}
