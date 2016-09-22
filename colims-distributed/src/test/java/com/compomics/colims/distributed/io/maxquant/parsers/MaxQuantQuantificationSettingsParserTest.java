package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationMethodCvParam;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author demet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:colims-distributed-context.xml", "classpath:colims-distributed-test-context.xml"})
public class MaxQuantQuantificationSettingsParserTest {
    
    @Autowired
    MaxQuantQuantificationSettingsParser maxQuantQuantificationSettingsParser;
    
    @Test
    public void testParse(){
        List<AnalyticalRun> analyticalRuns =  new ArrayList<>();
        AnalyticalRun analyticalRun1 = new AnalyticalRun();
        analyticalRun1.setName("Run1");
        AnalyticalRun analyticalRun2 = new AnalyticalRun();
        analyticalRun2.setName("Run2");
        analyticalRuns.add(analyticalRun1);
        analyticalRuns.add(analyticalRun2);
        String experimentLabel = "TMT";
        List<String> reagents = new ArrayList<>();
        reagents.add("TMT reagent 126");
        
        maxQuantQuantificationSettingsParser.parse(analyticalRuns, experimentLabel, reagents);
        
        assertThat(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().size(), is(2));
        assertThat(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().get(analyticalRun1).getQuantificationMethodCvParam().getAccession(), is("PRIDE:0000314"));
        assertThat(maxQuantQuantificationSettingsParser.getRunsAndQuantificationSettings().get(analyticalRun1).getQuantificationMethodCvParam().getQuantificationMethodHasReagents().
                get(0).getQuantificationReagent().getAccession(), is("PRIDE:0000285"));
        
    }

    @Test
    public void testCreateQuantificationReagent(){
        QuantificationMethodCvParam quantificationMethodCvParam = new QuantificationMethodCvParam("PRIDE", "PRIDE:0000315", "SILAC", null);
        String experimentLabel = "TMT";
        List<String> reagents = new ArrayList<>();
        reagents.add("SILAC heavy");
        reagents.add("SILAC light");
        
        List<QuantificationMethodHasReagent> quantificationMethodHasReagents = maxQuantQuantificationSettingsParser.createQuantificationReagent(quantificationMethodCvParam, experimentLabel, reagents);
        
        assertThat(quantificationMethodHasReagents.size(), is(2));
        assertThat(quantificationMethodHasReagents.get(0).getQuantificationReagent().getLabel(), is("PRIDE"));
        assertThat(quantificationMethodHasReagents.get(0).getQuantificationMethodCvParam().getAccession(), is("PRIDE:0000315"));
    }
}
