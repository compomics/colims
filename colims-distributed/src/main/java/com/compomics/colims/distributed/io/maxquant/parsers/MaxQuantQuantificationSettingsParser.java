/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.core.ontology.OntologyMapper;
import com.compomics.colims.core.ontology.OntologyTerm;
import com.compomics.colims.core.service.QuantificationReagentService;
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
    
    private static final String SILAC_LABEL = "SILAC";

    /**
     * The quantification settings indexed by analytical run (key: AnalyticalRun ; value: QuantificationSettings)
     */
    private final Map<AnalyticalRun, QuantificationSettings> runsAndQuantificationSettings = new HashMap<>();
    /**
     * The MaxQuant version.
     */
    private final String version = "N/A";

    @Autowired
    private OntologyMapper ontologyMapper;
    @Autowired
    private QuantificationSettingsService quantificationSettingsService;
    @Autowired
    private QuantificationReagentService quantificationReagentService;

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
     * @param quantificationLabel 
     * @param reagents 
     */
    public void parse(List<AnalyticalRun> analyticalRuns, String quantificationLabel, List<String> reagents){
        
        OntologyTerm ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationMethods().get(quantificationLabel);

        // create quantificationCvParam
        QuantificationMethodCvParam quantificationMethodCvParam =
                new QuantificationMethodCvParam(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel(), null);
        quantificationMethodCvParam.getQuantificationMethodHasReagents().addAll(createQuantificationReagent(quantificationMethodCvParam, quantificationLabel, reagents));
        // check if quantificationMethodCvParam is in the db
        quantificationMethodCvParam = quantificationSettingsService.getQuantificationMethodCvParams(quantificationMethodCvParam);
        // create quantificationSettings
        QuantificationSettings quantificationSettings = new QuantificationSettings();
        quantificationSettings.setQuantificationMethodCvParam(quantificationMethodCvParam);
        quantificationSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(QuantificationEngineType.MAXQUANT, version));
        analyticalRuns.forEach(analyticalRun -> {
            runsAndQuantificationSettings.put(analyticalRun, quantificationSettings);
        });
    }

    /**
     * This method is to create QuantificationReagent and its link to QuantificationMethodCvParam
     *
     * @param quantificationMethodCvParam
     * @param quantificationLabel
     * @param reagents
     * @return QuantificationMethodHasReagents list
     */
    public List<QuantificationMethodHasReagent> createQuantificationReagent(QuantificationMethodCvParam quantificationMethodCvParam, String quantificationLabel, List<String> reagents){
        List<QuantificationMethodHasReagent> quantificationMethodHasReagents = new ArrayList<>();

        reagents.forEach(reagent -> {
            OntologyTerm ontologyTerm = null;
            if(quantificationLabel.equals(SILAC_LABEL)){
                ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationReagents().get(reagent);
            }else{
                ontologyTerm = ontologyMapper.getMaxQuantMapping().getQuantificationReagents().get(reagent);
            }
            
            QuantificationMethodHasReagent quantificationMethodHasReagent = new QuantificationMethodHasReagent();
            if(ontologyTerm != null){
                QuantificationReagent quantificationReagent = 
                    new QuantificationReagent(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel(), null);
                // check if quantificationReagent is in the db
                quantificationReagent = quantificationReagentService.getQuantificationReagent(quantificationReagent);
                quantificationMethodHasReagent.setQuantificationReagent(quantificationReagent);
                quantificationMethodHasReagent.setQuantificationMethodCvParam(quantificationMethodCvParam);
                quantificationMethodHasReagents.add(quantificationMethodHasReagent);
            }
        });

        return quantificationMethodHasReagents;
    }
}
