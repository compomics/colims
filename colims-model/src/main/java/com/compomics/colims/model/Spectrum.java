/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.FragmentationType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "spectrum")
@Entity
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Spectrum extends DatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    private String accession;
    @Basic(optional = true)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @Column(name = "scan_number", nullable = false)
    private String scanNumber;
    @Basic(optional = false)
    @Column(name = "mz_ratio", nullable = false)
    private Double mzRatio;
    @Basic(optional = true)
    @Column(name = "charge", nullable = true)
    private Integer charge;
    @Basic(optional = true)
    @Column(name = "scan_time")
    private Double scanTime;
    @Column(name = "intensity")
    private Double intensity;
    @Column(name = "retention_time")
    private Double retentionTime;
    @Column(name = "fragmentation_type")
    @Enumerated(EnumType.STRING)
    private FragmentationType fragmentationType;
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @ManyToOne
    private AnalyticalRun analyticalRun;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "spectrum")
    private List<Peptide> peptides = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "spectrum")
    private List<SpectrumFile> spectrumFiles = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "spectrum")
    private List<Quantification> quantifications = new ArrayList<>();

    public Spectrum() {
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(String scanNumber) {
        this.scanNumber = scanNumber;
    }

    public Double getMzRatio() {
        return mzRatio;
    }

    public void setMzRatio(Double mzRatio) {
        this.mzRatio = mzRatio;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Double getScanTime() {
        return scanTime;
    }

    public void setScanTime(Double scanTime) {
        this.scanTime = scanTime;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public FragmentationType getFragmentationType() {
        return fragmentationType;
    }

    public void setFragmentationType(FragmentationType fragmentationType) {
        this.fragmentationType = fragmentationType;
    }

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }

    public void setAnalyticalRun(AnalyticalRun analyticalRun) {
        this.analyticalRun = analyticalRun;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }

    public List<SpectrumFile> getSpectrumFiles() {
        return spectrumFiles;
    }

    public void setSpectrumFiles(List<SpectrumFile> spectrumFiles) {
        this.spectrumFiles = spectrumFiles;
    }

    public List<Quantification> getQuantifications() {
        return quantifications;
    }

    public void setQuantifications(List<Quantification> quantifications) {
        this.quantifications = quantifications;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.accession != null ? this.accession.hashCode() : 0);
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
        final Spectrum other = (Spectrum) obj;
        if ((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession)) {
            return false;
        }
        return true;
    }
}
