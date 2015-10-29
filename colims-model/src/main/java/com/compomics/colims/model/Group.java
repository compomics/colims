package com.compomics.colims.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a group entity in the database. The table name is "user_group" because "group" is a reserved
 * keyword.
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
    @NotBlank(message = "Please insert a name.")
    @Length(min = 3, max = 20, message = "Group name length must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * The group description.
     */
    @Basic(optional = true)
    @Length(max = 500, message = "Group description length must be less than {max} characters.")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return name.equals(group.name);

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
