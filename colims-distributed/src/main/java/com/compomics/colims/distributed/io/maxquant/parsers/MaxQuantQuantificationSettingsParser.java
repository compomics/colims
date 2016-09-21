/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.OntologyTerm;
import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationMethodCvParam;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.QuantificationReagent;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.enums.QuantificationEngineType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author demet
 */
@Component("maxQuantQuantificationSettingsParser")
public class MaxQuantQuantificationSettingsParser {
    
    /**
     * Logger instance.
     */
    private static Logger LOGGER = Logger.getLogger(MaxQuantSearchSettingsParser.class);
    /**
     * The quantification settings indexed by analytical run (key: AnalyticalRun ; value: QuantificationSettings)
     */
    private Map<AnalyticalRun, QuantificationSettings> runsAndQuantificationSettings = new HashMap<>();
    /**
     * The MaxQuant version.
     */
    private String version = "N/A";
    
    @Autowired
    private OntologyMapper ontologyMapper;
    @Autowired
    private QuantificationSettingsService quantificationSettingsService;
    
    /**
     * Get map of analytical run and quantification settings 
     * @return runsAndQuantificationSettings
     */
    public Map<AnalyticalRun, QuantificationSettings> getRunsAndQuantificationSettings() {
        return runsAndQuantificationSettings;
    }
    /**
     * Parse the quantification parameters for a MaxQuant experiment
     * @param analyticalRuns
     * @param experimentLabel 
     * @param reagents 
     */
    public void parse(List<AnalyticalRun> analyticalRuns, String experimentLabel, List<String> reagents){
        
        OntologyTerm ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationMethods().get(experimentLabel);
        // create quantificationCvParam
        QuantificationMethodCvParam quantificationMethodCvParam = 
                new QuantificationMethodCvParam(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel(), null);
        quantificationMethodCvParam.getQuantificationMethodHasReagents().addAll(createQuantificationReagent(quantificationMethodCvParam, reagents));
        quantificationMethodCvParam = quantificationSettingsService.getQuantificationMethodCvParams(quantificationMethodCvParam);
        // create quantificationSettings
        QuantificationSettings quantificationSettings = new QuantificationSettings();
        quantificationSettings.setQuantificationMethodCvParam(quantificationMethodCvParam);
        quantificationSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(QuantificationEngineType.MAX_QUANT, version));
        analyticalRuns.forEach(analyticalRun -> {
            runsAndQuantificationSettings.put(analyticalRun, quantificationSettings);
        });
    }
    
    /**
     * This method is to create QuantificationReagent and its link to QuantificationMethodCvParam
     * 
     * @param quantificationMethodCvParam
     * @param reagents
     * @return QuantificationMethodHasReagents list
     */
    public List<QuantificationMethodHasReagent> createQuantificationReagent(QuantificationMethodCvParam quantificationMethodCvParam, List<String> reagents){
        List<QuantificationMethodHasReagent> quantificationMethodHasReagents = new ArrayList<>();
        
        reagents.forEach(reagent -> {
            OntologyTerm ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationReagents().get(reagent);
            
            QuantificationMethodHasReagent quantificationMethodHasReagent = new QuantificationMethodHasReagent();
            
            QuantificationReagent quantificationReagent = 
                    new QuantificationReagent(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel(), null);
            
            quantificationMethodHasReagent.setQuantificationReagent(quantificationReagent);
            quantificationMethodHasReagent.setQuantificationMethodCvParam(quantificationMethodCvParam);
            quantificationMethodHasReagents.add(quantificationMethodHasReagent);
        });
        
        return quantificationMethodHasReagents;
    }
}
