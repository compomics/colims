/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public class BinaryFile extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;
  
    @Basic(optional = false)
    @Column(name = "file_name", nullable = false)
    protected String fileName;
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    protected BinaryFileType binaryFileType;
    @Basic(optional = false)
    @Lob
    @Column(name = "content", nullable = false)
    protected byte[] content;

    public BinaryFile(){}
    
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
        if (this.binaryFileType != other.binaryFileType) {
            return false;
        }
        return true;
    }    
        
}