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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "fasta_db")
@Entity
public class FastaDb extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The official name of the fasta db
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB name")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The name of the fasta db file
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB file name")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    /**
     * The fasta db file path
     */
    @Basic(optional = true)
    @Column(name = "file_path", nullable = true)
    protected String filePath;
    /**
     * The taxonomy ID
     */
    @Basic(optional = true)
    @Column(name = "taxonomy_accession", nullable = true)
    private String taxonomyAccession;
    /**
     * The species name
     */
    @Basic(optional = true)
    @Column(name = "species", nullable = true)
    private String species;
    /**
     * The version of the fasta db
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB version")
    @Column(name = "version", nullable = false)
    private String version;
    /**
     * The MD5 checksum of the fasta db
     */
    @Basic(optional = true)
    @Column(name = "md5_checksum", nullable = true)
    private String md5CheckSum;    
    @OneToMany(mappedBy = "fastaDb")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTaxonomyAccession() {
        return taxonomyAccession;
    }

    public void setTaxonomyAccession(String taxonomyAccession) {
        this.taxonomyAccession = taxonomyAccession;
    }      

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }       

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }        

    public String getMd5CheckSum() {
        return md5CheckSum;
    }

    public void setMd5CheckSum(String md5CheckSum) {
        this.md5CheckSum = md5CheckSum;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.taxonomyAccession);
        hash = 37 * hash + Objects.hashCode(this.species);
        hash = 37 * hash + Objects.hashCode(this.version);
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
        final FastaDb other = (FastaDb) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.taxonomyAccession, other.taxonomyAccession)) {
            return false;
        }
        if (!Objects.equals(this.species, other.species)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }    
    
}
