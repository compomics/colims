/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "analytical_run")
@Entity
public class AnalyticalRun extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "fragmentation_type")
    @Enumerated(EnumType.STRING)    
    private FragmentationType fragmentationType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    @Basic(optional = true)
    protected Date startDate;
    @JoinColumn(name = "l_sample_id", referencedColumnName = "id")
    @ManyToOne
    private Sample sample;
    @JoinColumn(name = "l_instrument_id", referencedColumnName = "id")
    @ManyToOne
    private Instrument instrument;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "analyticalRun")
    private List<Spectrum> spectrums = new ArrayList<>();

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

    public FragmentationType getFragmentationType() {
        return fragmentationType;
    }

    public void setFragmentationType(FragmentationType fragmentationType) {
        this.fragmentationType = fragmentationType;
    }        

    public Date getStartDate() {
        return startDate != null ? new Date(startDate.getTime()) : null;
    }

    public void setStartDate(Date startDate) {
        this.startDate = new Date(startDate.getTime());
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public List<Spectrum> getSpectrums() {
        return spectrums;
    }

    public void setSpectrums(List<Spectrum> spectrums) {
        this.spectrums = spectrums;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final AnalyticalRun other = (AnalyticalRun) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
