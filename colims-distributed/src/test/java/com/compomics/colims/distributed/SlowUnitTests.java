package com.compomics.colims.distributed;

import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesModificationMapperTest;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesModificationProfileMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UtilitiesModificationMapperTest.class,
        UtilitiesModificationProfileMapperTest.class})
public class SlowUnitTests {
}
