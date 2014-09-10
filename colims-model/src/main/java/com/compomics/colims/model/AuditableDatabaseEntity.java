/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

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
public abstract class AuditableDatabaseEntity extends DatabaseEntity {

    private static final long serialVersionUID = -6390297193482831504L;

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

    public void setUserName(final String username) {
        this.userName = username;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(final Date modificationDate) {
        this.modificationDate = modificationDate;
    }
    
}
