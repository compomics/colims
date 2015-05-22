package com.compomics.colims.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({FastTests.class, SlowTests.class, UnstableTests.class})
public class AllTests {
}
