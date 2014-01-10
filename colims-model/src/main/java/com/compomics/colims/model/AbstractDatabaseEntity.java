/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected Long id;    
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

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }        

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String username) {
        this.userName = username;
    }

    public Date getCreationdate() {
        return creationDate != null ? new Date(creationDate.getTime()) : null;
    }

    public void setCreationdate(final Date creationdate) {
        this.creationDate = new Date(creationdate.getTime());
    }

    public Date getModificationdate() {
        return modificationDate != null ? new Date(modificationDate.getTime()) : null;
    }

    public void setModificationdate(final Date aModificationdate) {
        modificationDate = new Date(aModificationdate.getTime());
    }
}
