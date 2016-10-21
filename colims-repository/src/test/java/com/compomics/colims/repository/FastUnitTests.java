package com.compomics.colims.repository;

import com.compomics.colims.client.model.PeptideDTOTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all tests.
 * <p/>
 * Created by Niels Hulstaert on 20/05/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AuditableTypedCvParamRepositoryTest.class,
        ExperimentRepositoryTest.class,
        InstrumentRepositoryTest.class,
        ModificationRepositoryTest.class,
        PeptideRepositoryTest.class,
        ProjectRepositoryTest.class,
        ProteinGroupRepositoryTest.class,
        ProteinRepositoryTest.class,
        SearchEngineRepositoryTest.class,
        SearchModificationRepositoryTest.class,
        SearchParametersRepositoryTest.class,
        SpectrumRepositoryTest.class,
        TypedCvParamRepositoryTest.class,
        UserRepositoryTest.class,
        PeptideDTOTest.class,
        SampleRepositoryTest.class,
        FastaDbRepositoryTest.class
})
public class FastUnitTests {
}
