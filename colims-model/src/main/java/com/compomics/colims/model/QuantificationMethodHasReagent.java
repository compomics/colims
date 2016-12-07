/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author demet
 */
@Table(name = "quantification_method_has_reagent")
@Entity
public class QuantificationMethodHasReagent extends DatabaseEntity{
    
    private static final long serialVersionUID = -8615906691573796821L;
    
    /**
     * The QuantificationMethodCvParam instance of this join entity.
     */
    @JoinColumn(name = "l_quantification_method_id", referencedColumnName = "id")
    @ManyToOne(cascade=CascadeType.ALL)
    private QuantificationMethod quantificationMethod;
    
    /**
     * The QuantificationMethodCvParam instance of this join entity.
     */
    @JoinColumn(name = "l_quantification_reagent_id", referencedColumnName = "id")
    @ManyToOne(cascade=CascadeType.MERGE)
    private QuantificationReagent quantificationReagent;

    public QuantificationMethodHasReagent() {
        
    }

    public QuantificationMethod getQuantificationMethod() {
        return quantificationMethod;
    }

    public void setQuantificationMethod(QuantificationMethod quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    public QuantificationReagent getQuantificationReagent() {
        return quantificationReagent;
    }

    public void setQuantificationReagent(QuantificationReagent quantificationReagent) {
        this.quantificationReagent = quantificationReagent;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.quantificationMethod);
        hash = 71 * hash + Objects.hashCode(this.quantificationReagent);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QuantificationMethodHasReagent other = (QuantificationMethodHasReagent) obj;
        if (!Objects.equals(this.quantificationMethod, other.quantificationMethod)) {
            return false;
        }
        if (!Objects.equals(this.quantificationReagent, other.quantificationReagent)) {
            return false;
        }
        return true;
    }
    
    
}
