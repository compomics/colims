package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
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
    @OneToMany(mappedBy = "quantificationFile", cascade = CascadeType.ALL)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuantificationFile that = (QuantificationFile) o;

        if (!fileName.equals(that.fileName)) return false;
        if (filePath != null ? !filePath.equals(that.filePath) : that.filePath != null) return false;
        return binaryFileType == that.binaryFileType;

    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        result = 31 * result + (binaryFileType != null ? binaryFileType.hashCode() : 0);
        return result;
    }
}
