package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Niels Hulstaert
 */
@Table(name = "quantification_settings")
@Entity
public class QuantificationSettings extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 5371850641629946378L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationSettings")
    private List<QuantificationFile> quantificationFiles = new ArrayList<>();
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @JoinColumn(name = "l_quant_engine_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationEngine quantificationEngine;
    @JoinColumn(name = "l_quant_param_settings_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private QuantificationParameters quantificationParameterSettings;

    public List<QuantificationFile> getQuantificationFiles() {
        return quantificationFiles;
    }

    public void setQuantificationFiles(List<QuantificationFile> quantificationFiles) {
        this.quantificationFiles = quantificationFiles;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public QuantificationEngine getQuantificationEngine() {
        return quantificationEngine;
    }

    public void setQuantificationEngine(QuantificationEngine quantificationEngine) {
        this.quantificationEngine = quantificationEngine;
    }

    public QuantificationParameters getQuantificationParameterSettings() {
        return quantificationParameterSettings;
    }

    public void setQuantificationParameterSettings(QuantificationParameters quantificationParameterSettings) {
        this.quantificationParameterSettings = quantificationParameterSettings;
    }

}
