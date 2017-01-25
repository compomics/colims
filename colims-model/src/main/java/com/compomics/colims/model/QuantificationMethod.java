/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.cv.CvParam;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.FetchType;

/**
 * This class represents a quantification method cv parameters entity in the database.
 *
 * @author demet
 */
@Table(name = "quantification_method")
@Entity
public class QuantificationMethod extends CvParam {

    private static final long serialVersionUID = 8852434545673934041L;

    /**
     * The QuantificationMethodHasReagent instances from the join table between the QuantificationMethodCvParam and
     * QuantificationReagent.
     */
    @OneToMany(mappedBy = "quantificationMethod", cascade = CascadeType.ALL)
    private List<QuantificationMethodHasReagent> quantificationMethodHasReagents = new ArrayList<>();

    public QuantificationMethod() {
    }

    public QuantificationMethod(final String label, final String accession, final String name) {
        super(label, accession, name);
    }

    public List<QuantificationMethodHasReagent> getQuantificationMethodHasReagents() {
        return quantificationMethodHasReagents;
    }

    public void setQuantificationMethodHasReagents(List<QuantificationMethodHasReagent> quantificationMethodHasReagents) {
        this.quantificationMethodHasReagents = quantificationMethodHasReagents;
    }


}
