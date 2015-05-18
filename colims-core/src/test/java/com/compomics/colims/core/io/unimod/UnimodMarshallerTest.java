package com.compomics.colims.core.io.unimod;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.maxquant.MaxQuantSpectrumParser;
import com.compomics.colims.core.io.maxquant.UnparseableException;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import org.jdom2.JDOMException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UnimodMarshallerTest {

    private static final String MOD_NAME = "DTT_C";

    @Autowired
    private UnimodMarshaller unimodMarshaller;

    /**
     * Test the marshalling of the unimod.xml file.
     *
     * @throws JDOMException top level exception that can be thrown in case of a
     * problem in the JDOM classes.
     */
    @Test
    public void testMarshall() throws JDOMException {
        Assert.assertFalse(unimodMarshaller.getModifications().isEmpty());
    }

    /**
     * Test the retrieval of a modification from the marshaller of both
     * Modification and SearchModification instances.
     */
    @Test
    public void testGetModificationByName() {
        SearchModification searchModification = unimodMarshaller.getModificationByName(SearchModification.class, MOD_NAME);
        Assert.assertNotNull(searchModification);

        Modification modification = unimodMarshaller.getModificationByName(Modification.class, MOD_NAME);
        Assert.assertNotNull(modification);
    }

}
