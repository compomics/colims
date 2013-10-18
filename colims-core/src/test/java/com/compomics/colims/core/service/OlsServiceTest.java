
package com.compomics.colims.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.CvTermType;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OlsServiceTest {
    
    @Autowired
    private OlsService olsService;
    
    @Test
    public void testFindModificationByAccession() {         
        //try to find a non existing modification
        Modification modification  = olsService.findModifiationByAccession("MOD:00935999");
        
        Assert.assertNull(modification);
        
        modification  = olsService.findModifiationByAccession("MOD:00935");
        
        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00935", modification.getAccession());
        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
        Assert.assertEquals(83.037114, modification.getMonoIsotopicMass(), 0.001);
        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
        Assert.assertEquals(83.09, modification.getAverageMass(), 0.001);
        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
    }
    
    @Test
    public void testFindModificationByExactName() {         
        //try to find a non existing modification
        Modification modification  = olsService.findModifiationByExactName("non existing modification");
        
        Assert.assertNull(modification);
        
        modification  = olsService.findModifiationByExactName("methionine oxidation with neutral loss of 64 Da");
        
        Assert.assertNotNull(modification);
        Assert.assertEquals("MOD:00935", modification.getAccession());
        Assert.assertEquals("methionine oxidation with neutral loss of 64 Da", modification.getName());
        Assert.assertEquals(83.037114, modification.getMonoIsotopicMass(), 0.001);
        Assert.assertEquals(-63.998286, modification.getMonoIsotopicMassShift(), 0.001);
        Assert.assertEquals(83.09, modification.getAverageMass(), 0.001);
        Assert.assertEquals(-64.1, modification.getAverageMassShift(), 0.001);
    }
    
    @Test
    public void testFindModificationByName() {         
        //try to find a non existing modification
        List<Modification> modifications  = olsService.findModifiationByName("non existing modification");        
        Assert.assertTrue(modifications.isEmpty());
        
        modifications  = olsService.findModifiationByName("oxidation of m");        
        Assert.assertEquals(3, modifications.size());
    }
    
}
