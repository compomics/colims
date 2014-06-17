/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "colims_user")
@Entity
public class User extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an user name")
    @Length(min = 3, max = 20, message = "User name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a first name")
    @Length(min = 3, max = 20, message = "First name must be between {min} and {max} characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a last name")
    @Length(min = 3, max = 30, message = "Last name must be between {min} and {max} characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Basic(optional = false)
    @Email(message = "Please insert a valid email address")
    @NotBlank(message = "Please insert an email address")
    @Column(name = "email", nullable = false)
    private String email;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a password")
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
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        this.password = encryptedPassword;
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

    /**
     * Check the user input against the digested password in the database.
     * Returns true if the check was successful.
     *
     * @param plainPassword
     * @return
     */
    public boolean checkPassword(String plainPassword) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        return passwordEncryptor.checkPassword(plainPassword, password);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.name);
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
        final User other = (User) obj;
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
