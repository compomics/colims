package com.compomics.colims.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This abstract class is subclassed by all database entities that are
 * auditable. This class contains user name, creation and modification fields
 * for this purpose.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AuditableDatabaseEntity extends DatabaseEntity {

    private static final long serialVersionUID = -6390297193482831504L;

    /**
     * The user name of the user that created or updated this entity.
     */
    @Basic(optional = false)
    @Column(name = "user_name", nullable = false)
    protected String userName;
    /**
     * The date and time of insertion in the database.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    protected Date creationDate;
    /**
     * The date and time of the latest update.
     */
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
