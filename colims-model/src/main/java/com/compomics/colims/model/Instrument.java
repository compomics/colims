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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument")
@Entity
public class Instrument extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument name")
    @Length(min = 3, max = 30, message = "Name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Basic(optional = false)
    @NotNull(message = "An instrument must have a type")
    @ManyToOne
    @JoinColumn(name = "l_type_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvTerm type;
    @Basic(optional = false)
    @NotNull(message = "An instrument must have a source")
    @ManyToOne
    @JoinColumn(name = "l_source_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvTerm source;
    @Basic(optional = false)
    @NotNull(message = "An instrument must have a detector")
    @ManyToOne
    @JoinColumn(name = "l_detector_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvTerm detector;
    @OneToMany(mappedBy = "instrument")
    private List<AnalyticalRun> analyticalRuns = new ArrayList<>();
    @NotEmpty(message = "An instrument must have at least one analyzer")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstrumentCvTerm getType() {
        return type;
    }

    public void setType(InstrumentCvTerm type) {
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
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + Objects.hashCode(this.source);
        hash = 67 * hash + Objects.hashCode(this.detector);
        hash = 67 * hash + Objects.hashCode(this.analyzers);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.detector, other.detector)) {
            return false;
        }
        if (!Objects.equals(this.analyzers, other.analyzers)) {
            return false;
        }
        return true;
    }    

    @Override
    public String toString() {
        return name + " [" + type.getName() + "]";
    }
}
