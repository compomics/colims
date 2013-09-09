
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
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.CvTermProperty;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CvTermServiceTest {
    
    @Autowired
    private CvTermService cvTermService;
    
    @Test
    public void testFindByCvTermByProperty() {         
        List<InstrumentCvTerm> sources = cvTermService.findByCvTermByProperty(InstrumentCvTerm.class, CvTermProperty.SOURCE);
        
        Assert.assertNotNull(sources);
        Assert.assertEquals(2, sources.size());
    }
    
}
