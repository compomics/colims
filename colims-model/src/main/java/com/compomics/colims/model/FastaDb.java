package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @NotBlank(message = "Please insert a fasta DB name.")
    @Length(min = 4, max = 100, message = "Name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The name of the FASTA db file.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB file name.")
    @Length(min = 5, max = 200, message = "File name must be between {min} and {max} characters.")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    /**
     * The FASTA db file path.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB file path.")
    @Length(min = 5, max = 250, message = "File path must be between {min} and {max} characters.")
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
    @NotBlank(message = "Please insert a fasta DB version.")
    @Length(min = 3, max = 20, message = "Version must be between {min} and {max} characters.")
    @Column(name = "version", nullable = false)
    private String version = "N/A";
    /**
     * The MD5 checksum of the FASTA db.
     */
    @Basic(optional = true)
    @Column(name = "md5_checksum", nullable = true)
    private String md5CheckSum;
    /**
     * The list of search and validation settings that used this FASTA instance for the search.
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
    public String toString() {
        return name + ", accession: " + taxonomyAccession + ", species: " + species + ", version: " + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FastaDb fastaDb = (FastaDb) o;

        if (name != null ? !name.equals(fastaDb.name) : fastaDb.name != null) return false;
        if (fileName != null ? !fileName.equals(fastaDb.fileName) : fastaDb.fileName != null) return false;
        if (filePath != null ? !filePath.equals(fastaDb.filePath) : fastaDb.filePath != null) return false;
        if (taxonomyAccession != null ? !taxonomyAccession.equals(fastaDb.taxonomyAccession) : fastaDb.taxonomyAccession != null)
            return false;
        if (species != null ? !species.equals(fastaDb.species) : fastaDb.species != null) return false;
        if (version != null ? !version.equals(fastaDb.version) : fastaDb.version != null) return false;
        return !(md5CheckSum != null ? !md5CheckSum.equals(fastaDb.md5CheckSum) : fastaDb.md5CheckSum != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        result = 31 * result + (taxonomyAccession != null ? taxonomyAccession.hashCode() : 0);
        result = 31 * result + (species != null ? species.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (md5CheckSum != null ? md5CheckSum.hashCode() : 0);
        return result;
    }
}
