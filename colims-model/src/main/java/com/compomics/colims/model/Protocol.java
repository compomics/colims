/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol")
@Entity
public class Protocol extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a protocol name")
    @Length(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    @Column(name = "name")
    private String name;   
    @ManyToOne
    @JoinColumn(name = "l_reduction_cv_id", referencedColumnName = "id")    
    private ProtocolCvTerm reduction;
    @ManyToOne
    @JoinColumn(name = "l_enzyme_cv_id", referencedColumnName = "id")    
    private ProtocolCvTerm enzyme;    
    @ManyToOne
    @JoinColumn(name = "l_cell_based_cv_id", referencedColumnName = "id")    
    private ProtocolCvTerm cellBased;    
    @OneToMany(mappedBy = "protocol")
    private List<Sample> samples = new ArrayList<>();       
    @ManyToMany
    @JoinTable(name = "protocol_has_chemical_labeling",
            joinColumns = {
        @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_chemical_labeling_cv_term_id", referencedColumnName = "id")})
    private List<InstrumentCvTerm> analyzers = new ArrayList<>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return name;
    }

    public void setType(String type) {
        this.name = type;
    }    

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtocolCvTerm getReduction() {
        return reduction;
    }

    public void setReduction(ProtocolCvTerm reduction) {
        this.reduction = reduction;
    }

    public ProtocolCvTerm getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(ProtocolCvTerm enzyme) {
        this.enzyme = enzyme;
    }

    public ProtocolCvTerm getCellBased() {
        return cellBased;
    }

    public void setCellBased(ProtocolCvTerm cellBased) {
        this.cellBased = cellBased;
    }

    public List<InstrumentCvTerm> getAnalyzers() {
        return analyzers;
    }

    public void setAnalyzers(List<InstrumentCvTerm> analyzers) {
        this.analyzers = analyzers;
    }        
        
}
