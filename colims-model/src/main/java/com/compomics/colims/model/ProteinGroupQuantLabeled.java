/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.eav.JsonNodeBinaryType;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents protein group quantification for labeled experiments in the database
 *
 * @author demet
 */
@Table(name = "protein_group_quant_labeled")
@Entity
@TypeDef(
        name = "jsonb-node",
        typeClass = JsonNodeBinaryType.class
)
public class ProteinGroupQuantLabeled extends DatabaseEntity {

    private static final long serialVersionUID = 8300418756381224846L;

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
     * The labels as a json String.
     */
    @Column(name = "labels", nullable = false, length = 750)
    private String labels;

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

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }
}
