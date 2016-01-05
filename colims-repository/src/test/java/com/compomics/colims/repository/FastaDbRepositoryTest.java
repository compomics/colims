package com.compomics.colims.repository;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-repository-context.xml", "classpath:colims-repository-test-context.xml"})
@Transactional
@Rollback
public class FastaDbRepositoryTest {

    @Autowired
    private FastaDbRepository fastaDbRepository;

    @Test
    public void testFindByFastaDbType() {
        List<FastaDbType> fastaDbTypes = new ArrayList<>();
        fastaDbTypes.add(FastaDbType.PRIMARY);
        List<FastaDb> foundFastaDbs = fastaDbRepository.findByFastaDbType(fastaDbTypes);

        Assert.assertFalse(foundFastaDbs.isEmpty());
        Assert.assertEquals(1, foundFastaDbs.size());

        fastaDbTypes.add(FastaDbType.ADDITIONAL);
        foundFastaDbs = fastaDbRepository.findByFastaDbType(fastaDbTypes);

        Assert.assertFalse(foundFastaDbs.isEmpty());
        //size should still be one because it's the same FASTA type with different types
        Assert.assertEquals(1, foundFastaDbs.size());
    }

}