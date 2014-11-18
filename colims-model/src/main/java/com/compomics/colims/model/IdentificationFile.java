package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This class represents an identification file entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "identification_file")
@Entity
public class IdentificationFile extends DatabaseEntity {

    private static final long serialVersionUID = -3405917820461550590L;

    /**
     * The identification file name.
     */
    @Basic(optional = false)
    @Column(name = "file_name", nullable = false)
    protected String fileName;
    /**
     * The identification file path.
     */
    @Basic(optional = true)
    @Column(name = "file_path", nullable = true)
    protected String filePath;
    /**
     * The file type.
     */
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = true)
    protected BinaryFileType binaryFileType;
    /**
     * The content as a binary array. The is stored in the database as a LOB.
     */
    @Basic(optional = true, fetch = FetchType.LAZY)
    @Lob
    @Column(name = "content", nullable = true)
    protected byte[] content;
    /**
     * The SearchAndValidationSettings linked to this entity.
     */
    @JoinColumn(name = "l_search_and_val_settings_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValidationSettings searchAndValidationSettings;
    /**
     * The peptides identified in this identification file.
     */
    @OneToMany(mappedBy = "identificationFile")
    private List<Peptide> peptides = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public IdentificationFile() {
    }

    /**
     * Constructor.
     *
     * @param fileName the file name
     * @param filePath the file path
     */
    public IdentificationFile(final String fileName, final String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * Constructor.
     *
     * @param fileName the file name
     * @param filePath the file path
     * @param binaryFileType the file type
     */
    public IdentificationFile(final String fileName, final String filePath, final BinaryFileType binaryFileType) {
        this(fileName, filePath);
        this.binaryFileType = binaryFileType;
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

    public BinaryFileType getBinaryFileType() {
        return binaryFileType;
    }

    public void setBinaryFileType(BinaryFileType binaryFileType) {
        this.binaryFileType = binaryFileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.fileName);
        hash = 79 * hash + Objects.hashCode(this.filePath);
        hash = 79 * hash + Objects.hashCode(this.binaryFileType);
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
        final IdentificationFile other = (IdentificationFile) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        return this.binaryFileType == other.binaryFileType;
    }

}
