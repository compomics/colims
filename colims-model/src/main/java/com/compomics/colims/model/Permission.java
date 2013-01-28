
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "permission")
@Entity
public class Permission extends AbstractDatabaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a permission accession")
    @Length(min = 5, max = 100, message = "Permission name length must be between 5 and 100 characters")
    @Column(name = "name")
    private String name;
    @Basic(optional = true)
    @Length(max = 500, message = "Permission description length must be less than 500 characters")
    @Column(name = "description")
    private String description;
}
