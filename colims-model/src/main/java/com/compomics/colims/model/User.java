/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jasypt.hibernate4.type.EncryptedStringType;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "user")
@Entity
@TypeDef(name = "encryptedString",
        typeClass = EncryptedStringType.class,
        parameters = {
    @Parameter(name = "encryptorRegisteredName", value = "jasyptHibernateEncryptor")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an user name")
    @Length(min = 2, max = 20, message = "User name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a first name")
    @Length(min = 2, max = 20, message = "First name must be between {min} and {max} characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a last name")
    @Length(min = 2, max = 30, message = "Last name must be between {min} and {max} characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Basic(optional = false)
    @Email(message = "Please insert a valid email address")
    @NotBlank(message = "Please insert an email address")
    @Column(name = "email", nullable = false)
    private String email;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a password")
    @Type(type = "encryptedString")
    @Column(name = "password", nullable = false)
    private String password;
    @ManyToOne
    @JoinColumn(name = "l_institution_id", referencedColumnName = "id")
    private Institution institution;
    @OneToMany(mappedBy = "owner")
    private List<Project> ownedProjects = new ArrayList<>();
    @ManyToMany(mappedBy = "users")
    private List<Project> projects = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "user_has_group",
            joinColumns = {
        @JoinColumn(name = "l_user_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_group_id", referencedColumnName = "id")})
    private List<Group> groups = new ArrayList<>();

    public User() {
    }

    public User(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public List<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public void setOwnedProjects(List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }        

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
