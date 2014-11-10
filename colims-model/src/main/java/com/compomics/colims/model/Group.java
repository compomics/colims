package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents a group entity in the database. The table name is
 * "user_group" because "group" is a reserved keyword.
 *
 * @author Niels Hulstaert
 */
@Table(name = "user_group")
@Entity
public class Group extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 3684555329343238970L;

    /**
     * The group name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a name")
    @Length(min = 3, max = 20, message = "Group name length must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * The group description.
     */
    @Basic(optional = true)
    @Length(max = 500, message = "Group description length must be less than {max} characters")
    @Column(name = "description", nullable = true)
    private String description;
    /**
     * The users of the group.
     */
    @ManyToMany(mappedBy = "groups")
    private List<User> users = new ArrayList<>();
    /**
     * The roles of the group.
     */
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "group_has_role",
            joinColumns = {
                @JoinColumn(name = "l_group_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "l_role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Group() {
    }

    /**
     * Constructor.
     *
     * @param name the group name
     */
    public Group(final String name) {
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
