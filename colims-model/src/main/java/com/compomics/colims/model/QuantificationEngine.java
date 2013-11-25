/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_engine")
@Entity
public class QuantificationEngine extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationEngine")
    private List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines = new ArrayList<>();

    public List<QuantMethodHasQuantEngine> getQuantMethodHasQuantEngines() {
        return quantMethodHasQuantEngines;
    }

    public void setQuantMethodHasQuantEngines(List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines) {
        this.quantMethodHasQuantEngines = quantMethodHasQuantEngines;
    }
}
