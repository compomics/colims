package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

/**
 * This parent class of all entity attachments (project, experiment, ...). The
 * attachment is stored as a binary lob in the database and has a file name and
 * a type.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public class BinaryFile extends AuditableDatabaseEntity {
    
    private static final long serialVersionUID = -5581612780987474005L;

    /**
     * The file name.
     */
    @Basic(optional = false)
    @Column(name = "file_name", nullable = false)
    protected String fileName;
    /**
     * The binary file type.
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    protected BinaryFileType binaryFileType;
    /**
     * The content as a binary array. The is stored in the database as a LOB.
     */
    @Basic(optional = false)
    @Lob
    @Column(name = "content", nullable = false)
    protected byte[] content;

    /**
     * No-arg constructor.
     */
    public BinaryFile() {
    }

    /**
     * Constructor.
     *
     * @param content the content as a byte array
     */
    public BinaryFile(final byte[] content) {
        this.content = content;
    }

    public BinaryFileType getBinaryFileType() {
        return binaryFileType;
    }

    public void setBinaryFileType(final BinaryFileType binaryFileType) {
        this.binaryFileType = binaryFileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return fileName + " (" + binaryFileType + ")";
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.fileName);
        hash = 41 * hash + Objects.hashCode(this.binaryFileType);
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
        final BinaryFile other = (BinaryFile) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        return this.binaryFileType == other.binaryFileType;
    }

}
