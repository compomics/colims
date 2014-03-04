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
/**
 *
 * @author Kenneth Verheggen
 */
@Table(name = "quantification_group")
@Entity
public class QuantificationGroup extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;
   
    @JoinColumn(name = "l_quantification_file_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationFile quantificationFile;    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationGroup")
    private List<Quantification> quantifications = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationGroup")
    private List<QuantificationGroupHasPeptide> quantificationGroupHasPeptides = new ArrayList<>();
    
    
    public QuantificationFile getQuantificationFile() {
        return quantificationFile;
    }

    public void setQuantificationFile(QuantificationFile quantificationFile) {
        this.quantificationFile = quantificationFile;
    }

    public List<Quantification> getQuantifications() {
        return quantifications;
    }

    public void setQuantifications(List<Quantification> quantifications) {
        this.quantifications = quantifications;
    }

    public List<QuantificationGroupHasPeptide> getQuantificationGroupHasPeptides() {
        return quantificationGroupHasPeptides;
    }

    public void setQuantificationGroupHasPeptides(List<QuantificationGroupHasPeptide> quantificationGroupHasPeptides) {
        this.quantificationGroupHasPeptides = quantificationGroupHasPeptides;
    }
    
}
