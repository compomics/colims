/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

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
@Table(name = "user", uniqueConstraints =
@UniqueConstraint(columnNames = {"name"}))
@Entity
@TypeDef(name = "encryptedString",
typeClass = EncryptedStringType.class,
parameters = {
    @Parameter(name = "encryptorRegisteredName", value = "jasyptHibernateEncryptor")
})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByFullName", query = "SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an user name")
    @Length(min = 2, max = 20, message = "User name must be between 2 and 20 characters")
    private String name;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a first name")
    @Length(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a last name")
    @Length(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Column(name = "last_name")
    private String lastName;
    @Basic(optional = false)
    @Email(message = "Please insert a valid email address")
    @NotBlank(message = "Please insert an email address")
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "password")
    @NotBlank(message = "Please insert a password")
    @Type(type = "encryptedString")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserHasGroup> userHasGroups = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Project> projects = new ArrayList<>();

    public User() {
    }

    public User(String name) {
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

    public List<UserHasGroup> getUserHasGroups() {
        return userHasGroups;
    }

    public void setUserHasGroups(List<UserHasGroup> userHasGroups) {
        this.userHasGroups = userHasGroups;
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
