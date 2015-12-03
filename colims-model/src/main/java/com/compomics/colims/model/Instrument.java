package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an instrument entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument")
@Entity
public class Instrument extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -7111402094194930375L;

    /**
     * The instrument in-house name. This field is used to make a distinction between instruments of the same type
     * within on lab.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument name.")
    @Length(min = 3, max = 30, message = "Name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * The mandatory type CV term that represents the instrument type.
     */
    @NotNull(message = "An instrument must have a type.")
    @ManyToOne
    @JoinColumn(name = "l_type_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvParam type;
    /**
     * The mandatory instrument source CV term.
     */
    @NotNull(message = "An instrument must have a source.")
    @ManyToOne
    @JoinColumn(name = "l_source_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvParam source;
    /**
     * The mandatory detector CV term.
     */
    @NotNull(message = "An instrument must have a detector.")
    @ManyToOne
    @JoinColumn(name = "l_detector_cv_id", referencedColumnName = "id", nullable = false)
    private InstrumentCvParam detector;
    /**
     * The list of runs executed on this instrument.
     */
    @OneToMany(mappedBy = "instrument")
    private List<AnalyticalRun> analyticalRuns = new ArrayList<>();
    /**
     * The list of analyzer CV terms. There has to be at least one analyzer.
     */
    @NotEmpty(message = "An instrument must have at least one analyzer.")
    @ManyToMany
    @JoinTable(name = "instrument_has_analyzer",
            joinColumns = {
                    @JoinColumn(name = "l_instrument_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "l_instrument_cv_param_id", referencedColumnName = "id")})
    private List<InstrumentCvParam> analyzers = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Instrument() {
    }

    /**
     * Constructor.
     *
     * @param name the instrument name
     */
    public Instrument(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstrumentCvParam getType() {
        return type;
    }

    public void setType(InstrumentCvParam type) {
        this.type = type;
    }

    public InstrumentCvParam getSource() {
        return source;
    }

    public void setSource(InstrumentCvParam source) {
        this.source = source;
    }

    public InstrumentCvParam getDetector() {
        return detector;
    }

    public void setDetector(InstrumentCvParam detector) {
        this.detector = detector;
    }

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
    }

    public List<InstrumentCvParam> getAnalyzers() {
        return analyzers;
    }

    public void setAnalyzers(List<InstrumentCvParam> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instrument that = (Instrument) o;

        if (!name.equals(that.name)) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (detector != null ? !detector.equals(that.detector) : that.detector != null) return false;
        return !(analyzers != null ? !analyzers.equals(that.analyzers) : that.analyzers != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (detector != null ? detector.hashCode() : 0);
        result = 31 * result + (analyzers != null ? analyzers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " [" + type.getName() + "]";
    }

}
