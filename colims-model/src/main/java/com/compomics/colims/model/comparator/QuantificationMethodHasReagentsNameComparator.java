/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model.comparator;

import com.compomics.colims.model.QuantificationMethodHasReagent;
import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares the reagents names of QuantificationMethodHasReagent entity instances.
 * 
 * @author demet
 */
public class QuantificationMethodHasReagentsNameComparator implements Comparator<QuantificationMethodHasReagent>, Serializable{

    private static final long serialVersionUID = -8380720439063629719L;

    @Override
    public int compare(QuantificationMethodHasReagent o1, QuantificationMethodHasReagent o2) {
        return o1.getQuantificationReagent().getName().compareTo(o2.getQuantificationReagent().getName());
    }
    
}
