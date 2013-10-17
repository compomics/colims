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
import com.compomics.colims.model.Peptide;
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
    public void testMapPeptide() throws MappingException, IOException {
        //create new utilities peptide
        com.compomics.util.experiment.biology.Peptide source = new com.compomics.util.experiment.biology.Peptide("YKENNAMRT", new ArrayList<String>(), new ArrayList<ModificationMatch>());
        //add modifications       
        ModificationMatch oxidation = new ModificationMatch("oxidation of m", true, 7);
        source.addModificationMatch(oxidation);
        ModificationMatch phosphorylation = new ModificationMatch("phosphorylation of y", true, 1);
        source.addModificationMatch(phosphorylation);

        //create new colims entity peptide
        com.compomics.colims.model.Peptide target = new Peptide();

        utilitiesPeptideMapper.map(source, target);

        Assert.assertEquals(source.getSequence(), target.getSequence());
        Assert.assertEquals(source.getMass(), target.getTheoreticalMass(), 0.001);
    }
}
