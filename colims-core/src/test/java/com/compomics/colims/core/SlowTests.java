package com.compomics.colims.core;

import com.compomics.colims.core.io.peptideshaker.PeptideShakerIOTest;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImporterTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesModificationMapperTest;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesModificationProfileMapperTest;
import com.compomics.colims.core.service.OlsServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UtilitiesModificationMapperTest.class,
        UtilitiesModificationProfileMapperTest.class,
        OlsServiceTest.class,
        PeptideShakerImporterTest.class,
        PeptideShakerIOTest.class})
public class SlowTests {
}
