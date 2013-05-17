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

    @Column(name = "user_name")
    @Basic(optional = false)
    protected String userName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", updatable = false)
    @Basic(optional = false)
    protected Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_date")
    @Basic(optional = false)
    protected Date modificationDate;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
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
