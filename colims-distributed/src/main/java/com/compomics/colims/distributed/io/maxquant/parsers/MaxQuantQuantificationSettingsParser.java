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
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.model.enums.QuantificationMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author demet
 */
@Component("maxQuantQuantificationSettingsParser")
public class MaxQuantQuantificationSettingsParser {

    /**
     * The quantification settings indexed by analytical run (key: AnalyticalRun ; value: QuantificationSettings)
     */
    private final Map<AnalyticalRun, QuantificationSettings> runsAndQuantificationSettings = new HashMap<>();
    /**
     * Beans.
     */
    private final OntologyMapper ontologyMapper;
    private final QuantificationSettingsService quantificationSettingsService;
    private final QuantificationReagentService quantificationReagentService;
    private final MaxQuantSearchSettingsParser maxQuantSearchSettingsParser;

    @Autowired
    public MaxQuantQuantificationSettingsParser(QuantificationSettingsService quantificationSettingsService, OntologyMapper ontologyMapper,
                                                QuantificationReagentService quantificationReagentService, MaxQuantSearchSettingsParser maxQuantSearchSettingsParser) {
        this.quantificationSettingsService = quantificationSettingsService;
        this.ontologyMapper = ontologyMapper;
        this.quantificationReagentService = quantificationReagentService;
        this.maxQuantSearchSettingsParser = maxQuantSearchSettingsParser;
    }

    /**
     * Get map of analytical run and quantification settings
     *
     * @return runsAndQuantificationSettings
     */
    public Map<AnalyticalRun, QuantificationSettings> getRunsAndQuantificationSettings() {
        return runsAndQuantificationSettings;
    }

    /**
     * Clear resources.
     */
    public void clear() {
        runsAndQuantificationSettings.clear();
    }

    /**
     * Parse the quantification parameters for a MaxQuant experiment.
     *
     * @param analyticalRuns     the list of analytical runs
     * @param quantificationType the quantification type
     * @param reagents           the list of reagents
     */
    public void parse(List<AnalyticalRun> analyticalRuns, QuantificationMethod quantificationType, List<String> reagents) {
        OntologyTerm ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationMethods().get(quantificationType.toString());

        //create the quantification method
        com.compomics.colims.model.QuantificationMethod quantificationMethod =
                new com.compomics.colims.model.QuantificationMethod(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel());
        quantificationMethod.getQuantificationMethodHasReagents().addAll(createQuantificationReagent(quantificationMethod, quantificationType, reagents));
        //check if the quantification method is already present in the db
        quantificationMethod = quantificationSettingsService.getQuantificationMethod(quantificationMethod);
        //create the quantification settings
        QuantificationSettings quantificationSettings = new QuantificationSettings();
        quantificationSettings.setQuantificationMethod(quantificationMethod);
        quantificationSettings.setQuantificationEngine(quantificationSettingsService.getQuantificationEngine(QuantificationEngineType.MAXQUANT, maxQuantSearchSettingsParser.getVersion()));
        analyticalRuns.forEach(analyticalRun -> {
            QuantificationSettings quantSettings = new QuantificationSettings();
            quantSettings.setAnalyticalRun(analyticalRun);
            quantSettings.setQuantificationMethod(quantificationSettings.getQuantificationMethod());
            quantSettings.setQuantificationEngine(quantificationSettings.getQuantificationEngine());
            runsAndQuantificationSettings.put(analyticalRun, quantSettings);
        });
    }

    /**
     * This method creates {@link QuantificationReagent} instances and their link to {@link com.compomics.colims.model.QuantificationMethod} instances.
     *
     * @param quantificationMethod the {@link com.compomics.colims.model.QuantificationMethod} instance
     * @param quantificationType   the quantification type
     * @param reagents             the list of reagents
     * @return QuantificationMethodHasReagents list
     */
    public List<QuantificationMethodHasReagent> createQuantificationReagent(com.compomics.colims.model.QuantificationMethod quantificationMethod, QuantificationMethod quantificationType, List<String> reagents) {
        List<QuantificationMethodHasReagent> quantificationMethodHasReagents = new ArrayList<>();

        reagents.forEach(reagent -> {
            OntologyTerm ontologyTerm;
            if (quantificationType.equals(QuantificationMethod.SILAC) || quantificationType.equals(QuantificationMethod.ICAT)) {
                ontologyTerm = ontologyMapper.getColimsMapping().getQuantificationReagents().get(reagent);
            } else {
                ontologyTerm = ontologyMapper.getMaxQuantMapping().getQuantificationReagents().get(reagent);
            }

            QuantificationMethodHasReagent quantificationMethodHasReagent = new QuantificationMethodHasReagent();
            if (ontologyTerm != null) {
                QuantificationReagent quantificationReagent =
                        new QuantificationReagent(ontologyTerm.getOntologyPrefix(), ontologyTerm.getOboId(), ontologyTerm.getLabel());

                //check if the quantification reagent is already present in the database
                quantificationReagent = quantificationReagentService.getQuantificationReagent(quantificationReagent);

                quantificationMethodHasReagent.setQuantificationReagent(quantificationReagent);
                quantificationMethodHasReagent.setQuantificationMethod(quantificationMethod);
                quantificationMethodHasReagents.add(quantificationMethodHasReagent);
            }
        });

        return quantificationMethodHasReagents;
    }

}
