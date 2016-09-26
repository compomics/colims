package com.compomics.colims.distributed;

import com.compomics.colims.distributed.io.ModificationMapperTest;
import com.compomics.colims.distributed.io.SearchModificationMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all slow unit tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ModificationMapperTest.class,
        SearchModificationMapperTest.class})
public class SlowUnitTests {
}
