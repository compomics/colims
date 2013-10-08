/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationEngine")
    private List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QuantMethodHasQuantEngine> getQuantMethodHasQuantEngines() {
        return quantMethodHasQuantEngines;
    }

    public void setQuantMethodHasQuantEngines(List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines) {
        this.quantMethodHasQuantEngines = quantMethodHasQuantEngines;
    }
}
