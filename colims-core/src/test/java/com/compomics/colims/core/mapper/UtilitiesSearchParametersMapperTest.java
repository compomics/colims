/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.mapper;

import com.compomics.colims.core.io.peptideshaker.PeptideShakerIO;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.mapper.impl.UtilitiesSearchParametersMapper;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.util.experiment.identification.SearchParameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Kenneth Verheggen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-core-context.xml", "classpath:colims-core-test-context.xml"})
public class UtilitiesSearchParametersMapperTest {


    @Autowired
    private PeptideShakerIO peptideShakerIO;

    /**
     * Test of map method, of class UtilitiesSearchParametersMapper.
     */
    @Test
    public void testMap() throws Exception {
        System.out.println("map");
        PeptideShakerImport source = peptideShakerIO.unpackPeptideShakerCpsArchive(new ClassPathResource("test_peptideshaker_project_2.cps").getFile());
        SearchParameterSettings target = new SearchParameterSettings();
        UtilitiesSearchParametersMapper instance = new UtilitiesSearchParametersMapper();
        instance.map(source, target);
        Assert.assertEquals("trypsin", target.getEnzyme().toLowerCase());
        Assert.assertEquals(2, target.getMaxMissedCleavages());
        Assert.assertEquals(10.0, target.getPrecMassTolerance(),0.001);
        Assert.assertEquals(0.5, target.getFragMassTolerance(),0.001);
        Assert.assertEquals(2, target.getPrecursorLowerCharge());
        Assert.assertEquals(4, target.getPrecursorUpperCharge());
        Assert.assertEquals(100.0, target.getEvalueCutoff(),0.001);
        Assert.assertEquals(25, target.getHitlistLength());
        Assert.assertEquals("uniprot_sprot_101104_human_concat.fasta", target.getFastaDb().getFileName());
    }
}
