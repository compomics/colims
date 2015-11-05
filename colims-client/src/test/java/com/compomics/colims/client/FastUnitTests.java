package com.compomics.colims.client;

import com.compomics.colims.client.controller.UserQueryPanelControllerTest;
import com.compomics.colims.client.model.PeptideTableRowTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all fast unit tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UserQueryPanelControllerTest.class,
        PeptideTableRowTest.class
})
public class FastUnitTests {
}
