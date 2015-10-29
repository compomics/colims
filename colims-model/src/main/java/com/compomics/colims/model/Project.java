package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a project entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "project")
@Entity
public class Project extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -172981262866248897L;

    /**
     * The project title.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a project title.")
    @Length(min = 5, max = 100, message = "Title must be between {min} and {max} characters.")
    @Column(name = "title", nullable = false, unique = true)
    private String title;
    /**
     * The project label.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a project label.")
    @Length(min = 3, max = 20, message = "Label must be between {min} and {max} characters.")
    @Column(name = "label", nullable = false)
    private String label;
    /**
     * The project description. This is a free text field.
     */
    @Basic(optional = true)
    @Length(max = 500, message = "Description must be less than {max} characters.")
    @Column(name = "description", nullable = true)
    private String description;
    /**
     * The project owner. A project can have only one owner.
     */
    @ManyToOne
    @JoinColumn(name = "l_owner_user_id", referencedColumnName = "id", nullable = false)
    private User owner;
    /**
     * The users of this project.
     */
    @ManyToMany
    @JoinTable(name = "project_has_user",
            joinColumns = {
                    @JoinColumn(name = "l_project_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "l_user_id", referencedColumnName = "id")})
    private List<User> users = new ArrayList<>();
    /**
     * The project experiments.
     */
    @OneToMany(mappedBy = "project")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private List<Experiment> experiments = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Project() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (!title.equals(project.title)) return false;
        if (!label.equals(project.label)) return false;
        return !(description != null ? !description.equals(project.description) : project.description != null);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return title;
    }

}
