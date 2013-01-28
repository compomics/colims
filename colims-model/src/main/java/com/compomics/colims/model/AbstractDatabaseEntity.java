/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

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
        return creationDate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationDate = creationdate;
    }

    public Date getModificationdate() {
        return modificationDate;
    }

    public void setModificationdate(Date aModificationdate) {
        modificationDate = aModificationdate;
    }
}
