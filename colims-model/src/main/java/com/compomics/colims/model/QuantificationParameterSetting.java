/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_parameter_setting")
@Entity
public class QuantificationParameterSetting extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationParamaterSetting")
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
