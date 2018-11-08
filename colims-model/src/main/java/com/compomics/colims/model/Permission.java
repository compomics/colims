package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a permission entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "permission")
@Entity
public class Permission extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 8437203328116083889L;

    /**
     * The permission name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a permission accession.")
    @Length(min = 3, max = 20, message = "Permission name length must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * The permission description.
     */
    @Basic(optional = true)
    @Length(max = 500, message = "Permission description length must be less than {max} characters.")
    @Column(name = "description", nullable = true)
    private String description;
    /**
     * The roles this permission belongs to.
     */
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Permission() {
    }

    /**
     * Constructor.
     *
     * @param name the permission name
     */
    public Permission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
