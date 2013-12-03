/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.mapper.Mapper;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.quantification.quantification.QuantificationHit;
import com.compomics.util.experiment.quantification.quantification.QuantificationModel;
import com.compomics.util.experiment.quantification.quantification.QuantificationWeight;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("utilitiesQuantificationMapper")
@Transactional
public class UtilitiesQuantificationGroupMapper implements Mapper<QuantificationModel, QuantificationGroup> {

    @Override
    public void map(QuantificationModel source, QuantificationGroup target) throws MappingException {
        HashMap<QuantificationWeight, QuantificationHit> utilitiesQuantificationsMap = source.getQuantifications();
        ArrayList<Quantification> colimsQuantificationsList = new ArrayList<Quantification>();
        UtilitiesQuantificationMapper mapper = new UtilitiesQuantificationMapper();
        //TODO FIND WHAT FRAGMENTATIONTYPE SHOULD BE USED, using the technique maybe!?
        FragmentationType fragType = FragmentationType.CID;
        for (QuantificationWeight aWeight : utilitiesQuantificationsMap.keySet()) {
            Quantification colimsQuantification = new Quantification();
            mapper.map(utilitiesQuantificationsMap.get(aWeight), fragType, colimsQuantification);
            colimsQuantificationsList.add(colimsQuantification);
        }
        target.setQuantifications(colimsQuantificationsList);
    }
}
