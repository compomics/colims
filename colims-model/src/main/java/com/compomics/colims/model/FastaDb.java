package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents a FASTA database in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "fasta_db")
@Entity
public class FastaDb extends DatabaseEntity {

    private static final long serialVersionUID = -7674593202998529863L;

    /**
     * The official name of the FASTA db.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB name")
    @Length(min = 4, max = 100, message = "Name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The name of the FASTA db file.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB file name")
    @Length(min = 5, max = 200, message = "File name must be between {min} and {max} characters")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    /**
     * The FASTA db file path.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB file path")
    @Length(min = 5, max = 250, message = "File path must be between {min} and {max} characters")
    @Column(name = "file_path", nullable = false)
    private String filePath;
    /**
     * The taxonomy ID.
     */
    @Basic(optional = true)
    @Column(name = "taxonomy_accession", nullable = true)
    private String taxonomyAccession = "N/A";
    /**
     * The species name.
     */
    @Basic(optional = true)
    @Column(name = "species", nullable = true)
    private String species = "N/A";
    /**
     * The version of the FASTA db.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB version")
    @Length(min = 3, max = 20, message = "Version must be between {min} and {max} characters")
    @Column(name = "version", nullable = false)
    private String version = "N/A";
    /**
     * The MD5 checksum of the FASTA db.
     */
    @Basic(optional = true)
    @Column(name = "md5_checksum", nullable = true)
    private String md5CheckSum;
    /**
     * The list of search and validation settings that used this FASTA instance
     * for the search.
     */
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
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.fileName);
        hash = 97 * hash + Objects.hashCode(this.filePath);
        hash = 97 * hash + Objects.hashCode(this.taxonomyAccession);
        hash = 97 * hash + Objects.hashCode(this.species);
        hash = 97 * hash + Objects.hashCode(this.version);
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
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        if (!Objects.equals(this.taxonomyAccession, other.taxonomyAccession)) {
            return false;
        }
        if (!Objects.equals(this.species, other.species)) {
            return false;
        }
        return Objects.equals(this.version, other.version);
    }

    @Override
    public String toString() {
        return name + ", accession: " + taxonomyAccession + ", species: " + species + ", version: " + version;
    }

}
