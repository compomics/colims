/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.FragmentIonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "fragment_ion")
@Entity
public class FragmentIon extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "fragment_ion_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FragmentIonType fragmentIonType;
    @Basic(optional = false)
    @Column(name = "fragment_ion_number", nullable = false)
    private Integer fragmentIonNumber;
    @Basic(optional = false)
    @Column(name = "mz_ratio", nullable = false)
    private Double mzRatio;
    @Basic(optional = false)
    @Column(name = "intensity", nullable = false)
    private Double intensity;
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne
    private Spectrum spectrum;
    @ManyToMany    
    @JoinTable(name = "fragment_ion_has_neutral_loss",
            joinColumns = {
        @JoinColumn(name = "l_fragment_ion_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_neutral_loss_id", referencedColumnName = "id")})
    private List<Role> neutralLosses = new ArrayList<>();    

    public FragmentIon() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FragmentIonType getFragmentIonType() {
        return fragmentIonType;
    }

    public void setFragmentIonType(FragmentIonType fragmentIonType) {
        this.fragmentIonType = fragmentIonType;
    }

    public Integer getFragmentIonNumber() {
        return fragmentIonNumber;
    }

    public void setFragmentIonNumber(Integer fragmentIonNumber) {
        this.fragmentIonNumber = fragmentIonNumber;
    }

    public Double getMzRatio() {
        return mzRatio;
    }

    public void setMzRatio(Double mzRatio) {
        this.mzRatio = mzRatio;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }        

    public List<Role> getNeutralLosses() {
        return neutralLosses;
    }

    public void setNeutralLosses(List<Role> neutralLosses) {
        this.neutralLosses = neutralLosses;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.fragmentIonType);
        hash = 97 * hash + Objects.hashCode(this.fragmentIonNumber);
        hash = 97 * hash + Objects.hashCode(this.mzRatio);
        hash = 97 * hash + Objects.hashCode(this.intensity);
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
        final FragmentIon other = (FragmentIon) obj;
        if (this.fragmentIonType != other.fragmentIonType) {
            return false;
        }
        if (!Objects.equals(this.fragmentIonNumber, other.fragmentIonNumber)) {
            return false;
        }
        if (!Objects.equals(this.mzRatio, other.mzRatio)) {
            return false;
        }
        if (!Objects.equals(this.intensity, other.intensity)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FragmentIon{" + "fragmentIonType=" + fragmentIonType + ", fragmentIonNumber=" + fragmentIonNumber + ", mzRatio=" + mzRatio + ", intensity=" + intensity + '}';
    }        
    
}
