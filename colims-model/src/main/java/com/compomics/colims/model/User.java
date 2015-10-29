package com.compomics.colims.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jasypt.util.password.BasicPasswordEncryptor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an user entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "colims_user")
@Entity
public class User extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -4086933454695081685L;

    /**
     * The user name. This is be used during the application log in.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an user name.")
    @Length(min = 3, max = 20, message = "User name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * The first name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a first name.")
    @Length(min = 3, max = 20, message = "First name must be between {min} and {max} characters.")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    /**
     * The last name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a last name.")
    @Length(min = 3, max = 30, message = "Last name must be between {min} and {max} characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    /**
     * The email address.
     */
    @Basic(optional = false)
    @Email(message = "Please insert a valid email address.")
    @NotBlank(message = "Please insert an email address.")
    @Column(name = "email", nullable = false)
    private String email;
    /**
     * The user password. This is stored encrypted in the database.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a password.")
    @Column(name = "password", nullable = false)
    private String password;
    /**
     * The institution the user belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "l_institution_id", referencedColumnName = "id")
    private Institution institution;
    /**
     * The projects owned by this user.
     */
    @OneToMany(mappedBy = "owner")
    private List<Project> ownedProjects = new ArrayList<>();
    /**
     * The list of projects associated with this user.
     */
    @ManyToMany(mappedBy = "users")
    private List<Project> projects = new ArrayList<>();
    /**
     * The groups the user belongs to.
     */
    @ManyToMany
    @JoinTable(name = "user_has_group",
            joinColumns = {
                    @JoinColumn(name = "l_user_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "l_group_id", referencedColumnName = "id")})
    private List<Group> groups = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public User() {
    }

    /**
     * Constructor.
     *
     * @param name the user name
     */
    public User(final String name) {
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
        this.password = passwordEncryptor.encryptPassword(password);
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
     * Check the user input against the digested password in the database. Returns true if the check was successful.
     *
     * @param plainPassword the plain password String
     * @return did the password check pass
     */
    public boolean checkPassword(String plainPassword) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        return passwordEncryptor.checkPassword(plainPassword, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return name.equals(user.name);

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
