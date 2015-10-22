package com.compomics.colims.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This abstract class is the parent class of all database entity classes.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class DatabaseEntity implements Serializable {

    private static final long serialVersionUID = 5095854000948409265L;

    /**
     * The entity ID in the database. This will be set by the persistence provider for new entities. The
     * GenerationType.IDENTITY means that the persistence provider must assign primary keys for the entity using a
     * database identity column.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected Long id;

    /**
     * No-arg constructor.
     */
    public DatabaseEntity() {
    }

    /**
     * Constructor.
     *
     * @param id the entity
     */
    protected DatabaseEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseEntity that = (DatabaseEntity) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
