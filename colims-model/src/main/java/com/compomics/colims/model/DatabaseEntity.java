package com.compomics.colims.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This abstract class is the parent class of all database entity classes.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class DatabaseEntity implements Serializable {

    private static final long serialVersionUID = 5095854000948409265L;

    /**
     * The entity ID in the database. This will be set by the persistence
     * provider for new entities. The GenerationType.IDENTITY means that the
     * persistence provider must assign primary keys for the entity using a
     * database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
