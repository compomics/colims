/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_file")
@Entity
public class QuantificationFile extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "l_quantification_method_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationMethod quantificationMethod;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationFile")
    private List<QuantificationGroup> quantificationGroups = new ArrayList<>();

    public QuantificationMethod getQuantificationMethod() {
        return quantificationMethod;
    }

    public void setQuantificationMethod(QuantificationMethod quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    public List<QuantificationGroup> getQuantificationGroups() {
        return quantificationGroups;
    }

    public void setQuantificationGroups(List<QuantificationGroup> quantificationGroups) {
        this.quantificationGroups = quantificationGroups;
    }
}
