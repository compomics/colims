package com.compomics.colims.model;

import javax.persistence.*;

/**
 * @author Niels Hulstaert
 */
@Table(name = "quantification_settings")
@Entity
public class QuantificationSettings extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 5371850641629946378L;

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
    @ManyToOne(cascade = CascadeType.MERGE)
    private QuantificationEngine quantificationEngine;
    
    /**
     * The quantification method cv parameters.
     */
    @JoinColumn(name = "l_quant_method_cv_param", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private QuantificationMethodCvParam quantificationMethodCvParam;

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

    public QuantificationMethodCvParam getQuantificationMethodCvParam() {
        return quantificationMethodCvParam;
    }

    public void setQuantificationMethodCvParam(QuantificationMethodCvParam quantificationMethodCvParam) {
        this.quantificationMethodCvParam = quantificationMethodCvParam;
    }

}
