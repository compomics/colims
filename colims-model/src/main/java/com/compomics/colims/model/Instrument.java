/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument")
@Entity
public class Instrument extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument name")
    @Length(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument type")
    @Length(min = 2, max = 30, message = "Type must be between 2 and 30 characters")
    @Column(name = "type")
    private String type;    
    @ManyToOne
    @JoinColumn(name = "l_source_cv_id", referencedColumnName = "id")    
    private InstrumentCvTerm source;
    @ManyToOne
    @JoinColumn(name = "l_detector_cv_id", referencedColumnName = "id")    
    private InstrumentCvTerm detector;
    @OneToMany(mappedBy = "instrument")
    private List<AnalyticalRun> analyticalRuns = new ArrayList<>();    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "instrument_has_analyzer",
            joinColumns = {
        @JoinColumn(name = "l_instrument_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_instrument_cv_term_id", referencedColumnName = "id")})
    private List<InstrumentCvTerm> analyzers = new ArrayList<>();

    public Instrument() {
    }

    public Instrument(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }    

    public InstrumentCvTerm getSource() {
        return source;
    }

    public void setSource(InstrumentCvTerm source) {
        this.source = source;
    }

    public InstrumentCvTerm getDetector() {
        return detector;
    }

    public void setDetector(InstrumentCvTerm detector) {
        this.detector = detector;
    }        

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
    }

    public List<InstrumentCvTerm> getAnalyzers() {
        return analyzers;
    }

    public void setAnalyzers(List<InstrumentCvTerm> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " [" + type + "]";
    }
        
}
