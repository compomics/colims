package com.compomics.colims.model;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
// group is a reserved SQL keyword
@Table(name = "user_group")
@Entity
public class Group extends AbstractDatabaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a name")
    @Length(min = 5, max = 100, message = "Group name length must be between 5 and 100 characters")
    @Column(name = "title")
    private String name;
    @Basic(optional = true)
    @Length(max = 500, message = "Group description length must be less than 500 characters")
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private List<GroupHasRole> groupHasRoles;

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
}
