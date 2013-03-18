/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
//    @JoinColumn(name = "l_replicateid", referencedColumnName = "replicateid")
//    @ManyToOne
//    private Replicate replicate;
    @Basic(optional = false)
    @Column(name = "accession")
    private String accession;
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

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public Date getStartDate() {
        return startDate != null? new Date(startDate.getTime()) : null;
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

//    public Replicate getReplicate() {
//        return replicate;
//    }
//
//    public void setReplicate(Replicate replicate) {
//        this.replicate = replicate;
//    }
    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.accession != null ? this.accession.hashCode() : 0);
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
        if ((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return accession;
    }
}
