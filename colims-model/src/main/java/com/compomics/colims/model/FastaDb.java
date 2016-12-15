package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * The databaseName of the FASTA db.
     */
    @Basic(optional = false)
    @Column(name = "database_name", nullable = false)
    private String databaseName;
    /**
     * The version of the FASTA db.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a fasta DB version. If you do not know, type N/A")
    @Length(min = 3, max = 20, message = "Version must be between {min} and {max} characters.")
    @Column(name = "version", nullable = false)
    private String version;
    /**
     * The MD5 checksum of the FASTA db.
     */
    @Basic(optional = true)
    @Column(name = "md5_checksum", nullable = true)
    private String md5CheckSum;
    /**
     * The regular expression for parsing the FASTA headers to extract the
     * protein accession.
     */
    @Basic(optional = true)
    @Column(name = "header_parse_rule", nullable = true)
    private String headerParseRule;
    /**
     * The species taxonomy CV term.
     */
    @ManyToOne
    @JoinColumn(name = "l_taxonomy_cv_id", referencedColumnName = "id", nullable = true)
    private TaxonomyCvParam taxonomy;
    /**
     * The SearchSettingsHasFastaDb instances from the join table between the
     * search and validation settings and FASTA databases.
     */
    @OneToMany(mappedBy = "fastaDb", cascade = CascadeType.ALL)
    private List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs = new ArrayList<>();

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public String getHeaderParseRule() {
        return headerParseRule;
    }

    public void setHeaderParseRule(String headerParseRule) {
        this.headerParseRule = headerParseRule;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public TaxonomyCvParam getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(TaxonomyCvParam taxonomy) {
        this.taxonomy = taxonomy;
    }

    public List<SearchSettingsHasFastaDb> getSearchSettingsHasFastaDbs() {
        return searchSettingsHasFastaDbs;
    }

    public void setSearchSettingsHasFastaDbs(List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs) {
        this.searchSettingsHasFastaDbs = searchSettingsHasFastaDbs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.fileName);
        hash = 89 * hash + Objects.hashCode(this.filePath);
        hash = 89 * hash + Objects.hashCode(this.databaseName);
        hash = 89 * hash + Objects.hashCode(this.version);
        hash = 89 * hash + Objects.hashCode(this.md5CheckSum);
        hash = 89 * hash + Objects.hashCode(this.headerParseRule);
        hash = 89 * hash + Objects.hashCode(this.taxonomy);
        hash = 89 * hash + Objects.hashCode(this.searchSettingsHasFastaDbs);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
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
        if (!Objects.equals(this.databaseName, other.databaseName)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.md5CheckSum, other.md5CheckSum)) {
            return false;
        }
        if (!Objects.equals(this.headerParseRule, other.headerParseRule)) {
            return false;
        }
        if (!Objects.equals(this.taxonomy, other.taxonomy)) {
            return false;
        }
        return Objects.equals(this.searchSettingsHasFastaDbs, other.searchSettingsHasFastaDbs);
    }

    @Override
    public String toString() {
        String taxonomyAccession = (taxonomy != null) ? taxonomy.getAccession() : "none";
        return name + ", accession: " + taxonomyAccession + ", version: " + version;
    }

}
