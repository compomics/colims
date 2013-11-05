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
import com.compomics.colims.core.mapper.impl.UtilitiesPeptideMapper;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.preferences.ModificationProfile;
import java.io.FileNotFoundException;
import org.junit.Before;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesProteinMapperTest {

    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;    
    
    @Test
    public void testMapPeptide() throws MappingException, IOException {
//        //create new utilities peptide
//        com.compomics.util.experiment.biology.Peptide sourcePeptide = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<String>(), new ArrayList<ModificationMatch>());        
//        Peptide targetPeptide = new Peptide();
//
//        utilitiesPeptideMapper.map(sourcePeptide, targetPeptide);
//
//        Assert.assertEquals(sourcePeptide.getSequence(), targetPeptide.getSequence());
//        Assert.assertEquals(sourcePeptide.getMass(), targetPeptide.getTheoreticalMass(), 0.001);        
    }
}
