package com.compomics.colims.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Table(name = "quantification_settings")
@Entity
public class QuantificationSettings extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 5371850641629946378L;

    /**
     * The quantification files provided by the quantification engine. Multiple files can be linked to one
     * SearchAndValidationSettings instance.
     */
    @OneToMany(mappedBy = "quantificationSettings")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<QuantificationFile> quantificationFiles = new ArrayList<>();
    /**
     * The analytical run onto which the quantifications were performed.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @OneToOne
    private AnalyticalRun analyticalRun;
    /**
     * The quantification engine used for the searches.
     */
    @JoinColumn(name = "l_quant_engine_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationEngine quantificationEngine;
    /**
     * The quantification parameters.
     */
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

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }

    public void setAnalyticalRun(AnalyticalRun analyticalRun) {
        this.analyticalRun = analyticalRun;
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
