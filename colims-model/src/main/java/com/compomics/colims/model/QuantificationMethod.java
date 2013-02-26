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
@Table(name = "quantification_method")
@Entity
public class QuantificationMethod extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationMethod")
    private List<QuantificationFile> quantificationFiles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public List<QuantificationFile> getQuantificationFiles() {
        return quantificationFiles;
    }

    public void setQuantificationFiles(List<QuantificationFile> quantificationFiles) {
        this.quantificationFiles = quantificationFiles;
    }
}
