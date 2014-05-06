/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protein")
@Entity
@Indexed
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Protein extends DatabaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Lob
    @Basic(optional = false)
    @Column(name = "protein_sequence", nullable = false)
    @Field(index=Index.YES, analyze=Analyze.NO, store=Store.NO)
    private String sequence;    
    @OneToMany(mappedBy = "protein")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    @OneToMany(mappedBy = "mainGroupProtein")
    private List<PeptideHasProtein> peptideHasMainGroupProteins = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "protein")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProteinAccession> proteinAccessions = new ArrayList<>();

    public Protein() {
    }

    public Protein(String sequence) {
        this.sequence = sequence;       
    }
  
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
       
    public List<PeptideHasProtein> getPeptideHasProteins() {
        return peptideHasProteins;
    }

    public void setPeptideHasProteins(List<PeptideHasProtein> peptideHasProteins) {
        this.peptideHasProteins = peptideHasProteins;
    }

    public List<PeptideHasProtein> getPeptideHasMainGroupProteins() {
        return peptideHasMainGroupProteins;
    }

    public void setPeptideHasMainGroupProteins(List<PeptideHasProtein> peptideHasMainGroupProteins) {
        this.peptideHasMainGroupProteins = peptideHasMainGroupProteins;
    }

    public List<ProteinAccession> getProteinAccessions() {
        return proteinAccessions;
    }

    public void setProteinAccessions(List<ProteinAccession> proteinAccessions) {
        this.proteinAccessions = proteinAccessions;
    }          

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.sequence);
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
        final Protein other = (Protein) obj;
        if (!Objects.equals(this.sequence, other.sequence)) {
            return false;
        }       
        return true;
    }

    @Override
    public String toString() {
        return sequence;
    }

}
