/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AbstractDatabaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "user_name", nullable = false)
    protected String userName;
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)    
    @Column(name = "creation_date", nullable = false, updatable = false)
    protected Date creationDate;
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_date", nullable = false)
    protected Date modificationDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public Date getCreationdate() {
        return creationDate != null ? new Date(creationDate.getTime()) : null;
    }

    public void setCreationdate(Date creationdate) {
        this.creationDate = new Date(creationdate.getTime());
    }

    public Date getModificationdate() {
        return modificationDate != null ? new Date(modificationDate.getTime()) : null;
    }

    public void setModificationdate(Date aModificationdate) {
        modificationDate = new Date(aModificationdate.getTime());
    }
}
