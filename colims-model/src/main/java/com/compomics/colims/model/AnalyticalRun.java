package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
     * The storage location of the run, for example the file path of the imported data. This is a free text field.
     */
    @Basic(optional = true)
    @Column(name = "storage_location", nullable = true)
    private String storageLocation;
    /**
     * The spectra of this AnalyticalRun instance.
     */
    @OneToMany(mappedBy = "analyticalRun")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Spectrum> spectrums = new ArrayList<>();
    /**
     * The search and validation settings for this run.
     */
    @OneToOne(mappedBy = "analyticalRun")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    SearchAndValidationSettings searchAndValidationSettings;
    /**
     * The quantification settings for this run.
     */
    @OneToOne(mappedBy = "analyticalRun")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    QuantificationSettings quantificationSettings;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.startDate);
        hash = 59 * hash + Objects.hashCode(this.sample);
        hash = 59 * hash + Objects.hashCode(this.instrument);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.sample, other.sample)) {
            return false;
        }
        return Objects.equals(this.instrument, other.instrument);
    }

    @Override
    public String toString() {
        return name;
    }

}
