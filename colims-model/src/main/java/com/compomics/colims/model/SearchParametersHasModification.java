package com.compomics.colims.model;

import com.compomics.colims.model.enums.ModificationType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_params_has_modification")
@Entity
public class SearchParametersHasModification extends DatabaseEntity {

    private static final long serialVersionUID = -4450629780383189785L;

    /**
     * The modification type (fixed, variable).
     */
    @Basic(optional = true)
    @Column(name = "modification_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ModificationType modificationType;
    /**
     * The modification residue(s)
     */
    @Basic(optional = true)
    @Column(name = "residues", nullable = true)
    private String residues;
    /**
     * The SearchParameters instance of this join entity.
     */
    @JoinColumn(name = "l_search_parameters_id", referencedColumnName = "id")
    @ManyToOne
    private SearchParameters searchParameters;
    /**
     * The SearchModification instance of this join entity.
     */
    @JoinColumn(name = "l_search_modification_id", referencedColumnName = "id")
    @ManyToOne
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private SearchModification searchModification;

    /**
     * No-arg constructor.
     */
    public SearchParametersHasModification() {
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    public SearchModification getSearchModification() {
        return searchModification;
    }

    public void setSearchModification(SearchModification searchModification) {
        this.searchModification = searchModification;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(final ModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public String getResidues() { return residues; }

    public void setResidues(String residues) { this.residues = residues; }
}
