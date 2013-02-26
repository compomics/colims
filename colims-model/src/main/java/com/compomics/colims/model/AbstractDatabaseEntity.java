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
    
    @Column(name = "username")
    @Basic(optional = false)
    protected String userName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationdate", updatable = false)
    @Basic(optional = false)
    protected Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modificationdate")
    @Basic(optional = false)
    protected Date modificationDate;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public Date getCreationdate() {
        return new Date(creationDate.getTime());
    }

    public void setCreationdate(Date creationdate) {
        this.creationDate = new Date(creationdate.getTime());
    }

    public Date getModificationdate() {
        return new Date(modificationDate.getTime());
    }

    public void setModificationdate(Date aModificationdate) {
        modificationDate = new Date(aModificationdate.getTime());
    }
}
