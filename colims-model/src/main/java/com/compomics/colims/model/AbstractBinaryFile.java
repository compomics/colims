/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public class AbstractBinaryFile extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;  
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

    public AbstractBinaryFile(){}
    
    public AbstractBinaryFile(byte[] content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
        
}
