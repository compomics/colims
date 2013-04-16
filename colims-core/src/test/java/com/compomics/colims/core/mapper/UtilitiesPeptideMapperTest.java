
package com.compomics.colims.core.mapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.IOManager;
import com.compomics.colims.core.io.mapper.UtilitiesPeptideMapper;
import com.compomics.colims.core.io.mapper.UtilitiesSpectrumMapper;
import com.compomics.colims.core.io.model.MascotGenericFile;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesPeptideMapperTest {
    
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    
    @Test
    public void testMapPeptide() throws MappingException, IOException{
        //create new utilities peptide
        com.compomics.util.experiment.biology.Peptide source = new com.compomics.util.experiment.biology.Peptide("KENNART", new ArrayList<String>(), new ArrayList<ModificationMatch>());        
        com.compomics.colims.model.Peptide target = new com.compomics.colims.model.Peptide();
        
        utilitiesPeptideMapper.map(source, target);
        
        Assert.assertEquals(source.getSequence(), target.getSequence());                                
    }

}
