package com.compomics.colims.model;

import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.colims.model.util.CompareUtils;

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
public class Spectrum extends DatabaseEntity {

    private static final long serialVersionUID = -6581466869218920103L;

    /**
     * The default accession for a matching-between-runs dummy spectrum.
     */
    public static final String MBR_SPECTRUM_ACCESSION = "MBR";

    /**
     * The spectrum accession.
     */
    @Basic(optional = false)
    @Column(name = "accession", length = 500, nullable = false)
    private String accession;
    /**
     * The spectrum title. This should be the same as the MGF TITLE header value in the associated {@link SpectrumFile}
     * content.
     */
    @Basic(optional = true)
    @Column(name = "title", length = 500, nullable = true)
    private String title;
    /**
     * The scan number.
     */
    @Basic(optional = true)
    @Column(name = "scan_number", nullable = true)
    private Long scanNumber;
    /**
     * The scan index.
     */
    @Basic(optional = true)
    @Column(name = "scan_index", nullable = true)
    private Long scanIndex;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private AnalyticalRun analyticalRun;
    /**
     * The peptides that identify this spectrum.
     */
    @OneToMany(mappedBy = "spectrum", cascade = CascadeType.ALL)
    private List<Peptide> peptides = new ArrayList<>();
    /**
     * The SpectrumFile instances linked to this spectrum.
     */
    @OneToMany(mappedBy = "spectrum", cascade = CascadeType.ALL)
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

    public Long getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(Long scanNumber) {
        this.scanNumber = scanNumber;
    }

    public Long getScanIndex() {
        return scanIndex;
    }

    public void setScanIndex(Long scanIndex) {
        this.scanIndex = scanIndex;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Spectrum spectrum = (Spectrum) o;

        if (!accession.equals(spectrum.accession)) {
            return false;
        }
        if (title != null ? !title.equals(spectrum.title) : spectrum.title != null) {
            return false;
        }
        if (scanNumber != null ? !scanNumber.equals(spectrum.scanNumber) : spectrum.scanNumber != null) {
            return false;
        }
        if (scanIndex != null ? !scanIndex.equals(spectrum.scanIndex) : spectrum.scanIndex != null) {
            return true;
        }
        if (mzRatio != null ? !CompareUtils.equals(mzRatio, spectrum.mzRatio) : spectrum.mzRatio != null) {
            return false;
        }
        if (charge != null ? !charge.equals(spectrum.charge) : spectrum.charge != null) {
            return false;
        }
        if (scanTime != null ? !CompareUtils.equals(scanTime, spectrum.scanTime) : spectrum.scanTime != null) {
            return false;
        }
        if (intensity != null ? !CompareUtils.equals(intensity, spectrum.intensity) : spectrum.intensity != null) {
            return false;
        }
        if (retentionTime != null ? !CompareUtils.equals(retentionTime, spectrum.retentionTime) : spectrum.retentionTime != null) {
            return false;
        }
        return fragmentationType == spectrum.fragmentationType;

    }

    @Override
    public int hashCode() {
        int result = accession.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (scanNumber != null ? scanNumber.hashCode() : 0);
        result = 31 * result + (scanIndex != null ? scanIndex.hashCode() : 0);
        result = 31 * result + (mzRatio != null ? mzRatio.hashCode() : 0);
        result = 31 * result + (charge != null ? charge.hashCode() : 0);
        result = 31 * result + (scanTime != null ? scanTime.hashCode() : 0);
        result = 31 * result + (intensity != null ? intensity.hashCode() : 0);
        result = 31 * result + (retentionTime != null ? retentionTime.hashCode() : 0);
        result = 31 * result + (fragmentationType != null ? fragmentationType.hashCode() : 0);
        return result;
    }
}
