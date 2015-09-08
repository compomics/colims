package com.compomics.colims.model;

import com.compomics.colims.model.enums.FragmentationType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a spectrum entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "spectrum")
@Entity
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Spectrum extends DatabaseEntity {

    private static final long serialVersionUID = -6581466869218920103L;

    /**
     * The spectrum accession.
     */
    @Basic(optional = false)
    @Column(name = "accession", length = 500, nullable = false)
    private String accession;
    /**
     * The spectrum title.
     */
    @Basic(optional = true)
    @Column(name = "title", length = 500, nullable = true)
    private String title;
    /**
     * The scan number.
     */
    @Basic(optional = false)
    @Column(name = "scan_number", nullable = false)
    private String scanNumber;
    /**
     * The precursor m/z value.
     */
    @Basic(optional = true)
    @Column(name = "mz_ratio", nullable = true)
    private Double mzRatio;
    /**
     * The precursor charge.
     */
    @Basic(optional = true)
    @Column(name = "charge", nullable = true)
    private Integer charge;
    /**
     * The scan time.
     */
    @Basic(optional = true)
    @Column(name = "scan_time", nullable = true)
    private Double scanTime;
    /**
     * The intensity value.
     */
    @Basic(optional = true)
    @Column(name = "intensity", nullable = true)
    private Double intensity;
    /**
     * The retention time value.
     */
    @Basic(optional = true)
    @Column(name = "retention_time", nullable = true)
    private Double retentionTime;
    /**
     * The fragmentation type.
     */
    @Basic(optional = true)
    @Column(name = "fragmentation_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private FragmentationType fragmentationType;
    /**
     * The analytical run that produced this spectrum.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @ManyToOne
    private AnalyticalRun analyticalRun;
    /**
     * The peptides that identify this spectrum.
     */
//    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "spectrum")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Peptide> peptides = new ArrayList<>();
    /**
     * The SpectrumFile instances linked to this spectrum.
     */
    @OneToMany(mappedBy = "spectrum")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<SpectrumFile> spectrumFiles = new ArrayList<>();

    /**
     * No-arg constructor.
     */
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
        return !((this.accession == null) ? (other.accession != null) : !this.accession.equals(other.accession));
    }

}
