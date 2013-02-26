/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_file")
@Entity
public class QuantificationFile extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "l_quantification_method_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationMethod quantificationMethod;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationFile")
    private List<QuantificationGroup> quantificationGroups;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuantificationMethod getQuantificationMethod() {
        return quantificationMethod;
    }

    public void setQuantificationMethod(QuantificationMethod quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    public List<QuantificationGroup> getQuantificationGroups() {
        return quantificationGroups;
    }

    public void setQuantificationGroups(List<QuantificationGroup> quantificationGroups) {
        this.quantificationGroups = quantificationGroups;
    }
}
