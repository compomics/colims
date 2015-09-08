package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.title);
        hash = 67 * hash + Objects.hashCode(this.label);
        hash = 67 * hash + Objects.hashCode(this.description);
        hash = 67 * hash + Objects.hashCode(this.owner);
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
        final Project other = (Project) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return Objects.equals(this.owner, other.owner);
    }

    @Override
    public String toString() {
        return title;
    }

}
