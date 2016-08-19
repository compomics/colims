/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents protein group quantification in the database
 * 
 * @author demet
 */
@Table(name = "protein_group_quant")
@Entity
public class ProteinGroupQuant extends DatabaseEntity {

    private static final long serialVersionUID = -5940113521686521740L;

    /**
     * The ProteinGroup instance of this join entity.
     */
    @JoinColumn(name = "l_protein_group_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProteinGroup proteinGroup;
    
    /**
     * The analytical run instance of this join entity.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AnalyticalRun analyticalRun;
    
    /**
     * The intensity value.
     */
    @Column(name = "intensity")
    private Double intensity;
    
    /**
     * The LFQ intensity value.
     */
    @Column(name = "lfq_intensity")
    private Double lfqIntensity;

    /**
     * The iBAQ value.
     */
    @Column(name = "ibaq")
    private Double ibaq;
    
    /**
     * The MSMS Count value.
     */
    @Column(name = "msms_count")
    private Integer msmsCount;
    
    public ProteinGroup getProteinGroup() {
        return proteinGroup;
    }

    public void setProteinGroup(ProteinGroup proteinGroup) {
        this.proteinGroup = proteinGroup;
    }

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }

    public void setAnalyticalRun(AnalyticalRun analyticalRun) {
        this.analyticalRun = analyticalRun;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public Double getLfqIntensity() {
        return lfqIntensity;
    }

    public void setLfqIntensity(Double lfqIntensity) {
        this.lfqIntensity = lfqIntensity;
    }

    public Double getIbaq() {
        return ibaq;
    }

    public void setIbaq(Double ibaq) {
        this.ibaq = ibaq;
    }

    public Integer getMsmsCount() {
        return msmsCount;
    }

    public void setMsmsCount(Integer msmsCount) {
        this.msmsCount = msmsCount;
    }
   
}
