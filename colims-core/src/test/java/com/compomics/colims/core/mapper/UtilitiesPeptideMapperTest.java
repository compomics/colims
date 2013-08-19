
package com.compomics.colims.core.mapper;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.peptideshaker.mapper.UtilitiesPeptideMapper;
import com.compomics.util.experiment.identification.matches.ModificationMatch;

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
