/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The name of the fasta db file
     */
    @Basic(optional = false)
    @Column(name = "file_name", nullable = false)
    private String fileName;
    /**
     * The version of the fasta db
     */
    @Basic(optional = true)
    @Column(name = "version", nullable = true)
    private String version;
    /**
     * The MD5 checksum of the fasta db
     */
    @Basic(optional = true)
    @Column(name = "md5_checksum", nullable = true)
    private String md5CheckSum;    
    @OneToMany(mappedBy = "fastaDb")
    private List<SearchParameterSettings> SearchParameterSettings = new ArrayList<>();  

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

    public List<SearchParameterSettings> getSearchParameterSettings() {
        return SearchParameterSettings;
    }

    public void setSearchParameterSettings(List<SearchParameterSettings> SearchParameterSettings) {
        this.SearchParameterSettings = SearchParameterSettings;
    }        
    
}
