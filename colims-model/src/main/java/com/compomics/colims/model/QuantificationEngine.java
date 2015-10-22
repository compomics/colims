package com.compomics.colims.model;

import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.enums.QuantificationEngineType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Table(name = "quantification_engine")
@Entity
public class QuantificationEngine extends CvParam {

    private static final long serialVersionUID = 4719894153697846226L;
    private static final String NOT_APPLICABLE = "N/A";

    /**
     * The search engine type.
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuantificationEngineType quantificationEngineType;
    /**
     * The version of the search engine.
     */
    @Basic(optional = true)
    @Column(name = "version", nullable = true)
    private String version;
    @OneToMany(mappedBy = "quantificationEngine")
    private List<QuantificationSettings> quantificationSettingses = new ArrayList<>();

    /**
     * No arg constructor.
     */
    public QuantificationEngine() {
    }

    public QuantificationEngine(QuantificationEngineType quantificationEngineType, String version) {
        super(NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE);
        this.quantificationEngineType = quantificationEngineType;
        this.version = version;
    }

    public QuantificationEngine(final QuantificationEngineType quantificationEngineType, final String version, final String ontology, final String label, final String accession, final String name) {
        super(ontology, label, accession, name);
        this.quantificationEngineType = quantificationEngineType;
        this.version = version;
    }

    /**
     * Constructor that creates a new instance with all fields of the given QuantificationEngine and the given version.
     *
     * @param quantificationEngine the QuantificationEngine to copy
     * @param version              the quantification engine version
     */
    public QuantificationEngine(final QuantificationEngine quantificationEngine, final String version) {
        this(quantificationEngine.getQuantificationEngineType(), version, quantificationEngine.getOntology(), quantificationEngine.getLabel(), quantificationEngine.getAccession(), quantificationEngine.getName());
    }

    public QuantificationEngineType getQuantificationEngineType() {
        return quantificationEngineType;
    }

    public void setQuantificationEngineType(final QuantificationEngineType quantificationEngineType) {
        this.quantificationEngineType = quantificationEngineType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<QuantificationSettings> getQuantificationSettingses() {
        return quantificationSettingses;
    }

    public void setQuantificationSettingses(List<QuantificationSettings> quantificationSettingses) {
        this.quantificationSettingses = quantificationSettingses;
    }
}
