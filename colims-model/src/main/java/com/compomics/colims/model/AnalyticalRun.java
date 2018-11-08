package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents an analytical run entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "analytical_run")
@Entity
public class AnalyticalRun extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 8278042140876711715L;

    /**
     * The run name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an analytical run name.")
    @Length(min = 1, max = 100, message = "Name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The start date and time.
     */
    @Basic(optional = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = true)
    private Date startDate;
    /**
     * The sample the run belongs to.
     */
    @JoinColumn(name = "l_sample_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Sample sample;
    /**
     * The instrument the run was executed on.
     */
    @JoinColumn(name = "l_instrument_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Instrument instrument;
    /**
     * The storage location of the run, for example the file path of the
     * imported data. This is a free text field.
     */
    @Basic(optional = true)
    @Column(name = "storage_location", nullable = true)
    private String storageLocation;
    /**
     * The spectra of this AnalyticalRun instance.
     */
    @OneToMany(mappedBy = "analyticalRun", cascade = CascadeType.REMOVE)
    private List<Spectrum> spectrums = new ArrayList<>();
    /**
     * The analytical run attachments. These are stored as lob's in the
     * database.
     */
    @OneToMany(mappedBy = "analyticalRun", cascade = CascadeType.ALL, orphanRemoval = true)
    List<AnalyticalRunBinaryFile> binaryFiles = new ArrayList<>();
    /**
     * The search and validation settings for this run.
     */
    @OneToOne(mappedBy = "analyticalRun", cascade = CascadeType.ALL)
    SearchAndValidationSettings searchAndValidationSettings;
    /**
     * The quantification settings for this run.
     */
    @OneToOne(mappedBy = "analyticalRun", cascade = CascadeType.ALL)
    QuantificationSettings quantificationSettings;
    /**
     * The protein quantification for this run.
     */
    @OneToMany(mappedBy = "analyticalRun", cascade = CascadeType.REMOVE)
    List<ProteinGroupQuant> proteinGroupQuants = new ArrayList<>();
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public List<Spectrum> getSpectrums() {
        return spectrums;
    }

    public void setSpectrums(List<Spectrum> spectrums) {
        this.spectrums = spectrums;
    }

    public List<AnalyticalRunBinaryFile> getBinaryFiles() {
        return binaryFiles;
    }

    public void setBinaryFiles(List<AnalyticalRunBinaryFile> binaryFiles) {
        this.binaryFiles = binaryFiles;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public QuantificationSettings getQuantificationSettings() {
        return quantificationSettings;
    }

    public void setQuantificationSettings(QuantificationSettings quantificationSettings) {
        this.quantificationSettings = quantificationSettings;
    }

    public List<ProteinGroupQuant> getProteinGroupQuants() {
        return proteinGroupQuants;
    }

    public void setProteinGroupQuants(List<ProteinGroupQuant> proteinGroupQuants) {
        this.proteinGroupQuants = proteinGroupQuants;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AnalyticalRun that = (AnalyticalRun) o;

        if (!name.equals(that.name)) {
            return false;
        }
        return !(startDate != null ? !startDate.equals(that.startDate) : that.startDate != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        return result;
    }
}
