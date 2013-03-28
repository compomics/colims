package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
// group is a reserved SQL keyword
@Table(name = "user_group")
@Entity
public class Group extends AbstractDatabaseEntity implements Comparable<Group> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a name")
    @Length(min = 5, max = 100, message = "Group name length must be between 5 and 100 characters")
    @Column(name = "name")
    private String name;
    @Basic(optional = true)
    @Length(max = 500, message = "Group description length must be less than 500 characters")
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    //@Fetch(FetchMode.JOIN)        
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<GroupHasRole> groupHasRoles = new ArrayList<>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<GroupHasRole> getGroupHasRoles() {
        return groupHasRoles;
    }

    public void setGroupHasRoles(List<GroupHasRole> groupHasRoles) {
        this.groupHasRoles = groupHasRoles;
    }

    /**
     * Convenience method for getting the roles of the group.
     *
     * @return the role list
     */
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        for (GroupHasRole groupHasRole : groupHasRoles) {
            roles.add(groupHasRole.getRole());
        }
        return roles;
    }

    /**
     * Get the grouphasrole with the given role. Return null if nothing was
     * found.
     *
     * @param role the role
     * @return the found grouphasrole
     */
    public GroupHasRole getGroupHasRoleByRole(Role role) {
        GroupHasRole foundGroupHasRole = null;
        for (GroupHasRole groupHasRole : groupHasRoles) {
            if (groupHasRole.getRole().equals(role)) {
                foundGroupHasRole = groupHasRole;
                break;
            }
        }
        return foundGroupHasRole;
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
    public int compareTo(Group o) {
        return name.compareToIgnoreCase(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
