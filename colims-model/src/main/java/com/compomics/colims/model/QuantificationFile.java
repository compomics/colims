/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;

import javax.persistence.CascadeType;
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
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_file")
@Entity
public class QuantificationFile extends DatabaseEntity {
    
    private static final long serialVersionUID = 5714905008883803369L;
    
    /**
     * The quantification file name
     */
    @Basic(optional = false)
    @Column(name = "file_name", nullable = false)
    protected String fileName;
    /**
     * The quantification file path
     */
    @Basic(optional = true)
    @Column(name = "file_path", nullable = true)
    protected String filePath;
    /**
     * The file type
     */
    @Basic(optional = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = true)
    protected BinaryFileType binaryFileType;
    @Basic(optional = true, fetch = FetchType.LAZY)
    @Lob
    @Column(name = "content", nullable = true)
    protected byte[] content;
    @JoinColumn(name = "l_quant_settings_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationSettings quantificationSettings;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationFile")
    private List<Quantification> quantification = new ArrayList<>();

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
    
    public QuantificationSettings getQuantificationSettings() {
        return quantificationSettings;
    }

    public void setQuantificationSettings(QuantificationSettings quantificationSettings) {
        this.quantificationSettings = quantificationSettings;
    }

    public List<Quantification> getQuantification() {
        return quantification;
    }

    public void setQuantification(List<Quantification> quantification) {
        this.quantification = quantification;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.fileName);
        hash = 47 * hash + Objects.hashCode(this.filePath);
        hash = 47 * hash + Objects.hashCode(this.binaryFileType);
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
        final QuantificationFile other = (QuantificationFile) obj;
        if (!Objects.equals(this.fileName, other.fileName)) {
            return false;
        }
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        if (this.binaryFileType != other.binaryFileType) {
            return false;
        }
        return true;
    }            
    
}
